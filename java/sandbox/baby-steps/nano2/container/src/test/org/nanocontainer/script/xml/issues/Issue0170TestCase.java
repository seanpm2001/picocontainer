package org.nanocontainer.script.xml.issues;

import com.thoughtworks.proxy.toys.hotswap.Swappable;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.gems.adapters.HotSwappingComponentAdapter;

//http://jira.codehaus.org/browse/NANO-170
public class Issue0170TestCase extends AbstractScriptedContainerBuilderTestCase {


    public void testSomething() {

    }
    
    public void BROKEN_testHotSwappingCAF() {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-adapter-factory key='factory' class='org.picocontainer.gems.adapters.HotSwappingComponentAdapterFactory'>"+
                "    <component-adapter-factory class='org.picocontainer.adapters.CachingComponentAdapterFactory'>"+
                "      <component-adapter-factory class='org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory'/>"+
                "    </component-adapter-factory>"+
                "  </component-adapter-factory>"+
                "  <component-adapter class-name-key='java.util.List' class='java.util.ArrayList' factory='factory'/>"+
                "</container>");

        PicoContainer pico = buildContainer(script);
        assertNotNull(pico);
        List list = (List)pico.getComponent(List.class);
        assertNotNull(list);

        ComponentAdapter listCA = pico.getComponentAdapter(List.class);

        assertTrue(listCA instanceof HotSwappingComponentAdapter);
        HotSwappingComponentAdapter hsca = (HotSwappingComponentAdapter) listCA;
        ArrayList newList = new ArrayList();
        List oldList = (List) hsca.swapRealInstance(newList);

        List list2 = (List)pico.getComponent(List.class);

        assertEquals(list, list2); // still the same 'end point'

        list2.add("foo");

        assertFalse(oldList.contains("foo"));
        assertTrue(newList.contains("foo"));


    }

    private PicoContainer buildContainer(Reader script) {
        return buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
    }

}

   