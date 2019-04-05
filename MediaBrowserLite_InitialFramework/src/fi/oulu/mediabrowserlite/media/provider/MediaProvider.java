package fi.oulu.mediabrowserlite.media.provider;

import java.util.List;

import fi.oulu.mediabrowserlite.media.Media;

public interface MediaProvider {

	public boolean isSupported(Media media);

	public boolean canConvert(Media media, String destinationMediaType);

	public Media convert(Media media, String destinationMediaType)
			throws UnsupportedOperationException;

	public List<Media> load();

	public boolean updateMediaContent(Media media, boolean internalAction);

	public void storeApplicationData();

	public void prepareForShutdown();

	public boolean newMedia(Media media, boolean internalAction);

	public boolean updateMedia(Media media, boolean internalAction);

	public boolean deleteMedia(Media media, boolean internalAction);

	public boolean undeleteMedia(Media media, boolean internalAction);

	public boolean executeUpdate(Media media);

}
