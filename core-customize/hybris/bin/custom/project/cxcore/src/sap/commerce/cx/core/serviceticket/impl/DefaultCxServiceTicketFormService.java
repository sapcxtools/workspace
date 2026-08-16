package sap.commerce.cx.core.serviceticket.impl;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import sap.commerce.cx.core.model.CxServiceTicketFormModel;
import sap.commerce.cx.core.serviceticket.CxServiceTicketFormService;

/**
 * Default implementation of {@link CxServiceTicketFormService}. Uses a {@link GenericDao} to retrieve form
 * configurations from the database.
 */
public class DefaultCxServiceTicketFormService implements CxServiceTicketFormService {
	private final GenericDao<CxServiceTicketFormModel> cxServiceTicketFormDao;

	public DefaultCxServiceTicketFormService(final GenericDao<CxServiceTicketFormModel> cxServiceTicketFormDao) {
		this.cxServiceTicketFormDao = cxServiceTicketFormDao;
	}

	/**
	 * Returns all configured service request forms.
	 *
	 * @return all forms
	 */
	@Override
	public List<CxServiceTicketFormModel> getAllServiceRequestForms() {
		return List.copyOf(cxServiceTicketFormDao.find());
	}

	/**
	 * Returns a service request form for a given id.
	 *
	 * @param id the form id
	 * @return the matching form or {@code null}
	 */
	@Override
	public Optional<CxServiceTicketFormModel> getServiceRequestFormForId(final String id) {
		return emptyIfNull(cxServiceTicketFormDao.find(
				Map.of(CxServiceTicketFormModel.ID, id)))
				.stream()
				.findFirst();
	}
}
