package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used to test out various things asked on the mailing list.
 * Or to answer questions.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class UserQuestionTestCase extends TestCase {

    // From Scott Farquahsr
    public static class CheeseComponentAdapter extends AbstractComponentAdapter {
        private Map bla;

        public CheeseComponentAdapter(Object componentKey, Class componentImplementation, Map cheeseMap) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
            super(componentKey, componentImplementation);
            this.bla = cheeseMap;
        }

        public Object getComponentInstance(PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException {
            return bla.get("cheese");
        }

        public void verify(PicoContainer pico) throws UnsatisfiableDependenciesException {
        }
    }

    public static interface Cheese {
        String getName();
    }

    public static class Gouda implements Cheese {
        public String getName() {
            return "Gouda";
        }
    }

    public static class Roquefort implements Cheese {
        public String getName() {
            return "Roquefort";
        }
    }

    public static class Omelette {
        private final Cheese cheese;

        public Omelette(Cheese cheese) {
            this.cheese = cheese;
        }

        public Cheese getCheese() {
            return cheese;
        }
    }

    public void testOmeletteCanHaveDifferentCheeseWithAFunnyComponentAdapter() {
        Map cheeseMap = new HashMap();

        MutablePicoContainer pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        pico.registerComponent(Omelette.class);
        pico.registerComponent(new CheeseComponentAdapter("scott", Gouda.class, cheeseMap));

        Cheese gouda = new Gouda();
        cheeseMap.put("cheese", gouda);
        Omelette goudaOmelette = (Omelette) pico.getComponent(Omelette.class);
        assertSame(gouda, goudaOmelette.getCheese());

        Cheese roquefort = new Roquefort();
        cheeseMap.put("cheese", roquefort);
        Omelette roquefortOmelette = (Omelette) pico.getComponent(Omelette.class);
        assertSame(roquefort, roquefortOmelette.getCheese());
    }

    public static interface InterfaceX {
        String getIt();
    }

    public static class Enabled implements InterfaceX {
        public String getIt() {
            return "Enabled";
        }
    }

    public static class Disabled implements InterfaceX {
        public String getIt() {
            return "Disabled";
        }
    }

    public static class Something implements InterfaceX {
        private final Disabled disabled;
        private final Enabled enabled;
        private final Map map;

        public Something(Disabled disabled, Enabled enabled, Map map) {
            this.disabled = disabled;
            this.enabled = enabled;
            this.map = map;
        }

        public String getIt() {
            if (map.get("enabled") == null) {
                return disabled.getIt();
            } else {
                return enabled.getIt();
            }
        }
    }

    public static class NeedsInterfaceX {
        private final InterfaceX interfaceX;

        public NeedsInterfaceX(InterfaceX interfaceX) {
            this.interfaceX = interfaceX;
        }

        public String getIt() {
            return interfaceX.getIt();
        }
    }

    public void testMoreWeirdness() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        Map map = new HashMap();
        pico.registerComponent(map);
        // See class level javadoc in DefaultPicoContainer - about precedence. 
        pico.registerComponent(InterfaceX.class, Something.class);
        pico.registerComponent(Disabled.class);
        pico.registerComponent(Enabled.class);
        pico.registerComponent(NeedsInterfaceX.class);

        NeedsInterfaceX needsInterfaceX = (NeedsInterfaceX) pico.getComponent(NeedsInterfaceX.class);
        assertEquals("Disabled", needsInterfaceX.getIt());
        map.put("enabled", "blah");
        assertEquals("Enabled", needsInterfaceX.getIt());
    }

    // From John Tal 23/03/2004
    public static interface ABC {
    }

    public static interface DEF {
    }

    public static class ABCImpl implements ABC {
        public ABCImpl(DEF def) {
        }
    }

    public static class DEFImpl implements DEF {
        public DEFImpl() {
        }
    }

    public void testJohnTalOne() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();

        picoContainer.registerComponent("ABC", ABCImpl.class);
        picoContainer.registerComponent("DEF", DEFImpl.class);

        assertEquals(ABCImpl.class, picoContainer.getComponent("ABC").getClass());
    }

    public static interface Foo {
    }

    public static interface Bar {
    }

    public static class FooBar implements Foo, Bar {
    }

    public static class NeedsFoo {
        private final Foo foo;

        public NeedsFoo(Foo foo) {
            this.foo = foo;
        }

        public Foo getFoo() {
            return foo;
        }
    }

    public static class NeedsBar {
        private final Bar bar;

        public NeedsBar(Bar bar) {
            this.bar = bar;
        }

        public Bar getBar() {
            return bar;
        }
    }

    public void testShouldBeAbleShareSameReferenceForDifferentTypes() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(FooBar.class);
        pico.registerComponent(NeedsFoo.class);
        pico.registerComponent(NeedsBar.class);
        NeedsFoo needsFoo = (NeedsFoo) pico.getComponent(NeedsFoo.class);
        NeedsBar needsBar = (NeedsBar) pico.getComponent(NeedsBar.class);
        assertSame(needsFoo.getFoo(), needsBar.getBar());
    }

    public void testSeveralDifferentInstancesCanBeCreatedWithOnePreconfiguredContainer() {
        // create a container that doesn't cache instances
        MutablePicoContainer container = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        container.registerComponent(NeedsBar.class);

        Bar barOne = new FooBar();
        container.registerComponent(Bar.class, barOne);
        NeedsBar needsBarOne = (NeedsBar) container.getComponent(NeedsBar.class);
        assertSame(barOne, needsBarOne.getBar());

        // reuse the same container - just flip out the existing foo.
        Bar barTwo = new FooBar();
        container.unregisterComponent(Bar.class);
        container.registerComponent(Bar.class, barTwo);
        NeedsBar needsBarTwo = (NeedsBar) container.getComponent(NeedsBar.class);
        assertSame(barTwo, needsBarTwo.getBar());

        assertNotSame(needsBarOne, needsBarTwo);
    }
}