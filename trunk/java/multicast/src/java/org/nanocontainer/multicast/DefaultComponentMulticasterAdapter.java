/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.multicast;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;

import java.io.Serializable;
import java.util.List;

/**
 * A default implementation of the {@link ComponentMulticasterAdapter} interface.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class DefaultComponentMulticasterAdapter implements ComponentMulticasterAdapter, Serializable {
    private final ComponentMulticasterFactory factory;

    public DefaultComponentMulticasterAdapter() {
        this(new DefaultComponentMulticasterFactory());
    }

    public DefaultComponentMulticasterAdapter(ComponentMulticasterFactory factory) {
        this.factory = factory;
    }

    public Object getComponentMulticaster(PicoContainer picoContainer, boolean callInInstantiationOrder, InvocationInterceptor invocationInterceptor) throws PicoException {
        List componentsToMulticast = picoContainer.getComponentInstances();
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder, invocationInterceptor, new MulticastInvoker());
    }

    public Object getComponentMulticaster(PicoContainer picoContainer, boolean callInInstantiationOrder) {
        return getComponentMulticaster(picoContainer, callInInstantiationOrder, new NullInvocationInterceptor());
    }
}
