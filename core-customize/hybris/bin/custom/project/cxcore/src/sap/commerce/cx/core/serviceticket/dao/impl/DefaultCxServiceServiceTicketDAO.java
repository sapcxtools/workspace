package sap.commerce.cx.core.serviceticket.dao.impl;

import static de.hybris.platform.core.model.product.ProductModel._TYPECODE;
import static sap.commerce.cx.core.jalo.CxServiceTicket.ALLMACHINES;

import java.util.*;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import jakarta.annotation.Nonnull;
import sap.commerce.cx.core.model.CxServiceTicketModel;
import sap.commerce.cx.core.serviceticket.dao.CxServiceTicketDAO;

/**
 * Default DAO implementation for service tickets.
 */
public class DefaultCxServiceServiceTicketDAO<M extends CxServiceTicketModel> extends DefaultGenericDao<M>
		implements CxServiceTicketDAO<M>, GenericDao<M> {

	public static final String LEFT_JOIN = " LEFT JOIN ";
	private static final String SELECT_TICKETS = ("SELECT {t:" + ItemModel.PK + "}");
	private static final String FROM_TICKET_JOIN_MACHINE_RELATION_CLAUSE = " FROM {" + CxServiceTicketModel._TYPECODE + " AS t" + LEFT_JOIN + "tickets2Machines AS rel ON {t:"
			+ ItemModel.PK + "} = {rel:source}" + LEFT_JOIN + _TYPECODE + " AS m ON {m:" + ItemModel.PK
			+ "} = {rel:target}}";
	private static final String MACHINES_CLAUSE = " WHERE {m:" + ProductModel.CODE + "} = ?code";
	private static final String ALL_MACHINE_CLAUSE = "{t:" + ALLMACHINES + "} = ?allMachines";

	public DefaultCxServiceServiceTicketDAO() {
		super(CxServiceTicketModel._TYPECODE);
	}

	/**
	 * Finds a ticket by code.
	 *
	 * @param code the ticket code
	 * @return optional with a matching ticket
	 */
	@Override
	public Optional<M> findByCode(@Nonnull final String code) {
		final Map<String, String> params = new HashMap<>();
		params.put("code", code);
		return find(params).stream().findFirst();
	}

	/**
	 * Returns all tickets.
	 *
	 * @return all ticket models
	 */
	@Override
	public List<M> getAllTickets() {
		final Map<String, PK> params = new HashMap<>();
		return find(params);
	}

	/**
	 * Returns tickets associated with the provided machine code or marked as all-machines.
	 *
	 * @param machineCode the machine code
	 * @return matching ticket models
	 */
	@Override
	public List<M> getAllTicketsWithMachineCode(@Nonnull final String machineCode) {
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("code", machineCode);
		queryParams.put("allMachines", Boolean.TRUE);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				SELECT_TICKETS + FROM_TICKET_JOIN_MACHINE_RELATION_CLAUSE + MACHINES_CLAUSE + " OR "
						+ ALL_MACHINE_CLAUSE,
				queryParams);
		final SearchResult<M> searchResult = getFlexibleSearchService().search(query);

		return searchResult != null ? searchResult.getResult() : new ArrayList<>();
	}
}
