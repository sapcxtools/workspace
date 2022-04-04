package tools.sapcx.commerce.toolkit.email.attachments;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

@FunctionalInterface
public interface EmailAfterBuildHook {
	void afterBuild(HtmlEmail email) throws EmailException;
}
