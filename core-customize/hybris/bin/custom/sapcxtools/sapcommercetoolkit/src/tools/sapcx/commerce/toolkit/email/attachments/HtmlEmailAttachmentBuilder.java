package tools.sapcx.commerce.toolkit.email.attachments;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public interface HtmlEmailAttachmentBuilder {
	HtmlEmailAttachmentBuilder name(String name);

	HtmlEmailAttachmentBuilder description(String description);

	void attach(HtmlEmail email) throws EmailException;
}
