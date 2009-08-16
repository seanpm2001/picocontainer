package org.picocontainer.web.sample.ajaxemail;

import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.JUnit4AndTestNgConditionRunner;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Text;

/**
 * A demonstration of the raw Selenium-RC way of testing.
 */
public class LoginTest {

	@Test
	public void testThatFredCanLogin() {

		Selenium browser = new DefaultSelenium("localhost", 4444, "*chrome",
				"http://localhost:8080");
		browser.start();
		browser.setContext("test that Fred can login");
		ConditionRunner conditionRunner = new JUnit4AndTestNgConditionRunner(
				browser, 10, 100, 10000);
		browser.open("/ajaxemail/json/SampleData/load");
		browser.open("/ajaxemail/");
		conditionRunner.waitFor(new Presence("id=userName"));
		browser.type("id=userName", "Gill Bates");
		browser.type("id=password", "1234");
		browser.click("id=submitLogin");
		conditionRunner.waitFor(new Text("Instant Millionaire"));

		sleep(5);
		// we're on the Inbox page - yay!

		browser.close();
		browser.stop();
	}

	private void sleep(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
		}
	}
}