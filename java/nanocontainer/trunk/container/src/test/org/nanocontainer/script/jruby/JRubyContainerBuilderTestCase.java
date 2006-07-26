package org.nanocontainer.script.jruby;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.jmock.Mock;
import org.nanocontainer.NanoPicoContainer;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.groovy.A;
import org.nanocontainer.script.groovy.B;
import org.nanocontainer.script.groovy.HasParams;
import org.nanocontainer.script.groovy.ParentAssemblyScope;
import org.nanocontainer.script.groovy.SomeAssemblyScope;
import org.nanocontainer.script.groovy.X;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.SetterInjectionComponentAdapter;
import org.picocontainer.defaults.SetterInjectionComponentAdapterFactory;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

public class JRubyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {
    private static final String ASSEMBLY_SCOPE = "SOME_SCOPE";

    public void testContainerCanBeBuiltWithParentGlobal() {
        Reader script = new StringReader("" +
                "include_class 'java.lang.StringBuffer'\n" +
                "container(:parent => $parent) { \n" +
                "  component(StringBuffer)\n" +
                "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotNull(pico.getParent());
        assertNotSame(parent, pico.getParent());
        assertEquals(StringBuffer.class, pico.getComponentInstance(StringBuffer.class).getClass());
    }

    public void testContainerCanBeBuiltWithComponentImplementation() {
        X.reset();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "container {\n" +
                "    component(A)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        // LifecyleContainerBuilder starts the container
        pico.dispose();

        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testContainerCanBeBuiltWithComponentInstance() {
        Reader script = new StringReader("" +
                "container { \n" +
                "  component(:key => 'string', :instance => 'foo')\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, "SOME_SCOPE");

        assertEquals("foo", pico.getComponentInstance("string"));
    }

    public void testBuildingWithPicoSyntax() {
        Reader script = new StringReader("" +
                "$parent.registerComponentImplementation('foo', Java::JavaClass.for_name('java.lang.String'))\n"  +
                "include_class 'org.picocontainer.defaults.DefaultPicoContainer'\n" +
                "pico = DefaultPicoContainer.new($parent)\n" +
                "pico.registerComponentImplementation(Java::JavaClass.for_name('org.nanocontainer.script.groovy.A'))\n" +
                "pico");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(script, parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        assertNotNull(pico.getComponentInstance(A.class));
        assertNotNull(pico.getComponentInstance("foo"));
    }

    public void testContainerBuiltWithMultipleComponentInstances() {
        Reader script = new StringReader("" +
                "container {\n" +
                "    component(:key => 'a', :instance => 'apple')\n" +
                "    component(:key => 'b', :instance => 'banana')\n" +
                "    component(:instance => 'noKeySpecified')\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        assertEquals("apple", pico.getComponentInstance("a"));
        assertEquals("banana", pico.getComponentInstance("b"));
        assertEquals("noKeySpecified", pico.getComponentInstance(String.class));
    }

    public void testShouldFailWhenNeitherClassNorInstanceIsSpecifiedForComponent() {
        Reader script = new StringReader("" +
                "container {\n" +
                "  component(:key => 'a')\n" +
                "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("NanoContainerMarkupException should have been raised");
        } catch (NanoContainerMarkupException e) {
            // expected
        }
    }

    public void testAcceptsConstantParametersForComponent() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.HasParams'\n" +
                "container {\n" +
                "    component(:key => 'byClass', :class => HasParams, :parameters => [ 'a', 'b', constant('c')])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClass = (HasParams) pico.getComponentInstance("byClass");
        assertEquals("abc", byClass.getParams());
    }

    public void testAcceptsComponentClassNameAsString() {
        Reader script = new StringReader("" +
                "container {\n" +
                "    component(:key => 'byClassString', :class => 'org.nanocontainer.script.groovy.HasParams', :parameters => [ 'c', 'a', 't' ])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClassString = (HasParams) pico.getComponentInstance("byClassString");
        assertEquals("cat", byClassString.getParams());
    }

    public void testAcceptsComponentParametersForComponent() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'org.nanocontainer.script.groovy.B'\n" +
                "container {\n" +
                "    component(:key => 'a1', :class => A)\n" +
                "    component(:key => 'a2', :class => A)\n" +
                "    component(:key => 'b1', :class => B, :parameters => [ key('a1') ])\n" +
                "    component(:key => 'b2', :class => B, :parameters => key('a2'))\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a1 = (A) pico.getComponentInstance("a1");
        A a2 = (A) pico.getComponentInstance("a2");
        B b1 = (B) pico.getComponentInstance("b1");
        B b2 = (B) pico.getComponentInstance("b2");

        assertNotNull(a1);
        assertNotNull(a2);
        assertNotNull(b1);
        assertNotNull(b2);

        assertSame(a1, b1.getA());
        assertSame(a2, b2.getA());
        assertNotSame(a1, a2);
        assertNotSame(b1, b2);
    }

    public void testAcceptsComponentParameterWithClassNameKey() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'org.nanocontainer.script.groovy.B'\n" +
                "container {\n" +
                "    component(:class => A)\n" +
                "    component(:key => B, :class => B, :parameters => key(A))\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = (A) pico.getComponentInstance(A.class);
        B b = (B) pico.getComponentInstance(B.class);

        assertNotNull(a);
        assertNotNull(b);
        assertSame(a, b.getA());
    }

    public void testInstantiateBasicComponentInDeeperTree() {
        X.reset();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "container {\n" +
                "  container {\n" +
                "    component(A)\n" +
                "  }\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.dispose();
        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testCustomComponentAdapterFactoryCanBeSpecified() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "container(:component_adapter_factory => $assembly_scope) {\n" +
                "    component(A)\n" +
                "}");

        A a = new A();
        Mock cafMock = mock(ComponentAdapterFactory.class);
        cafMock.expects(once()).method("createComponentAdapter").with(same(A.class), same(A.class), eq(null)).will(returnValue(new InstanceComponentAdapter(A.class, a)));
        PicoContainer pico = buildContainer(script, null, (ComponentAdapterFactory) cafMock.proxy());
        assertSame(a, pico.getComponentInstanceOfType(A.class));
    }

    public void testCustomComponentMonitorCanBeSpecified() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'java.io.StringWriter'\n" +
                "include_class 'org.picocontainer.monitors.WriterComponentMonitor'\n" +
                "writer = StringWriter.new\n" +
                "monitor = WriterComponentMonitor.new(writer) \n"+
                "container(:component_monitor => monitor) {\n" +
                "    component(A)\n" +
                "    component(:key => StringWriter, :instance => writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = (StringWriter) pico.getComponentInstanceOfType(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenCAFIsSpecified() {
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'java.io.StringWriter'\n" +
                "include_class 'org.picocontainer.monitors.WriterComponentMonitor'\n" +
                "include_class 'org.picocontainer.defaults.DefaultComponentAdapterFactory'\n" +
                "writer = StringWriter.new\n" +
                "monitor = WriterComponentMonitor.new(writer) \n"+
                "container(:component_adapter_factory => DefaultComponentAdapterFactory.new, :component_monitor => monitor) {\n" +
                "    component(A)\n" +
                "    component(:key => StringWriter, :instance => writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = (StringWriter) pico.getComponentInstanceOfType(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentIsSpecified() {
        DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'java.io.StringWriter'\n" +
                "include_class 'org.picocontainer.monitors.WriterComponentMonitor'\n" +
                "writer = StringWriter.new\n" +
                "monitor = WriterComponentMonitor.new(writer) \n"+
                "container(:parent => $parent, :component_monitor => monitor) {\n" +
                "    component(A)\n" +
                "    component(:key => StringWriter, :instance => writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        StringWriter writer = (StringWriter) pico.getComponentInstanceOfType(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentAndCAFAreSpecified() {
        DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'java.io.StringWriter'\n" +
                "include_class 'org.picocontainer.monitors.WriterComponentMonitor'\n" +
                "include_class 'org.picocontainer.defaults.DefaultComponentAdapterFactory'\n" +
                "writer = StringWriter.new\n" +
                "monitor = WriterComponentMonitor.new(writer) \n"+
                "container(:parent => $parent, :component_adapter_factory => DefaultComponentAdapterFactory.new, :component_monitor => monitor) {\n" +
                "    component(A)\n" +
                "    component(:key => StringWriter, :instance => writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        StringWriter writer = (StringWriter) pico.getComponentInstanceOfType(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testInstantiateWithImpossibleComponentDependenciesConsideringTheHierarchy() {
        X.reset();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'org.nanocontainer.script.groovy.B'\n" +
                "include_class 'org.nanocontainer.script.groovy.C'\n" +
                "container {\n" +
                "    component(B)\n" +
                "    container() {\n" +
                "        component(A)\n" +
                "    }\n" +
                "    component(C)\n" +
                "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (UnsatisfiableDependenciesException expected) {
        }
    }

