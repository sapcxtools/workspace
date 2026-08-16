package sap.commerce.cx.occ.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;

import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import sap.commerce.cx.facades.news.CxNewsFacade;
import sap.commerce.cx.facades.news.data.CxNewsData;
import sap.commerce.cx.occ.dto.news.CxNewsListWsDTO;
import sap.commerce.cx.occ.dto.news.CxNewsWsDTO;

/**
 * OCC controller that exposes CX news entries via REST.
 *
 * <p>Base endpoint: {@code /{baseSiteId}/cx/news}
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/cx/news")
public class CxNewsController {
	@Resource(name = "cxNewsFacade")
	private CxNewsFacade cxNewsFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/**
	 * Returns the latest active news entries.
	 *
	 * @param count optional maximum number of entries to return; if omitted the
	 *              server-side default {@code cx.news.component.maxItems} is used
	 * @param fields OCC field-set selector
	 * @return wrapper containing the mapped news DTOs
	 */
	@GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getLatestActiveNews", summary = "Returns latest active news.", description = "Returns the latest active news entries for the dashboard news component. "
			+ "The result can be limited with the optional count parameter. "
			+ "If count is omitted, the configured default limit is used.")
	@ApiBaseSiteIdParam
	public CxNewsListWsDTO getLatestActiveNews(
			@RequestParam(required = false) final Integer count,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields) {
		final List<CxNewsData> newsData = count == null
				? cxNewsFacade.getLatestActiveNews()
				: cxNewsFacade.getLatestActiveNews(count);

		return mapToWsDto(newsData, fields);
	}

	/**
	 * Returns all active news entries.
	 *
	 * @param fields OCC field-set selector
	 * @return wrapper containing all active mapped news DTOs
	 */
	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getAllActiveNews", summary = "Returns all active news.", description = "Returns all currently active news entries for the news overview page.")
	@ApiBaseSiteIdParam
	public CxNewsListWsDTO getAllActiveNews(
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields) {
		final List<CxNewsData> newsData = cxNewsFacade.getAllActiveNews();

		return mapToWsDto(newsData, fields);
	}

	protected CxNewsListWsDTO mapToWsDto(final List<CxNewsData> newsData, final String fields) {
		final CxNewsListWsDTO result = new CxNewsListWsDTO();
		result.setNews(dataMapper.mapAsList(newsData, CxNewsWsDTO.class, fields));
		return result;
	}
}
