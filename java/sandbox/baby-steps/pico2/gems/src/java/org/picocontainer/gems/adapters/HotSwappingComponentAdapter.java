/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * This addComponent addAdapter makes it possible to hide the implementation of a real subject (behind a proxy). If the key of the
 * addComponent is of type {@link Class} and that class represents an interface, the proxy will only implement the interface
 * represented by that Class. Otherwise (if the key is something else), the proxy will implement all the interfaces of the
 * underlying subject. In any case, the proxy will also implement {@link com.thoughtworks.proxy.toys.hotswap.Swappable}, making
 * it possible to swap out the underlying subject at runtime. <p/> <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link org.picocontainer.adapters.CachingComponentAdapter} around this one.
 * </em>
 * 
 * @author Paul Hammant
 * @version $Revision$
 */
public class HotSwappingComponentAdapter extends ImplementationHidingComponentAdapter {

    private final Swappable swappable = new Swappable();
    private Object instance;

    public HotSwappingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    protected Swappable getSwappable() {
        return swappable;
    }

    public Object swapRealInstance(Object instance) {
        return swappable.swap(instance);
    }

    public Object getRealInstance() {
        return swappable.getInstance();
    }


    public Object getComponentInstance(PicoContainer container) {
        synchronized(swappable) {
            if (instance == null) {
                instance = super.getComponentInstance(container);
            }
        }
        return instance;
    }
}
