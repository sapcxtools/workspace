package sap.commerce.cx.occ.mapping.converter;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.b2bwebservicescommons.dto.company.OrgUserRegistrationDataWsDTO;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

@WsDTOMapping
public class CxB2BRegistrationDataConverter {
	public B2BRegistrationData convert(final OrgUserRegistrationDataWsDTO source) {

		final B2BRegistrationData target = new B2BRegistrationData();

		target.setFirstName(StringUtils.trimToEmpty(source.getFirstName()));
		target.setName(StringUtils.trimToEmpty(source.getLastName()));
		target.setEmail(StringUtils.trimToEmpty(source.getEmail()));
		target.setTitleCode(StringUtils.trimToEmpty(source.getTitleCode()));
		target.setMessage(StringUtils.trimToEmpty(source.getMessage()));
		target.setVerificationTokenId(StringUtils.trimToEmpty(source.getVerificationTokenId()));
		target.setVerificationTokenCode(StringUtils.trimToEmpty(source.getVerificationTokenCode()));

		target.setTelephone(StringUtils.trimToEmpty(source.getTelephone()));
		target.setCompanyName(StringUtils.trimToEmpty(source.getCompanyName()));
		target.setCompanyAddressStreet(StringUtils.trimToEmpty(source.getCompanyAddressStreet()));
		target.setCompanyAddressStreetLine2(StringUtils.trimToEmpty(source.getCompanyAddressStreetLine2()));
		target.setCompanyAddressCity(StringUtils.trimToEmpty(source.getCompanyAddressCity()));
		target.setCompanyAddressPostalCode(StringUtils.trimToEmpty(source.getCompanyAddressPostalCode()));
		target.setCompanyAddressRegion(StringUtils.trimToEmpty(source.getCompanyAddressRegion()));
		target.setCompanyAddressCountryIso(StringUtils.trimToEmpty(source.getCompanyAddressCountryIso()));
		target.setUstId(StringUtils.trimToEmpty(source.getUstId()));

		return target;
	}
}
