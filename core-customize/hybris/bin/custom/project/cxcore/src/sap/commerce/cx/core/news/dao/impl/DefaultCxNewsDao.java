package sap.commerce.cx.core.news.dao.impl;

import java.util.List;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;

import sap.commerce.cx.core.model.CxNewsModel;
import sap.commerce.cx.core.news.dao.CxNewsDao;

/**
 * Default implementation of {@link CxNewsDao} that loads news entries via FlexibleSearch.
 * <p>
 * The result is ordered by creation time in descending order so that the newest
 * entries are returned first.
 * </p>
 */
public class DefaultCxNewsDao extends DefaultGenericDao<CxNewsModel> implements CxNewsDao {

	public DefaultCxNewsDao() {
		super(CxNewsModel._TYPECODE);
	}

	/** {@inheritDoc} */
	@Override
	public List<CxNewsModel> findNews() {
		return find(SortParameters.singletonDescending(CxNewsModel.CREATIONTIME));
	}
}
