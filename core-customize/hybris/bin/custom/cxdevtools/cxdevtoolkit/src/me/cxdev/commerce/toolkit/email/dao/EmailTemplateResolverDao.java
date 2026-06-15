package me.cxdev.commerce.toolkit.email.dao;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import me.cxdev.commerce.toolkit.model.ThymeleafEmailTemplateModel;

/**
 * DAO for retrieving {@link ThymeleafEmailTemplateModel} instances from the database.
 *
 * <p>Implementations query the {@code ThymeleafEmailTemplate} type by its unique code
 * using FlexibleSearch.
 */
public interface EmailTemplateResolverDao {
	/**
	 * Searches for a {@link ThymeleafEmailTemplateModel} by its unique code.
	 *
	 * @param templateName the unique code of the template (e.g. {@code "contact"}, {@code "registration"})
	 * @return the matching {@link ThymeleafEmailTemplateModel}, never {@code null}
	 * @throws UnknownIdentifierException if no template with the given code exists in the database
	 */
	ThymeleafEmailTemplateModel searchTemplate(String templateName) throws UnknownIdentifierException;
}
