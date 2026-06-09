package me.cxdev.commerce.toolkit.email.service.impl;

import java.util.Locale;
import javassist.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import me.cxdev.commerce.toolkit.email.dao.EmailTemplateResolverDao;
import me.cxdev.commerce.toolkit.email.service.EmailTemplateResolverService;
import me.cxdev.commerce.toolkit.model.ThymeleafEmailTemplateModel;

public class DatabaseEmailTemplateResolverService implements EmailTemplateResolverService {
	private static final Logger LOG = Logger.getLogger(DatabaseEmailTemplateResolverService.class);
	private final EmailTemplateResolverDao emailTemplateResolverDao;

	public DatabaseEmailTemplateResolverService(EmailTemplateResolverDao emailTemplateResolverDao) {
		this.emailTemplateResolverDao = emailTemplateResolverDao;
	}

	@Override
	public String resolveEmailTemplate(String templateName, Locale locale) {
		try {
			ThymeleafEmailTemplateModel template = emailTemplateResolverDao.searchTemplate(templateName);
			return template.getTemplate(locale);
		} catch (NotFoundException e) {
			LOG.error(String.format("An error occurred while searching for the email template: %s", e.getMessage()));
		}
		return StringUtils.EMPTY;
	}

}
