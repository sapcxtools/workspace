package sap.commerce.cx.core.serviceticket.dao;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nonnull;
import sap.commerce.cx.core.model.CxServiceTicketModel;

/**
 * Dao for service tickets.
 *
 * @param <M> the concrete ticket model type
 */
public interface CxServiceTicketDAO<M extends CxServiceTicketModel> {

	/**
	 * Finds a ticket by its unique code.
	 *
	 * @param code the ticket code
	 * @return an optional containing the ticket if found
	 */
	Optional<M> findByCode(@Nonnull String code);

	/**
	 * Returns all service tickets.
	 *
	 * @return all tickets
	 */
	List<M> getAllTickets();

	/**
	 * Returns tickets linked to a specific machine code.
	 *
	 * @param machineCode the machine product code
	 * @return matching tickets
	 */
	List<M> getAllTicketsWithMachineCode(@Nonnull String machineCode);
}
