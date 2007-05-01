package org.picocontainer.defaults.issues;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class Issue0196TestCase extends TestCase {
    public static class Descriptor {
        public static class DescriptorData {
        }
    }
    public static class DescriptorDep {
        public DescriptorDep(Descriptor.DescriptorData[] datas) {
            Assert.assertEquals(3, datas.length);
            Assert.assertNull(datas[0]);
            Assert.assertNull(datas[1]);
            Assert.assertNull(datas[2]);
        }
    }

    public void testShouldAllowRegistrationOfArrayAsInstance() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        Descriptor.DescriptorData[] datas = new Descriptor.DescriptorData[3];

        pico.component(datas);
        pico.component(DescriptorDep.class);

        DescriptorDep descriptorDep = (DescriptorDep) pico.getComponent(DescriptorDep.class);

        assertNotNull(descriptorDep);
    }
}
