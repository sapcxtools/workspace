package sap.commerce.cx.facades.registration.impl;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;

import sap.commerce.cx.core.registration.service.CxRegistrationService;
import sap.commerce.cx.facades.registration.CxRegistrationFacade;

public class DefaultCxRegistrationFacade implements CxRegistrationFacade {
	private final CxRegistrationService cxRegistrationService;

	public DefaultCxRegistrationFacade(CxRegistrationService cxRegistrationService) {
		this.cxRegistrationService = cxRegistrationService;
	}

	@Override
	public void register(B2BRegistrationData data) {
		cxRegistrationService.sendRegistrationMail(buildParameters(data));
	}

	private Map<String, Object> buildParameters(final B2BRegistrationData registrationData) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("title", registrationData.getTitleCode());
		parameters.put("name", registrationData.getName());
		parameters.put("firstname", registrationData.getFirstName());
		parameters.put("email", registrationData.getEmail());
		parameters.put("telephone", registrationData.getTelephone());
		parameters.put("companyName", registrationData.getCompanyName());
		parameters.put("companyAddressStreet", registrationData.getCompanyAddressStreet());
		parameters.put("companyAddressStreetLine2", registrationData.getCompanyAddressStreetLine2());
		parameters.put("companyAddressCity", registrationData.getCompanyAddressCity());
		parameters.put("companyAddressPostalCode", registrationData.getCompanyAddressPostalCode());
		parameters.put("companyAddressRegion", registrationData.getCompanyAddressRegion());
		parameters.put("country", registrationData.getCompanyAddressCountryIso());
		parameters.put("ustId", registrationData.getUstId());
		return parameters;
	}
}
