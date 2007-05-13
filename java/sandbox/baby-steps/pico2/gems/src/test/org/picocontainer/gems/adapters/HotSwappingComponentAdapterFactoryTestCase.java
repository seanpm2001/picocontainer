package org.picocontainer.gems.adapters;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class HotSwappingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    private HotSwappingComponentAdapterFactory implementationHidingComponentAdapterFactory = new HotSwappingComponentAdapterFactory(
            new AnyInjectionComponentAdapterFactory());

    // START SNIPPET: man
    public static interface Man {
        Woman getWoman();

        void kiss();

        boolean wasKissed();
    }

    // END SNIPPET: man

    // START SNIPPET: woman
    public static interface Woman {
        Man getMan();
    }

    // END SNIPPET: woman

    public static class Wife implements Woman {
        public final Man partner;

        public Wife(Man partner) {
            this.partner = partner;
        }

        public Man getMan() {
            return partner;
        }
    }


    public void testHotSwappingNaturaelyCaches() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.addComponent(Map.class, HashMap.class);
        Map firstMap = (Map)pico.getComponent(Map.class);
        Map secondMap = (Map)pico.getComponent(Map.class);
        assertSame(firstMap, secondMap);

    }


    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        HotSwappingComponentAdapter hsca = (HotSwappingComponentAdapter) pico.addAdapter(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class))).lastCA();
        List l = (List)pico.getComponent("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();

        ArrayList oldSubject = (ArrayList) hsca.swapRealInstance(newList);;
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }


    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return implementationHidingComponentAdapterFactory;
    }

}
