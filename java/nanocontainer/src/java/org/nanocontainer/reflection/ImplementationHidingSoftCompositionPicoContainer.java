/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is a MutablePicoContainer that supports soft composition and hides implementations where it can.
 *
 * In terms of implementation it adopts the behaviour of ImplementationHidingPicoContainer
 * and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingSoftCompositionPicoContainer extends AbstractSoftCompositionPicoContainer implements SoftCompositionPicoContainer, Serializable {

    private final ImplementationHidingSoftCompositionPicoContainer.InnerMutablePicoContainer delegate;

    // Serializable cannot be cascaded into DefaultReflectionContainerAdapter's referenced classes
    // need to implement custom Externalisable regime.
    private transient ReflectionContainerAdapter reflectionAdapter;

    public ImplementationHidingSoftCompositionPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        delegate = new ImplementationHidingSoftCompositionPicoContainer.InnerMutablePicoContainer(caf, parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
    }

    public ImplementationHidingSoftCompositionPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        delegate = new ImplementationHidingSoftCompositionPicoContainer.InnerMutablePicoContainer(new DefaultComponentAdapterFactory(), parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
    }

    public ImplementationHidingSoftCompositionPicoContainer(PicoContainer pc) {
        this(ImplementationHidingSoftCompositionPicoContainer.class.getClassLoader(), pc);
    }

    public ImplementationHidingSoftCompositionPicoContainer(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ImplementationHidingSoftCompositionPicoContainer() {
        this(ImplementationHidingSoftCompositionPicoContainer.class.getClassLoader(), null);
    }

    protected Map getNamedContainers() {
        return delegate.getNamedContainers();
    }

    public Object getComponentInstanceFromDelegate(Object componentKey) {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public Object getComponentInstanceOfType(String componentType) {
        return reflectionAdapter.getComponentInstanceOfType(componentType);
    }

    public List getComponentInstances() {
        return delegate.getComponentInstances();
    }

    public synchronized PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        return delegate.getComponentAdapterOfType(componentType);
    }

    public Collection getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public List getComponentAdaptersOfType(Class componentType) {
        return delegate.getComponentAdaptersOfType(componentType);
    }

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public void dispose() {
        delegate.dispose();
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentImplementation);
    }

    public ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentInstance);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentKey, componentInstance);
    }

    public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        return delegate.registerComponent(componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return delegate.unregisterComponent(componentKey);
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        return delegate.unregisterComponentByInstance(componentInstance);
    }

    public MutablePicoContainer makeChildContainer() {
        return makeChildContainer(null);
    }

    public MutablePicoContainer makeChildContainer(String name) {
        ClassLoader currentClassloader = reflectionAdapter.getComponentClassLoader();
        ImplementationHidingSoftCompositionPicoContainer pc = new ImplementationHidingSoftCompositionPicoContainer(currentClassloader, this);
        delegate.addChildContainer(name, pc);
        return pc;
    }

    public void addChildContainer(PicoContainer child) {
        delegate.addChildContainer(child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        delegate.addChildContainer(name, child);
    }

    public void removeChildContainer(PicoContainer child) {
        delegate.removeChildContainer(child);
    }

    public void accept(PicoVisitor visitor, Class componentType, boolean visitInInstantiationOrder) {
        delegate.accept(visitor, componentType, visitInInstantiationOrder);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    // --------------------

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(componentImplementationClassName);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName);
    }
    
    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, Parameter[] parameters) throws ClassNotFoundException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public void addClassLoaderURL(URL url) {
        reflectionAdapter.addClassLoaderURL(url);
    }

    //TODO Should this method be the ReflectionContainerAdapter interface only?
    public MutablePicoContainer getPicoContainer() {
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        return reflectionAdapter.getComponentClassLoader();
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    private class InnerMutablePicoContainer extends ImplementationHidingPicoContainer {
        public InnerMutablePicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
            super(componentAdapterFactory, parent);
        }
        public Map getNamedContainers() {
            return super.getNamedContainers();
        }

        public boolean equals(Object obj) {
            if (obj.equals(ImplementationHidingSoftCompositionPicoContainer.this)) {
                return true;
            }
            return super.equals(obj);
        }
    }
}
