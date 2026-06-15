package me.cxdev.commerce.toolkit.email.service;

import java.util.Locale;

import me.cxdev.commerce.toolkit.email.exception.TemplateNotFoundException;

/**
 * Resolves localized email templates by name from the database.
 *
 * <p>Templates are stored as localized items in the database and can be managed
 * via Backoffice or Impex. At runtime this service looks up the matching template
 * by name and locale and returns its raw HTML content for further processing by
 * the Thymeleaf rendering engine.
 */

public interface EmailTemplateResolverService {
	/**
	 * Returns the raw HTML content of the named template for the given locale.
	 *
	 * @param templateName logical name of the template (e.g. {@code "contact"}, {@code "registration"})
	 * @param locale       the locale used to select the localized template variant
	 * @return the resolved HTML template content, never {@code null}
	 * @throws TemplateNotFoundException if no template with the given name exists for the requested locale
	 */

	String resolveEmailTemplate(String templateName, Locale locale) throws TemplateNotFoundException;
}
