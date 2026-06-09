package me.cxdev.commerce.toolkit.email.dao;

import javassist.NotFoundException;

import me.cxdev.commerce.toolkit.model.ThymeleafEmailTemplateModel;

public interface EmailTemplateResolverDao {
	ThymeleafEmailTemplateModel searchTemplate(String templateName) throws NotFoundException;
}