    public void testInstantiateWithChildContainerAndStartStopAndDisposeOrderIsCorrect() {
        X.reset();
        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'org.nanocontainer.script.groovy.B'\n" +
                "include_class 'org.nanocontainer.script.groovy.C'\n" +
                "container {\n" +
                "    component(A)\n" +
                "    container() {\n" +
                "         component(B)\n" +
                "    }\n" +
                "    component(C)\n" +
                "}\n");

        // A and C have no no dependancies. B Depends on A.
        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.stop();
        pico.dispose();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", X.componentRecorder);
    }

    public void testBuildContainerWithParentAttribute() {
        DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer();
        parent.registerComponentInstance("hello", "world");

        Reader script = new StringReader("" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "container(:parent => $parent) {\n" +
                "    component(A)\n" +
                "}\n");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        assertEquals("world", pico.getComponentInstance("hello"));
    }

    public void testBuildContainerWithParentDependencyAndAssemblyScope() throws Exception {
        DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer();
        parent.registerComponentImplementation("a", A.class);

        String source = "" +
        "include_class 'org.nanocontainer.script.groovy.B'\n" +
        "include_class 'org.nanocontainer.script.groovy.SomeAssemblyScope'\n" +
        "container(:parent => $parent) {\n" +
        "  if $assembly_scope.kind_of?(SomeAssemblyScope)\n "+
        "    component(B)\n" +
        "  end\n "+
        "}\n";

        Reader script = new StringReader(source);

        PicoContainer pico = buildContainer(script, parent, new SomeAssemblyScope());
        assertNotNull(pico.getComponentInstanceOfType(B.class));

        script = new StringReader(source);
        pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        assertNull(pico.getComponentInstanceOfType(B.class));
    }

    public void testBuildContainerWithParentAndChildAssemblyScopes() throws IOException {
        String scriptValue = "" +
                "include_class 'org.nanocontainer.script.groovy.A'\n" +
                "include_class 'org.nanocontainer.script.groovy.B'\n" +
                "include_class 'org.nanocontainer.script.groovy.ParentAssemblyScope'\n" +
                "include_class 'org.nanocontainer.script.groovy.SomeAssemblyScope'\n" +
                "container(:parent => $parent) {\n" +
                "  puts 'assembly_scope:'+$assembly_scope.inspect\n " +
                "  case $assembly_scope\n" +
                "  when ParentAssemblyScope\n "+
                "    puts 'parent scope'\n " +
                "    component(A)\n" +
                "  when SomeAssemblyScope\n "+
                "    puts 'child scope'\n " +
                "    component(B)\n" +
                "  else \n" +
                "     raise 'Invalid Scope: ' +  $assembly_scope.inspect\n" +
                "  end\n "+
                "}\n";

        Reader script = new StringReader(scriptValue);
        NanoPicoContainer parent = new DefaultNanoPicoContainer(
            buildContainer(script, null, new ParentAssemblyScope()));
        assertNotNull(parent.getComponentAdapterOfType(A.class));

        script = new StringReader(scriptValue);
        PicoContainer pico = buildContainer(script, parent,  new SomeAssemblyScope());
        assertNotNull(pico.getComponentInstance(B.class));
    }

    public void FAILING_testBuildContainerWithParentAttributesPropagatesComponentAdapterFactory() {
        DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer(new SetterInjectionComponentAdapterFactory() );
        Reader script = new StringReader("container(:parent => $parent)\n");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        ComponentAdapter componentAdapter = pico.registerComponentImplementation(String.class);
        assertTrue("ComponentAdapter should be originally defined by parent" , componentAdapter instanceof SetterInjectionComponentAdapter);
    }

    private PicoContainer buildContainer(Reader script, PicoContainer parent, Object scope) {
        return buildContainer(new JRubyContainerBuilder(script, getClass().getClassLoader()), parent, scope);
    }
}
