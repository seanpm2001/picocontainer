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

import org.nanocontainer.NanoPicoContainer;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NullComponentMonitor;

import java.io.Serializable;

/**
 * This is a MutablePicoContainer that supports soft composition and hides implementations where it can.
 * <p/>
 * In terms of implementation it adopts the behaviour of ImplementationHidingPicoContainer
 * and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @version $Revision$
 *          <p/>
 *          <p/>
 *          This is not not one for one advice - PH
 * @ not deprecated Use {@link org.nanocontainer.DefaultNanoContainer)
 * constructed with a {@link org.picocontainer.alternatives.ImplementationHidingPicoContainer}.
 * Suggest something with DefaultNanoPicoContainer and its ctor.
 */
public class ImplementationHidingNanoPicoContainer extends AbstractNanoPicoContainer implements NanoPicoContainer, Serializable {
    private ComponentMonitor componentMonitor;

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(new ImplementationHidingPicoContainer(caf, parent), classLoader);
        this.componentMonitor = componentMonitor;
    }

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        super(new ImplementationHidingPicoContainer(new DefaultComponentAdapterFactory(NullComponentMonitor.getInstance()), parent), classLoader);
    }

    public ImplementationHidingNanoPicoContainer(PicoContainer pc) {
        this(ImplementationHidingNanoPicoContainer.class.getClassLoader(), pc);
    }

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ImplementationHidingNanoPicoContainer() {
        this(ImplementationHidingNanoPicoContainer.class.getClassLoader(), null);
    }

    public MutablePicoContainer makeChildContainer(String name) {
        ClassLoader currentClassloader = container.getComponentClassLoader();
        ImplementationHidingNanoPicoContainer child = new ImplementationHidingNanoPicoContainer(currentClassloader, this);
        getDelegate().addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

}
