/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVerificationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private PicoContainer delegate;
    private PicoContainer parent;

    public ImmutablePicoContainer(PicoContainer pc) {
        this.delegate = pc;
    }

    public Object getComponentInstance(Object componentKey) {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public List getComponentInstances() {
        return delegate.getComponentInstances();
    }

    public synchronized PicoContainer getParent() {
        if (parent == null) {
            PicoContainer par = delegate.getParent();
            if (par instanceof ImmutablePicoContainer) {
                parent = par;
            } else {
                parent = new ImmutablePicoContainer((MutablePicoContainer) par);
            }
        }
        return parent;
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        ComponentAdapter componentAdapter = delegate.getComponentAdapter(componentKey);
        return new ImmutableComponentAdapter(componentAdapter);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        ComponentAdapter componentAdapter = delegate.getComponentAdapterOfType(componentType);
        return new ImmutableComponentAdapter(componentAdapter);
    }

    public Collection getComponentAdapters() {
        List list = new ArrayList();
        Collection componentAdapters = delegate.getComponentAdapters();
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter ca = (ComponentAdapter) iterator.next();
            list.add(new ImmutableComponentAdapter(ca));
        }
        return list;
    }

    public List getComponentAdaptersOfType(Class componentType) {
        List list = new ArrayList();
        List componentAdaptersOfType = delegate.getComponentAdaptersOfType(componentType);
        for (Iterator iterator = componentAdaptersOfType.iterator(); iterator.hasNext();) {
            ComponentAdapter ca = (ComponentAdapter) iterator.next();
            list.add(new ImmutableComponentAdapter(ca));
        }
        return list;
    }

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        throw new UnsupportedOperationException("This container is immutable, addOrderedComponentAdapter() is not allowed");
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    public void start() {
        throw new UnsupportedOperationException("This container is immutable, start() is not allowed");
    }

    public void stop() {
        throw new UnsupportedOperationException("This container is immutable, stop() is not allowed");
    }

    public void dispose() {
        throw new UnsupportedOperationException("This container is immutable, dispose() is not allowed");
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ImmutablePicoContainer) {
            return obj.hashCode() == delegate.hashCode();
        }
        return false;
    }
}
