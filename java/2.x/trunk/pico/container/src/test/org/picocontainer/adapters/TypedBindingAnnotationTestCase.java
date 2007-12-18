package org.picocontainer.adapters;

import static org.picocontainer.BindKey.bindKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.annotations.Bind;
import org.picocontainer.annotations.Inject;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.AnnotatedFieldInjection;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.injectors.SetterInjection;

/** @author Paul Hammant */
public class TypedBindingAnnotationTestCase extends TestCase {

	public void testFieldInjectionWithBindings() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new AnnotatedFieldInjection());

        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);
    }

    private void assertGettingOfAppleOneWorks(MutablePicoContainer mpc) {
        try {
            mpc.getComponent(Apple.class);
            fail("should have barfed");
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            System.out.println("");
            // expected
        }
        assertNotNull(mpc.getComponent(Apple.class, Bramley.class));
    }

    public void testBindingAnnotationsWithConstructorInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer();

        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);
    }

    private void assertFourMemberApplesAreRight(FruitBasket fb) {
        assertNotNull(fb);
        assertEquals(fb.bramley.getX(), 1);
        assertEquals(fb.cox.getX(), 2);
        assertEquals(fb.granny.getX(), 3);
        assertEquals(fb.braeburn.getX(), 4);
    }

    public void testBindingAnnotationsWithMethodInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new MethodInjection("foo"));
        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);

    }

    public void testBindingAnnotationsWithSetterInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new SetterInjection());
        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);

    }



    private void addFiveComponents(MutablePicoContainer mpc) {
        mpc.addComponent(FruitBasket.class);
        mpc.addComponent(bindKey(Apple.class, Bramley.class), AppleImpl1.class);
        mpc.addComponent(bindKey(Apple.class, Cox.class), AppleImpl2.class);
        mpc.addComponent(bindKey(Apple.class, Granny.class), AppleImpl3.class);
        mpc.addComponent(bindKey(Apple.class, Braeburn.class), AppleImpl4.class);
    }

    public interface Apple {
        int getX();
    }
    public static class AppleImpl1 implements Apple {
        public int getX() {
            return 1;
        }
    }
    public static class AppleImpl2 implements Apple {
        public int getX() {
            return 2;
        }
    }
    public static class AppleImpl3 implements Apple {
        public int getX() {
            return 3;
        }
    }
    public static class AppleImpl4 implements Apple {
        public int getX() {
            return 4;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface Bramley {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface Cox {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface Granny {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface Braeburn {}

    public static class FruitBasket {
        @Inject
        private @Bramley Apple bramley;
        @Inject
        private @Cox Apple cox;
        @Inject
        private @Granny Apple granny;
        @Inject
        private @Braeburn Apple braeburn;

        public FruitBasket() {
        }

        // used in testBindingAnnotationsWithConstructorInjection()
        public FruitBasket(@Bramley Apple bramley, @Cox Apple cox, @Granny Apple granny, @Braeburn Apple braeburn) {
            this.bramley = bramley;
            this.cox = cox;
            this.granny = granny;
            this.braeburn = braeburn;
        }

        // used in testBindingAnnotationsWithMethodInjection()
        public void foo(@Bramley Apple bramley, @Cox Apple cox, @Granny Apple granny, @Braeburn Apple braeburn) {
            this.bramley = bramley;
            this.cox = cox;
            this.granny = granny;
            this.braeburn = braeburn;
        }

        public void setOne(@Bramley Apple bramley) {
            this.bramley = bramley;
        }

        public void setTwo(@Cox Apple cox) {
            this.cox = cox;
        }

        public void setThree(@Granny Apple granny) {
            this.granny = granny;
        }

        public void setFour(@Braeburn Apple braeburn) {
            this.braeburn = braeburn;
        }
    }


}
