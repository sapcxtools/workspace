package sap.commerce.cx.occ.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.CxServiceTicketSearchPageWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import sap.commerce.cx.core.contentsearch.facetdata.ContentSearchPageData;
import sap.commerce.cx.core.serviceticket.exception.ServiceTicketException;
import sap.commerce.cx.facades.data.serviceticket.CxServiceTicketCountData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketData;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.serviceticket.CxServiceTicketFacade;
import sap.commerce.cx.occ.dto.serviceticket.CxServiceTicketFormDTO;
import sap.commerce.cx.occ.dto.serviceticket.CxServiceTicketWsDTO;
import sap.commerce.cx.occ.dto.serviceticket.ServiceTicketCountWsDTO;

@RestController
@RequestMapping(value = "/{baseSiteId}${cxcore.solr.ticket.search.path}")
@ApiVersion("v2")
@Tag(name = "Service Requests")
public class CxServiceTicketController extends BaseController {

	@Resource(name = "cxServiceTicketFacade")
	private CxServiceTicketFacade<SearchStateData, CxServiceTicketData> cxServiceTicketFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@GetMapping()
	@ResponseStatus(HttpStatus.OK)
	@ApiBaseSiteIdParam
	@Operation(operationId = "getAllTickets", summary = "Get all tickets", description = "Should return all tickets with detailed information")
	public CxServiceTicketSearchPageWsDTO getAllTickets(
			@Parameter(description = "Filter conditions that combine attribute keys and attribute values. The attributes are the fields or properties for the Product type configured in the search index type."
					+ " The format is <attributeKey1>:<attributeValue1>:...:<attributeKeyN>:<attributeValueN>.") @RequestParam(required = false) final String filters,
			@Parameter(description = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2") @RequestParam(required = false, defaultValue = "") final String query,
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "The string field the results will be sorted with") @RequestParam(defaultValue = "date-desc") final String sort,
			@Parameter(description = "Response configuration for returned fields, default is BASIC", examples = @ExampleObject(DEFAULT_FIELD_SET)) @ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) {

		final String customerFilter = "customer:" + customerFacade.getCurrentCustomerUid();

		final ContentSearchPageData<SearchStateData, CxServiceTicketData> result = cxServiceTicketFacade.search(customerFilter, query, currentPage, pageSize, sort);

		return getDataMapper().map(result, CxServiceTicketSearchPageWsDTO.class, fields);
	}

	@GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ApiBaseSiteIdParam
	@Operation(operationId = "getTicketForCode", summary = "Retrieve ticket details", description = "Returns details for a specific service ticket.")
	public CxServiceTicketWsDTO getTicketForCode(
			@Parameter(description = "ticket identifier", required = true) @PathVariable final String code,
			@Parameter(description = "Response configuration for returned fields, default is BASIC", examples = @ExampleObject(DEFAULT_FIELD_SET)) @ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) {
		return getDataMapper().map(cxServiceTicketFacade.getTicketForCode(code), CxServiceTicketWsDTO.class, fields);
	}

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "getTicketsCounts", summary = "Retrieve number of service tickets", description = "Returns the number of service tickets.")
	@ApiBaseSiteIdParam
	public ServiceTicketCountWsDTO getTicketsCounts(
			@Parameter(description = "Response configuration for returned fields, default is BASIC", examples = @ExampleObject(DEFAULT_FIELD_SET)) @ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) {

		final CxServiceTicketCountData data = cxServiceTicketFacade.getTicketsCounts(customerFacade.getCurrentCustomerUid());

		return getDataMapper().map(data, ServiceTicketCountWsDTO.class, fields);
	}

	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "createServiceTicket", summary = "Creates a new service ticket request by given service request form id and send service ticket email request.", description = "Creates a new service ticket request by given service request form id and send service ticket email request.")
	public String createServiceTicket(
			@Parameter(required = true) @Valid @RequestBody final CxServiceTicketFormDTO formDTO,
			@Parameter(description = "Response configuration for returned fields, default is BASIC", examples = @ExampleObject(DEFAULT_FIELD_SET)) @ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws ServiceTicketException {

		final CxServiceTicketFormData formData = getDataMapper().map(formDTO, CxServiceTicketFormData.class,
				DEFAULT_FIELD_SET);

		final CxServiceTicketData ticket = cxServiceTicketFacade.createTicket(formData);

		cxServiceTicketFacade.sendServiceTicketEmail(ticket, formData.getFormFields());

		return ticket.getCode();
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(ServiceTicketException.class)
	public ErrorListWsDTO handleException(final ServiceTicketException exception) {
		final ErrorListWsDTO errorList = new ErrorListWsDTO();
		final List<ErrorWsDTO> errorWsDTO = List.of(
				createErrorWsDTO(exception.getMessage(), exception.getCause().toString(), exception.getType()));
		errorList.setErrors(errorWsDTO);

		return errorList;
	}

	private ErrorWsDTO createErrorWsDTO(final String message, final String reason, final String type) {
		final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
		errorWsDTO.setMessage(message);
		errorWsDTO.setReason(reason);
		errorWsDTO.setType(type);

		return errorWsDTO;
	}
}
