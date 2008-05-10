/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.annotations.Inject;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class TypedFieldInjectorTestCase {

    public static class Helicopter {
        private PogoStick pogo;
    }


    public static class PogoStick {
    }

    @Test public void testFieldInjectionByType() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new TypedFieldInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(),
                new NullLifecycleStrategy(),
                Integer.class.getName() + " " + PogoStick.class.getName() + " " + Float.class.getName()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }


}