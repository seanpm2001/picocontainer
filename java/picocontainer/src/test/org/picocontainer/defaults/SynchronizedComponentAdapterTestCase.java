/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapterTestCase extends TestCase {
    private Runner runner1;
    private Runner runner2;

    class Runner implements Runnable {
        public CyclicDependencyException exception;
        public Blocker blocker;
        private PicoContainer pico;

        public Runner(PicoContainer pico) {
            this.pico = pico;
        }

        public void run() {
            try {
                blocker = (Blocker) pico.getComponentInstance("key");
            } catch (CyclicDependencyException e) {
                exception = e;
            }
        }
    }

    public static class Blocker {
        public Blocker() throws InterruptedException {
            // Yes I know sleeping in tests is bad, but it's also simple :-)
            Thread.sleep(3000);
        }
    }

    private void initTest(ComponentAdapter componentAdapter) throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(componentAdapter);

        runner1 = new Runner(pico);
        runner2 = new Runner(pico);

        Thread racer1 = new Thread(runner1);
        Thread racer2 = new Thread(runner2);

        racer1.start();
        racer2.start();

        racer1.join();
        racer2.join();
    }

    public void testRaceConditionIsHandledBySynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter("key", Blocker.class));
        SynchronizedComponentAdapter synchronizedComponentAdapter = new SynchronizedComponentAdapter(componentAdapter);
        initTest(synchronizedComponentAdapter);

        assertNull(runner1.exception);
        assertNull(runner2.exception);

        assertNotNull(runner1.blocker);
        assertSame(runner1.blocker, runner2.blocker);
    }

    public void testRaceConditionIsNotHandledWithoutSynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter("key", Blocker.class));
        initTest(componentAdapter);

        assertTrue(runner1.exception != null || runner2.exception != null);
    }
}
