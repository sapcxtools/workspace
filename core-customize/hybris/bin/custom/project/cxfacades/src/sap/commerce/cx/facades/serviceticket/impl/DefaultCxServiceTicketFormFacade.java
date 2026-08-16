package sap.commerce.cx.facades.serviceticket.impl;

import java.util.List;

import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxServiceTicketFormModel;
import sap.commerce.cx.core.serviceticket.CxServiceTicketFormService;
import sap.commerce.cx.facades.data.serviceticket.data.CxServiceTicketFormData;
import sap.commerce.cx.facades.serviceticket.CxServiceTicketFormFacade;

/**
 * Default facade implementation for service request forms.
 */
public class DefaultCxServiceTicketFormFacade implements CxServiceTicketFormFacade {
	private final CxServiceTicketFormService cxServiceTicketFormService;
	private final Converter<CxServiceTicketFormModel, CxServiceTicketFormData> cxServiceTicketFormConverter;

	public DefaultCxServiceTicketFormFacade(
			final CxServiceTicketFormService cxServiceTicketFormService,
			final Converter<CxServiceTicketFormModel, CxServiceTicketFormData> cxServiceTicketFormConverter) {
		this.cxServiceTicketFormService = cxServiceTicketFormService;
		this.cxServiceTicketFormConverter = cxServiceTicketFormConverter;
	}

	@Override
	public List<CxServiceTicketFormData> getAllServiceRequestForms() {
		return cxServiceTicketFormConverter.convertAll(cxServiceTicketFormService.getAllServiceRequestForms());
	}

	@Override
	public CxServiceTicketFormData getServiceRequestFormForId(final String id) {
		return cxServiceTicketFormService.getServiceRequestFormForId(id)
				.map(cxServiceTicketFormConverter::convert)
				.orElseGet(CxServiceTicketFormData::new);
	}
}
