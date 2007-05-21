package org.picocontainer.gems;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

public class PicoBuilderTestCase extends TestCase {

    XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.gems.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithLog4JComponentMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withLog4JMonitoring().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.gems.monitors.Log4JComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCommonsLoggingComponentMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withCommonsLoggingMonitoring().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.gems.monitors.CommonsLoggingComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }



    private String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n  childrenStarted","");
        foo = foo.replaceAll("\n  componentAdapters","");
        foo = foo.replaceAll("\n  orderedComponentAdapters","");
        foo = foo.replaceAll("\n  startedfalsestarted","");
        foo = foo.replaceAll("\n  disposedfalsedisposed","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n    outer-class reference=\"/PICO\"","");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.defaults.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentCharacteristic","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\n    props","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n  componentAdapterFactory\n","\n");
        foo = foo.replaceAll("\n  componentMonitor\n","\n");
        foo = foo.replaceAll("\n  lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.defaults.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=\"org.picocontainer.defaults.DefaultPicoContainer_OrderedComponentAdapterLifecycleManager\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n\n","\n");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }




}
