package nanocontainer.nanning;

import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AspectSystem;
import picocontainer.RegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.PicoRegistrationException;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.hierarchical.HierarchicalPicoContainer;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class NanningNanoContainer extends HierarchicalPicoContainer {
    private final AspectSystem aspectSystem;

    private RegistrationPicoContainer serviceAndAspectContainer;

    public NanningNanoContainer(ComponentFactory componentFactory, RegistrationPicoContainer serviceAndAspectContainer, AspectSystem aspectSystem) {
        super(new NanningComponentFactory(aspectSystem, componentFactory), serviceAndAspectContainer);
        this.serviceAndAspectContainer = serviceAndAspectContainer;
        this.aspectSystem = aspectSystem;
    }

    public static class Default extends NanningNanoContainer {
        public Default(AspectSystem aspectSystem) {
            super(new DefaultComponentFactory(), new DefaultPicoContainer.Default(), aspectSystem);
        }
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param serviceClass
     */
    public void registerServiceOrAspect(Class serviceClass, Class compomentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponent(serviceClass, compomentImplementation);
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param compomentImplementation
     */
    public void registerServiceOrAspect(Class compomentImplementation) throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponentByClass(compomentImplementation);
    }

    public void instantiateComponents() throws PicoInitializationException {
        serviceAndAspectContainer.instantiateComponents();
        Object[] components = serviceAndAspectContainer.getComponents();
        for (int i = 0; i < components.length; i++) {
            Object component = components[i];
            if (component instanceof Aspect) {
                Aspect aspect = (Aspect) component;
                aspectSystem.addAspect(aspect);
            }
        }

        super.instantiateComponents();
    }
}
