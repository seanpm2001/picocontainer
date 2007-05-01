/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.componentadapters;

import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.componentadapters.CachingComponentAdapterFactory;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

/**
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class CachingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new CachingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory());
    }

    public void testContainerReturnsSameInstanceEachCall() {
        picoContainer.component(Touchable.class, SimpleTouchable.class);
        Touchable t1 = (Touchable) picoContainer.getComponent(Touchable.class);
        Touchable t2 = (Touchable) picoContainer.getComponent(Touchable.class);
        assertSame(t1, t2);
    }
}