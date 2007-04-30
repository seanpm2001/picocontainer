/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PermissionCollection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoClassNotFoundException;
import org.picocontainer.defaults.CustomPermissionsURLClassLoader;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;

/**
 * A base class for NanoPicoContainers. As well as the functionality indicated by the interface it
 * implements, extenders of this class will have named child component capability.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractNanoContainer extends AbstractDelegatingMutablePicoContainer implements NanoPicoContainer, Serializable {

    private static transient Map<String, String> primitiveNameToBoxedName = new HashMap<String, String>();
    static {
        primitiveNameToBoxedName.put("int", Integer.class.getName());
        primitiveNameToBoxedName.put("byte", Byte.class.getName());
        primitiveNameToBoxedName.put("short", Short.class.getName());
        primitiveNameToBoxedName.put("long", Long.class.getName());
        primitiveNameToBoxedName.put("float", Float.class.getName());
        primitiveNameToBoxedName.put("double", Double.class.getName());
        primitiveNameToBoxedName.put("boolean", Boolean.class.getName());
    }


    private transient List<ClassPathElement> classPathElements = new ArrayList<ClassPathElement>();
    private transient ClassLoader parentClassLoader;

    private transient ClassLoader componentClassLoader;
    private transient boolean componentClassLoaderLocked;



    protected Map<String,PicoContainer> namedChildContainers = new HashMap<String,PicoContainer>();


    protected AbstractNanoContainer(MutablePicoContainer delegate, ClassLoader classLoader) {
        super(delegate);
        parentClassLoader = classLoader;
    }

    public final Object getComponent(Object componentKeyOrType) throws PicoException {

        Object instance = getDelegate().getComponent(componentKeyOrType);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter componentAdapter = null;
        if (componentKeyOrType.toString().startsWith("*")) {
            String candidateClassName = componentKeyOrType.toString().substring(1);
            Collection<ComponentAdapter> cas = getComponentAdapters();
            for (ComponentAdapter ca : cas) {
                Object key = ca.getComponentKey();
                if (key instanceof Class && candidateClassName.equals(((Class) key).getName())) {
                    componentAdapter = ca;
                    break;
                }
            }
        }
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return getComponentInstanceFromChildren(componentKeyOrType);
        }
    }

    private Object getComponentInstanceFromChildren(Object componentKey) {
        String componentKeyPath = componentKey.toString();
        int ix = componentKeyPath.indexOf('/');
        if (ix != -1) {
            String firstElement = componentKeyPath.substring(0, ix);
            String remainder = componentKeyPath.substring(ix + 1, componentKeyPath.length());
            Object o = getNamedContainers().get(firstElement);
            if (o != null) {
                MutablePicoContainer child = (MutablePicoContainer) o;
                return child.getComponent(remainder);
            }
        }
        return null;
    }

    public final MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.size());
    }

    /**
     * Makes a child container with the same basic characteristics of <tt>this</tt>
     * object (ComponentAdapterFactory, PicoContainer type, LifecycleManager, etc)
     * @param name the name of the child container
     * @return The child MutablePicoContainer
     */
    public MutablePicoContainer makeChildContainer(String name) {
        AbstractNanoContainer child = createChildContainer();
        MutablePicoContainer parentDelegate = getDelegate();
        parentDelegate.removeChildContainer(child.getDelegate());
        parentDelegate.addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

    protected abstract AbstractNanoContainer createChildContainer();

    public boolean removeChildContainer(PicoContainer child) {
        boolean result = getDelegate().removeChildContainer(child);
        Iterator children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry e = (Map.Entry) children.next();
            PicoContainer pc = (PicoContainer) e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
        return result;
    }

    protected final Map getNamedContainers() {
        return namedChildContainers;
    }

    public Object getComponent(String componentType) {
        try {
            Class compType = getComponentClassLoader().loadClass(componentType);
            return super.getComponent(compType);
        } catch (ClassNotFoundException e) {
            // ugly hack as Rhino cannot differentiate between getComponent(String) and getComponent(Object) and
            // it is likely that there is no way to cast between the two.
            // moot if we merge all the getComponent() classes ?
            Object retval = super.getComponent(componentType);
            if (retval == null) {
                throw new NanoContainerMarkupException("Can't resolve class as type '" + componentType + "'");
            } else {
                return retval;
            }
        }
    }

    public ClassPathElement addClassLoaderURL(URL url) {
        if (componentClassLoaderLocked) throw new IllegalStateException("ClassLoader URLs cannot be added once this instance is locked");

        ClassPathElement classPathElement = new ClassPathElement(url);
        classPathElements.add(classPathElement);
        return classPathElement;
    }

    public ComponentAdapter registerComponent(Object componentImplementationOrInstance) {
        if(componentImplementationOrInstance instanceof ClassName) {
            String className = ((ClassName) componentImplementationOrInstance).className;
            return super.registerComponent(loadClass(className));
        }
        return super.registerComponent(componentImplementationOrInstance);
    }

    public ComponentAdapter registerComponent(Object key, Object componentImplementationOrInstance, Parameter... parameters) {
        if (key instanceof ClassName) {
            key = loadClass(((ClassName) key).getClassName());
        }
        if (componentImplementationOrInstance instanceof ClassName) {
            componentImplementationOrInstance = loadClass(((ClassName) componentImplementationOrInstance).getClassName());
        }
        return super.registerComponent(key,componentImplementationOrInstance, parameters);
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            componentClassLoaderLocked = true;
            componentClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new CustomPermissionsURLClassLoader(getURLs(classPathElements), makePermissions(), parentClassLoader);
                }
            });
        }
        return componentClassLoader;
    }

    public boolean addChildContainer(PicoContainer child) {
        boolean result = getDelegate().addChildContainer(child);


        namedChildContainers.put("containers" + namedChildContainers.size(), child);
        return result;
    }

    public void addChildContainer(String name, PicoContainer child) {

        super.addChildContainer(child);

        namedChildContainers.put(name, child);
    }

    private Class loadClass(final String className) {
        ClassLoader classLoader = getComponentClassLoader();
        String cn = getClassName(className);
        try {
            return classLoader.loadClass(cn);
        } catch (ClassNotFoundException e) {
            throw new PicoClassNotFoundException(cn, e);
        }
    }

    private Map<URL, PermissionCollection> makePermissions() {
        Map<URL, PermissionCollection> permissionsMap = new HashMap<URL, PermissionCollection>();
        for (ClassPathElement cpe : classPathElements) {
            PermissionCollection permissionCollection = cpe.getPermissionCollection();
            permissionsMap.put(cpe.getUrl(), permissionCollection);
        }
        return permissionsMap;
    }

    private URL[] getURLs(List<ClassPathElement> classPathElemelements) {
        final URL[] urls = new URL[classPathElemelements.size()];
        for(int i = 0; i < urls.length; i++) {
            urls[i] = (classPathElemelements.get(i)).getUrl();
        }
        return urls;
    }

    private static String getClassName(String primitiveOrClass) {
        String fromMap = primitiveNameToBoxedName.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }



}
