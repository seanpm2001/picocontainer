package org.picocontainer.defaults;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentRegistry;
import org.picocontainer.ComponentFactory;
import org.picocontainer.tck.AbstractBasicCompatabilityTestCase;
import org.picocontainer.tck.Touchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.tck.DependsOnTouchable;


public class DefaultPicoContainerWithComponentFactory extends AbstractBasicCompatabilityTestCase {

    private ComponentFactory componentFactory = new DefaultComponentFactory();

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponent(Touchable.class, SimpleTouchable.class);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    // testXXX methods are in superclass.

}
