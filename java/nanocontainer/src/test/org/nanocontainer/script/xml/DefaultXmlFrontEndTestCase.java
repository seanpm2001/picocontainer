/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.script.xml;

import junit.framework.TestCase;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServer;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultXmlFrontEndTestCase extends TestCase {

    private Element getRootElement(InputSource is) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
    }

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>"));

        XmlFrontEnd inputSourceContainerFactory = new DefaultXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testPicoInPico() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <container>" +
                "        <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));
        assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        assertNotNull(childContainer.getComponentInstance(WebServer.class));
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoCompositionException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <classpath>" +
                "        <element file='" + testCompJar.getCanonicalPath() + "'/>" +
                "    </classpath>" +
                "    <component key='foo' class='TestComp'/>" +
                "    <container>" +
                "        <classpath>" +
                "            <element file='" + testCompJar2.getCanonicalPath() + "'/>" +
                "        </classpath>" +
                "        <component key='bar' class='TestComp2'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));

        Object fooTestComp = rootContainer.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull("Container should have a 'bar' component", barTestComp);

        assertEquals("foo classloader should be parent of bar",fooTestComp.getClass().getClassLoader(),
                barTestComp.getClass().getClassLoader().getParent());

    }

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "      <component class='Foo'/>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException cnfe) {
        }
    }

    public void testInstantiateEmptyXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a EmptyCompositionException");
        } catch (EmptyCompositionException cnfe) {
        }
    }

    public void testPseudoComponentCreation() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <pseudocomponent factory='org.picoextras.script.xml.DefaultXmlFrontEndTestCase$TestFactory'>" +
                "      <config-or-whatever/>"+
                "    </pseudocomponent>" +
                "</container>"));

        XmlFrontEnd inputSourceContainerFactory = new DefaultXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstances().get(0));
        assertTrue(picoContainer.getComponentInstances().get(0) instanceof String);
        assertEquals("Hello", picoContainer.getComponentInstances().get(0).toString());

    }
    public static class TestFactory implements XmlPseudoComponentFactory {
        public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
            return "Hello";
        }
    }

}
