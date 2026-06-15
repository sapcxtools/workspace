package me.cxdev.commerce.toolkit.email.service.impl;

import java.util.Locale;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import me.cxdev.commerce.toolkit.email.dao.EmailTemplateResolverDao;
import me.cxdev.commerce.toolkit.email.exception.TemplateNotFoundException;
import me.cxdev.commerce.toolkit.email.service.EmailTemplateResolverService;
import me.cxdev.commerce.toolkit.model.ThymeleafEmailTemplateModel;

public class DatabaseEmailTemplateResolverService implements EmailTemplateResolverService {
	private final EmailTemplateResolverDao emailTemplateResolverDao;

	public DatabaseEmailTemplateResolverService(EmailTemplateResolverDao emailTemplateResolverDao) {
		this.emailTemplateResolverDao = emailTemplateResolverDao;
	}

	@Override
	public String resolveEmailTemplate(String templateName, Locale locale) throws TemplateNotFoundException {
		try {
			ThymeleafEmailTemplateModel template = emailTemplateResolverDao.searchTemplate(templateName);
			return template.getTemplate(locale);
		} catch (UnknownIdentifierException e) {
			throw new TemplateNotFoundException(String.format("An error occurred while searching for the email template: %s", e.getMessage()));
		}
	}
}
