package me.cxdev.commerce.toolkit.email.dao.impl;

import javassist.NotFoundException;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.log4j.Logger;

import me.cxdev.commerce.toolkit.email.dao.EmailTemplateResolverDao;
import me.cxdev.commerce.toolkit.model.ThymeleafEmailTemplateModel;

public class DatabaseEmailTemplateResolverDao implements EmailTemplateResolverDao {

	private static final Logger LOG = Logger.getLogger(DatabaseEmailTemplateResolverDao.class);

	private final String QUERY = "SELECT {pk} FROM {ThymeleafEmailTemplate} WHERE {code} = ?templateName";
	private final FlexibleSearchService flexibleSearchService;

	public DatabaseEmailTemplateResolverDao(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public ThymeleafEmailTemplateModel searchTemplate(String templateName) throws NotFoundException {
		FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY);
		query.addQueryParameter("templateName", templateName);
		LOG.debug(String.format("Searching for email template with template name: %s", templateName));
		final SearchResult<ThymeleafEmailTemplateModel> result = flexibleSearchService.search(query);
		if (result.getResult().isEmpty()) {
			LOG.error(String.format("No email template found for template name: %s", templateName));
			throw new NotFoundException(String.format("No email template found for template name: %s", templateName));
		}
		return result.getResult().getFirst();
	}
}
