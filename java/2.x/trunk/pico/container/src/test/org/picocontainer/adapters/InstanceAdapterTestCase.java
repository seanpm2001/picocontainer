/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.Map;

/**
 * Test the InstanceAdapter.
 * 
 * @author J&ouml;rg Schaible
 */
public final class InstanceAdapterTestCase
    extends AbstractComponentAdapterTestCase {

    public void testComponentAdapterReturnsSame() {
        final Touchable touchable = new SimpleTouchable();
        final ComponentAdapter componentAdapter = new InstanceAdapter(Touchable.class, touchable, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
        assertSame(touchable, componentAdapter.getComponentInstance(null));
    }

    public void testDefaultLifecycleStrategy() {
        LifecycleComponent component = new LifecycleComponent();
        InstanceAdapter adapter =
            new InstanceAdapter(LifecycleComponent.class, component, new StartableLifecycleStrategy(NullComponentMonitor.getInstance()),
                                                                        NullComponentMonitor.getInstance());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        assertEquals("start>stop>dispose>", component.buffer.toString());
        adapter.start(component);
        adapter.stop(component);
        adapter.dispose(component);
        assertEquals("start>stop>dispose>start>stop>dispose>", component.buffer.toString());
    }

    private static final class LifecycleComponent implements Startable, Disposable {
        final StringBuffer buffer = new StringBuffer();

        public void start() {
            buffer.append("start>");
        }

        public void stop() {
            buffer.append("stop>");
        }

        public void dispose() {
            buffer.append("dispose>");
        }
    }

    public void testCustomLifecycleCanBeInjected() {
        NullLifecycle component = new NullLifecycle();
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        InstanceAdapter adapter = new InstanceAdapter(NullLifecycle.class, component, strategy, NullComponentMonitor.getInstance());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        assertEquals("<start<stop<dispose", strategy.recording());
        adapter.start(component);
        adapter.stop(component);
        adapter.dispose(component);
        assertEquals("<start<stop<dispose<start<stop<dispose", strategy.recording());
    }

    public void testComponentAdapterCanIgnoreLifecycle() {
        final Touchable touchable = new SimpleTouchable();
        InstanceAdapter adapter = new InstanceAdapter(Touchable.class, touchable, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        adapter.start(touchable);
        adapter.stop(touchable);
        adapter.dispose(touchable);
    }

    public void testGuardAgainstNullInstance() {
        try {
            new InstanceAdapter(Map.class, null, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
            fail("should have barfed");
        } catch (NullPointerException e) {
            assertEquals("componentInstance cannot be null", e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterType()
     */
    protected Class getComponentAdapterType() {
        return InstanceAdapter.class;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterNature()
     */
    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING );
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new InstanceAdapter("foo", "bar", NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(
            MutablePicoContainer picoContainer) {
        return new InstanceAdapter("Key", 4711, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_visitable()
     */
    protected ComponentAdapter prepDEF_visitable() {
        return new InstanceAdapter("Key", 4711, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new InstanceAdapter("Key", 4711, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isXStreamSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return new InstanceAdapter("Key", 4711, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
    }

}