package tools.sapcx.commerce.toolkit.email.attachments;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;

import org.apache.commons.mail.EmailException;

public class MediaBasedHtmlEmailAttachmentBuilder extends AbstractHtmlEmailAttachmentBuilder {
	private MediaModel media;

	public MediaBasedHtmlEmailAttachmentBuilder(MediaModel media) {
		this.media = media;
		this.name(media.getRealFileName());
		this.description(media.getDescription());
	}

	@Override
	protected DataSource getDataSource() throws EmailException {
		try {
			MediaService mediaService = Registry.getApplicationContext().getBean(MediaService.class);
			return new ByteArrayDataSource(mediaService.getDataFromMedia(media), media.getMime());
		} catch (NoDataAvailableException e) {
			throw new EmailException("Cannot attach empty file from media: " + media.getCode(), e);
		}
	}
}
