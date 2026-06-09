package me.cxdev.commerce.toolkit.email.service;

import java.util.Locale;

public interface EmailTemplateResolverService {
	String resolveEmailTemplate(String templateName, Locale locale);
}
