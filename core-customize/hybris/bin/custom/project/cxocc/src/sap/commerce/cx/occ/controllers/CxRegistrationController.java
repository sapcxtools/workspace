package sap.commerce.cx.occ.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.b2bocc.exceptions.B2BRegistrationException;
import de.hybris.platform.b2bocc.exceptions.RegistrationRequestCreateException;
import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.b2bwebservicescommons.dto.company.OrgUserRegistrationDataWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.annotation.CaptchaAware;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.constants.CommercewebservicescommonsConstants;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import jakarta.ws.rs.core.MediaType;
import sap.commerce.cx.facades.registration.CxRegistrationFacade;
import sap.commerce.cx.occ.mapping.converter.CxB2BRegistrationDataConverter;

/**
 * OCC controller for handling CX specific registration requests.
 * <p>
 * This controller overrides the default B2B registration endpoint and
 * delegates registration processing to the custom CX registration facade.
 * It also validates the incoming request data and maps the webservice DTO
 * to the corresponding facade data object.
 * </p>
 */
@Controller
@ApiVersion("v2")
public class CxRegistrationController extends BaseController {

	private static final Logger LOG = Logger.getLogger(CxRegistrationController.class);

	protected static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";
	private static final String REGISTRATION_NOT_ENABLED_ERROR_KEY = "Registration is not enabled";

	@Resource
	private Validator orgUserRegistrationDataValidator;

	@Resource
	private Validator orgUserRegistrationNameValidator;

	@Resource
	private CxRegistrationFacade registrationFacade;

	@Resource(name = "cxB2BRegistrationDataConverter")
	private CxB2BRegistrationDataConverter cxB2BRegistrationDataConverter;

	/**
	 * Creates a registration request for a B2B customer.
	 * <p>
	 * The incoming request data is validated, mapped to
	 * {@link de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData},
	 * and then processed by the custom registration facade.
	 * </p>
	 *
	 * @param orgUserRegistrationData the registration request data
	 * @param fields the response field configuration
	 */
	@SecurePortalUnauthenticatedAccess
	@PostMapping(value = "/{baseSiteId}/orgUsers", consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(CREATED)
	@RequestMappingOverride(priorityProperty = "b2bocc.B2BUsersController.createRegistrationRequest.priority")
	@SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
	@Operation(operationId = "createRegistrationRequest", summary = "Creates a registration request for a B2B customer.", description = "Creates a B2B customer registration request with structured user, company, "
			+ "address and tax information. "
			+ "Required parameters are: firstName, lastName, email and ustId. "
			+ "The request may also contain: titleCode, telephone, companyName, "
			+ "companyAddressStreet, companyAddressStreetLine2, companyAddressCity, "
			+ "companyAddressRegion, companyAddressPostalCode and companyAddressCountryIso. "
			+ "verificationTokenId and verificationTokenCode are only required when OTP "
			+ "verification for registration is enabled.")
	@ApiBaseSiteIdParam
	@Parameter(name = CommercewebservicescommonsConstants.CAPTCHA_TOKEN_HEADER, description = CommercewebservicescommonsConstants.CAPTCHA_TOKEN_HEADER_DESC, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
	@CaptchaAware
	public void createRegistrationRequest(
			@Parameter(description = "Registration data containing user, company, address and tax information required to create a B2B registration request.", required = true) @RequestBody final OrgUserRegistrationDataWsDTO orgUserRegistrationData,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		validate(orgUserRegistrationData, "orgUserRegistrationData", orgUserRegistrationDataValidator);

		final B2BRegistrationData b2BRegistrationData = cxB2BRegistrationDataConverter.convert(orgUserRegistrationData);

		validate(b2BRegistrationData, "orgUserRegistrationData", orgUserRegistrationNameValidator);

		try {
			registrationFacade.register(b2BRegistrationData);
		} catch (final UnknownIdentifierException e) {
			throw new RegistrationRequestCreateException(e.getMessage(), e);
		} catch (final IllegalArgumentException e) {
			throw e;
		} catch (final RuntimeException re) {
			throw new B2BRegistrationException(
					"Encountered an error when creating a registration request", re);
		}
	}
}
