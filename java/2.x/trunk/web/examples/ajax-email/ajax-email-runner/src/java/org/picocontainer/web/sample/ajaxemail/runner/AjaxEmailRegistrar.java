package org.picocontainer.web.sample.ajaxemail.runner;

import org.codehaus.waffle.registrar.Registrar;
import org.jbehave.web.runner.waffle.JBehaveRegistrar;
import org.picocontainer.web.sample.ajaxemail.scenarios.steps.AjaxEmailScenario;

public class AjaxEmailRegistrar extends JBehaveRegistrar {

	public AjaxEmailRegistrar(Registrar delegate) {
		super(delegate);
	}

	@Override
	protected void registerSteps() {
		registerInstance(new AjaxEmailScenario().getAjaxEmailSteps());
	}
	
}
