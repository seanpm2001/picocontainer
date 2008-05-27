package org.nanocontainer.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import static org.picocontainer.injectors.Injectors.SDI;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.injectors.AnnotatedMethodInjection;
import org.picocontainer.injectors.ConstructorInjection;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.behaviors.ImplementationHiding;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import com.thoughtworks.xstream.XStream;

public class NanoBuilderTestCase {

    XStream xs = new XStream();

    @Test public void testBasic() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().build();
        NullComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithStartableLifecycle() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withLifecycle().build();
        NullComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new StartableLifecycleStrategy(cm), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithReflectionLifecycle() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withReflectionLifecycle().build();
        NullComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new ReflectionLifecycleStrategy(cm), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithConsoleMonitor() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withConsoleMonitor().build();
        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithCustomMonitorByClass() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @SuppressWarnings({ "unchecked" })
    @Test public void testWithBogusCustomMonitorByClass() {
        try {
            Class aClass = HashMap.class;
            new NanoBuilder().withMonitor(aClass).build();
            fail("should have barfed");
        } catch (ClassCastException e) {
            // expected
        }
    }

    @Test public void testWithImplementationHiding() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withHiddenImplementations().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new ImplementationHiding().wrap(new AdaptingInjection()),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }


    @Test public void testWithImplementationHidingInstance() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withComponentFactory(new ImplementationHiding()).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new ImplementationHiding().wrap(new AdaptingInjection()),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithComponentFactoriesListChainThingy() throws IOException{
        ScriptedPicoContainer nc = new NanoBuilder(SDI()).withComponentAdapterFactories(caching(), implementationHiding()).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new Caching().wrap(new ImplementationHiding().wrap(new SetterInjection())),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @SuppressWarnings("serial")
	public static class CustomParentcontainer extends EmptyPicoContainer {
    }

    @Test public void testWithCustomParentContainer() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder(new CustomParentcontainer()).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new CustomParentcontainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithBogusParentContainerBehavesAsIfNotSet() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder((PicoContainer)null).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }


    @Test public void testWithSetterDI() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withSetterInjection().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new SetterInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithAnnotationDI() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withAnnotatedMethodInjection().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new AnnotatedMethodInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithCtorDI() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withConstructorInjection().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new ConstructorInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithImplementationHidingAndSetterDI() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withHiddenImplementations().withSetterInjection().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new ImplementationHiding().wrap(new SetterInjection()),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithCachingImplementationHidingAndSetterDI() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new Caching().wrap(new ImplementationHiding().wrap(new SetterInjection())),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithThreadSafety() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().withThreadSafety().build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new DefaultScriptedPicoContainer(new Synchronizing().wrap(new AdaptingInjection()),new NullLifecycleStrategy(), new EmptyPicoContainer(), null, cm);
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @Test public void testWithCustomNanoContainer() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new TestNanoContainer(null,new DefaultPicoContainer(new AdaptingInjection(),new NullLifecycleStrategy(), new EmptyPicoContainer()));
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }


    @SuppressWarnings("serial")
	public static class TestNanoContainer extends DefaultScriptedPicoContainer {
        public TestNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
            super(classLoader, delegate);
        }
    }

    @Test public void testWithCustomNanoAndPicoContainer() throws IOException {
        ScriptedPicoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).picoImplementedBy(TestPicoContainer.class).build();
        ComponentMonitor cm = new NullComponentMonitor();
        ScriptedPicoContainer expected = new TestNanoContainer(null, new TestPicoContainer(new AdaptingInjection(), new NullComponentMonitor(), new NullLifecycleStrategy(), new EmptyPicoContainer()));
        assertEquals(xs.toXML(expected),xs.toXML(nc));
    }

    @SuppressWarnings("serial")
	public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory componentFactory, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentFactory, lifecycleStrategy, parent, monitor);
        }
    }




}
