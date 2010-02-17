package org.picocontainer.converters;

import org.junit.Test;
import org.picocontainer.Converting;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Type;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class BuiltInConverterTestCase {

    @Test
    public void testBuiltInCanConvert() {
        BuiltInConverter bic = new BuiltInConverter();
        assertTrue(bic.canConvert(Integer.class));
        assertTrue(bic.canConvert(Long.class));
        assertTrue(bic.canConvert(Byte.class));
        assertTrue(bic.canConvert(Double.class));
        assertTrue(bic.canConvert(Float.class));
        assertTrue(bic.canConvert(File.class));
        assertTrue(bic.canConvert(Character.class));
        assertTrue(bic.canConvert(Short.class));
        assertTrue(bic.canConvert(Boolean.class));
        assertFalse(bic.canConvert(JPanel.class));
    }

    @Test
    public void testBuiltInConvert() {
        BuiltInConverter bic = new BuiltInConverter();
        assertEquals(12, bic.convert("12", Integer.class));
        assertEquals(12345678901L, bic.convert("12345678901",Long.class));
        assertEquals((byte)12, bic.convert("12", Byte.class));
        assertEquals(2.22, bic.convert("2.22", Double.class));
        assertEquals(1.11F, bic.convert("1.11", Float.class));
        assertEquals(new File("c:\\foo"), bic.convert("c:\\foo", File.class));
        assertEquals('a', bic.convert("a", Character.class));
        assertEquals((short)12, bic.convert("12", Short.class));
        assertEquals(Boolean.TRUE, bic.convert("TRUE", Boolean.class));
        assertEquals(null, bic.convert("anything", JPanel.class));
    }

    @Test
    public void canAddAConverter() {
        BuiltInConverter bic = new BuiltInConverter();
        bic.addConverter(JPanel.class, new JPanelConverter());
        assertTrue(bic.convert("anything", JPanel.class) instanceof JPanel);

    }

    @Test
    public void canSupplementConverters() {
        BuiltInConverter bic = new BuiltInConverter() {
            @Override
            protected void addBuiltInConverters() {
                super.addBuiltInConverters();
                super.addConverter(JPanel.class, new JPanelConverter());
            }
        };
        assertEquals(Boolean.TRUE, bic.convert("TRUE", Boolean.class));
        assertTrue(bic.convert("anything", JPanel.class) instanceof JPanel);

    }

    private static class JPanelConverter implements Converter<JPanel> {
        public JPanel convert(String paramValue) {
            return new JPanel();
        }
    }
    
    
    @Test
    public void testPrimitivesAreSupported() {
        BuiltInConverter converters = new BuiltInConverter();
        assertTrue(converters.canConvert(Boolean.TYPE));
        assertTrue(converters.canConvert(Character.TYPE));
        assertTrue(converters.canConvert(Integer.TYPE));
        assertTrue(converters.canConvert(Long.TYPE));
        assertTrue(converters.canConvert(Short.TYPE));
        assertTrue(converters.canConvert(Float.TYPE));
        assertTrue(converters.canConvert(Double.TYPE));
        assertTrue(converters.canConvert(Byte.TYPE));
        
        assertEquals(12, converters.convert("12", Integer.TYPE));
        assertEquals(12345678901L, converters.convert("12345678901",Long.TYPE));
        assertEquals((byte)12, converters.convert("12", Byte.TYPE));
        assertEquals(2.22, converters.convert("2.22", Double.TYPE));
        assertEquals(1.11F, converters.convert("1.11", Float.TYPE));
        assertEquals('a', converters.convert("a", Character.TYPE));
        assertEquals((short)12, converters.convert("12", Short.TYPE));
        assertEquals(true, converters.convert("TRUE", Boolean.TYPE));
    }
}