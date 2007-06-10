package org.picocontainer.injectors;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.Inject;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AnnotationInjector;
import org.picocontainer.injectors.SetterInjector;

public class AnnotationInjectionAdapterTestCase extends TestCase {

    public static class AnnotatedBurp {

        private Wind wind;

        @Inject
        public void windyWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class SetterBurp {

        private Wind wind;

        public void setWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class Wind {
    }

    public void testSetterMethodInjectionToContrastWithThatBelow() {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(SetterBurp.class, SetterBurp.class, Parameter.DEFAULT));
        pico.addComponent(Wind.class, new Wind());
        SetterBurp burp = pico.getComponent(SetterBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    public void testNonSetterMethodInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotationInjector(AnnotatedBurp.class, AnnotatedBurp.class, Parameter.DEFAULT) {
            protected String getInjectorPrefix() {
                return "init";
            }
        });
        pico.addComponent(Wind.class, new Wind());
        AnnotatedBurp burp = pico.getComponent(AnnotatedBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

}