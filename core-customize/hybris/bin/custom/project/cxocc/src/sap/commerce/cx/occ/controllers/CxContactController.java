package sap.commerce.cx.occ.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import jakarta.annotation.Resource;
import sap.commerce.cx.facades.contact.CxContactFacade;
import sap.commerce.cx.facades.data.contact.ContactData;
import sap.commerce.cx.occ.annotations.ExceptionMapping;
import sap.commerce.cx.occ.annotations.ExceptionMappingEntry;
import sap.commerce.cx.occ.dto.contact.ContactWsDTO;

@RestController
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "Contact")
public class CxContactController extends BaseController {

	@Resource(name = "cxContactFacade")
	private CxContactFacade contactFacade;

	@Resource(name = "cxContactFormValidator")
	private Validator cxContactFormValidator;

	@PostMapping("/contact")
	@Operation(operationId = "sendContact", summary = "Send contact form", description = "Submits a contact form.")
	@ApiBaseSiteIdParam
	@ExceptionMapping(mappingEntries = {
			@ExceptionMappingEntry(cause = WebserviceValidationException.class, key = "VALIDATION_ERROR", status = HttpStatus.BAD_REQUEST),
			@ExceptionMappingEntry(cause = RuntimeException.class, key = "INTERNAL_SERVER_ERROR", status = HttpStatus.INTERNAL_SERVER_ERROR) })
	public ResponseEntity<Void> sendContact(@Parameter(required = true) @RequestBody final ContactWsDTO contactWsDTO) {

		validate(contactWsDTO, "contactWsDTO", cxContactFormValidator);
		final ContactData contactData = getDataMapper().map(contactWsDTO, ContactData.class);

		contactFacade.sendContact(contactData);

		return ResponseEntity.ok().build();
	}
}
