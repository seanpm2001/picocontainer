/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import junit.framework.TestCase;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerImpl;
import org.nanocontainer.testmodel.WebServerConfig;
import org.picocontainer.PicoConfigurationException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.EcmaError;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class JavaScriptAssemblyNanoContainerTestCase extends TestCase {

    protected void setUp() throws Exception {
        MockMonitor.monitorRecorder = "";
        MockMonitor.allComps = new ArrayList();
        Xxx.componentRecorder = "";
    }

    public void testInstantiateBasicRhinoScriptable() throws IOException, ClassNotFoundException, PicoConfigurationException {

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  addComponent('org.nanocontainer.Xxx$A');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        ), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<AA>!A", Xxx.componentRecorder);
    }

    public void testInstantiateBespokeRhinoScriptable() throws IOException, ClassNotFoundException, PicoConfigurationException {

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  addComponent('org.nanocontainer.Xxx$A');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        ), new MockMonitor(), BespokeNanoRhinoScriptable.class);
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<AA>!A", Xxx.componentRecorder);
    }

    public void testInstantiateBogusRhinoScriptable() throws IOException, ClassNotFoundException, PicoConfigurationException {

        try {
            new JavaScriptAssemblyNanoContainer(new StringReader("" +
                    "var parentContainer = new NanoRhinoScriptable();\n" +
                    "with (parentContainer) {\n" +
                    "  addComponent('org.nanocontainer.Xxx$A');\n" +
                    "}\n" +
                    "nano.setNanoRhinoScriptable(parentContainer)\n"
            ), new MockMonitor(), BogusNanoRhinoScriptable.class);
            fail("Should have barfed with EcmaError");
        } catch (EcmaError e) {
            // expected
        }
    }


    public void testInstantiateWithChildContainer() throws IOException, ClassNotFoundException, PicoConfigurationException {

        // A and C have no no dependancies. B Depends on A.

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  addComponent('org.nanocontainer.Xxx$A');\n" +
                "  var childContainer = new NanoRhinoScriptable();\n" +
                "  addContainer(childContainer);\n" +
                "  with (childContainer) {\n" +
                "      addComponent('org.nanocontainer.Xxx$B');\n" +
                "  }\n" +
                "  addComponent('org.nanocontainer.Xxx$C');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        ), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
        assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);

    }

    public void testInstantiateWithImpossibleComponentDependanciesConsideringTheHierarchy() throws IOException, ClassNotFoundException, PicoConfigurationException {

        // A and C have no no dependancies. B Depends on A.

        try {
            new JavaScriptAssemblyNanoContainer(new StringReader("" +
                    "var parentContainer = new NanoRhinoScriptable();\n" +
                    "with (parentContainer) {\n" +
                    "  addComponent('org.nanocontainer.Xxx$B');\n" +
                    "  var childContainer = new NanoRhinoScriptable();\n" +
                    "  addContainer(childContainer);\n" +
                    "  with (childContainer) {\n" +
                    "      addComponent('org.nanocontainer.Xxx$A');\n" +
                    "  }\n" +
                    "  addComponent('org.nanocontainer.Xxx$C');\n" +
                    "}\n" +
                    "nano.setNanoRhinoScriptable(parentContainer)\n"
            ), new MockMonitor());
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (NoSatisfiableConstructorsException e) {
        }
    }

    public void testInstantiateWithBespokeComponentAdaptor() throws IOException, ClassNotFoundException, PicoConfigurationException {

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable('org.picocontainer.extras.ImplementationHidingComponentAdapterFactory');\n" +
                "with (parentContainer) {\n" +
                "  addComponentWithClassKey('org.nanocontainer.testmodel.WebServerConfig','org.nanocontainer.testmodel.DefaultWebServerConfig');\n" +
                "  addComponentWithClassKey('org.nanocontainer.testmodel.WebServer','" + XmlAssemblyNanoContainerTestCase.OverriddenWebServerImpl.class.getName() + "');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        ), new MockMonitor());
        Object ws = nano.getRootContainer().getComponentInstance(WebServer.class);

        assertTrue(ws instanceof WebServer);
        assertFalse(ws instanceof WebServerImpl);

        ws = nano.getRootContainer().getComponentInstances().get(1);

        assertTrue(ws instanceof WebServer);

        //TODO - should be assertFalse( ), we're implementation hiding here !
        assertTrue(ws instanceof WebServerImpl);
    }

    public void testInstantiateWithInlineConfiguration() throws IOException, ClassNotFoundException, PicoConfigurationException {

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  var pc = new Packages.org.nanocontainer.testmodel.WebServerConfigBean();\n" +
                "  pc.setHost('foobar.com');\n" +
                "  pc.setPort(4321);\n" +
                "  addComponentInstance(pc);\n" +
                "  addComponentWithClassKey('org.nanocontainer.testmodel.WebServer','" + XmlAssemblyNanoContainerTestCase.OverriddenWebServerImpl.class.getName() + "');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        ), new MockMonitor());

        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, nano.getRootContainer().getComponentInstances().size());
        WebServerConfig wsc = (WebServerConfig) nano.getRootContainer().getComponentInstance(WebServerConfig.class);
        assertEquals("foobar.com", wsc.getHost());
        assertEquals(4321, wsc.getPort());
    }

}
