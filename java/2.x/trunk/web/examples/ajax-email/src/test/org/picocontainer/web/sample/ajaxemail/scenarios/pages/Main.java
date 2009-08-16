package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Not;
import static org.junit.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class Main extends Page {

	private String box;
    private static final String MAIL_FORM = "//div[contains(@class,\"blockPage\")]//form";

    public Main(Selenium selenium, ConditionRunner runner) {
		super(selenium, runner);
	}

	public static void logout(Selenium selenium) {
		selenium.open("/ajaxemail/");
		try {
			selenium.click("link=Log Out");
		} catch (RuntimeException e) {
		}
	}

	public void selectedBox(final String box) {

		waitFor(new Condition(box + " selected") {
			@Override
			public boolean isTrue(ConditionRunner.Context context) {
				return box.equals(context.getSelenium().getText(
						"//span[@class=\"mailbox_selected\"]"));
			}
		});
		this.box = box;
	}

	public String getSelectedBox() {
		return box;
	}

	public String firstListedEmailSubject() {
		return selenium
				.getText("(//tr[contains(@class,\"messageRow\")])[1]/td[4][text()]");
	}

	public int numberOfMailItemsVisible() {
		return selenium.getXpathCount("//tr[contains(@class,\"messageRow\")]")
				.intValue();
	}

    public void clickButton(String legend) {
        selenium.click("//input[@value='"+legend+"']");
    }

    public void mainPageObscured() {
        waitFor(obscured());
    }

    public void mainPageNotObscured() {
        waitFor(new Not(obscured()));
    }

    private Presence obscured() {
        return new Presence("//div[contains(@class,\"blockOverlay\")]");
    }


    public String blockingMailFormPresent() {
        assertTrue(selenium.isElementPresent(MAIL_FORM +"//textarea[@id = 'message']"));
        return MAIL_FORM;
    }

    public void blockingMailFormNotPresent() {
        assertFalse(selenium.isElementPresent(MAIL_FORM +"//textarea[@id = 'message']"));
    }

    public String[] fillMailForm() {
        return super.formFieldValues(MAIL_FORM, true);
    }
}
