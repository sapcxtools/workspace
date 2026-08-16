package sap.commerce.cx.occ.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import jakarta.ws.rs.core.MediaType;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.serviceticket.CxServiceTicketFormFacade;
import sap.commerce.cx.occ.dto.serviceticket.CxServiceTicketFormDTO;

@RestController
@RequestMapping(value = "/{baseSiteId}/forms")
@ApiVersion("v2")
@Tag(name = "serviceTicketForms")
public class CxServiceTicketFormsController extends BaseController {

	@Resource(name = "cxServiceTicketFormFacade")
	private CxServiceTicketFormFacade cxServiceTicketFormFacade;

	@GetMapping()
	@Operation(operationId = "getAllServiceRequestForms", summary = "Get all service ticket forms", description = "Returns all service ticket forms with detailed information")
	public List<CxServiceTicketFormDTO> getAllServiceRequestForms(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		final List<CxServiceTicketFormData> requestForms = cxServiceTicketFormFacade.getAllServiceRequestForms();
		return getDataMapper().mapAsList(requestForms, CxServiceTicketFormDTO.class, fields);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "getServiceRequestFormForId", summary = "Returns service ticket form of given id.", description = "Returns service ticket form of given id.")
	public CxServiceTicketFormDTO getServiceRequestFormForId(@PathVariable("id") final String id,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		final CxServiceTicketFormData requestForm = cxServiceTicketFormFacade.getServiceRequestFormForId(id);
		return getDataMapper().map(requestForm, CxServiceTicketFormDTO.class, fields);
	}
}
