package sap.commerce.cx.facades.news.populator;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import sap.commerce.cx.core.model.CxNewsModel;
import sap.commerce.cx.facades.news.data.CxNewsData;

/**
 * Populator that maps a {@link CxNewsModel} to a {@link CxNewsData}.
 */
public class CxNewsPopulator implements Populator<CxNewsModel, CxNewsData> {
	private final Converter<MediaModel, MediaData> mediaConverter;

	public CxNewsPopulator(Converter<MediaModel, MediaData> mediaConverter) {
		this.mediaConverter = mediaConverter;
	}

	/**
	 * Copies all fields from {@code source} to {@code target}.
	 *
	 * @param source the news model to read from; must not be {@code null}
	 * @param target the DTO to write to; must not be {@code null}
	 * @throws ConversionException if population fails
	 */
	@Override
	public void populate(final CxNewsModel source, final CxNewsData target) throws ConversionException {
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setDescription(source.getDescription());
		target.setShortDescription(source.getShortDescription());
		target.setUrl(source.getUrl());
		target.setCreationTime(source.getCreationtime());

		final MediaModel teaserImage = source.getTeaserImage();

		if (teaserImage != null) {
			target.setTeaserImage(mediaConverter.convert(teaserImage));
		}
	}
}
