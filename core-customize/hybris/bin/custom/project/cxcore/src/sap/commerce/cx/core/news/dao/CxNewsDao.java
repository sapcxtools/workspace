package sap.commerce.cx.core.news.dao;

import java.util.List;

import sap.commerce.cx.core.model.CxNewsModel;

/**
 * DAO for accessing news data.
 * <p>
 * Provides access methods for retrieving news entries from the persistence layer.
 * </p>
 */
public interface CxNewsDao {

	/** Returns all available news entries.
	 *
	 * @return a list of all available news entries
	 */
	List<CxNewsModel> findNews();
}
