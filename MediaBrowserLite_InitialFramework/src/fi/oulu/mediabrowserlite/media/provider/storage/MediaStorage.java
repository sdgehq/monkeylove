package fi.oulu.mediabrowserlite.media.provider.storage;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.mediabrowserlite.media.Media;

public abstract class MediaStorage {

	private List<String> supportedMediaTypes = new ArrayList<String>();
	private String mediaIdPrefix;

	public MediaStorage(String mediaIdPrefix) {
		this.mediaIdPrefix = mediaIdPrefix;
	}

	public MediaStorage(String mediaIdPrefix, String... supportedMediaTypes) {
		this(mediaIdPrefix);
		if (supportedMediaTypes != null) {
			for (String type : supportedMediaTypes) {
				this.supportedMediaTypes.add(type);
			}
		}
	}

	protected String getMediaIdPrefix() {
		return mediaIdPrefix;
	}

	protected void setMediaIdPrefix(String prefix) {
		this.mediaIdPrefix = prefix;
	}

	protected String createId(int id) {
		return getMediaIdPrefix() + id;
	}

	protected int parseId(String idString) {
		int id = -1;
		String[] parts = idString.split("_");
		if (parts.length == 2) {
			id = Integer.parseInt(parts[1]);
		}
		return id;
	}

	protected boolean contains(Media media) {
		// TODO implement this properly in the actual classes e.g. check if
		// there is something in a database with the media id
		return media.getId() != null && !media.getId().equals("");
	}

	public boolean isSupported(Media media) {
		return media.getMediaType() != null
				&& getSupportedMediaTypes().contains(media.getMediaType());
	}

	public void addSupportedMediaType(String type) {
		supportedMediaTypes.add(type);
	}

	public boolean removeSupportedMediaType(String type) {
		return supportedMediaTypes.remove(type);
	}

	public List<String> getSupportedMediaTypes() {
		return new ArrayList<String>(supportedMediaTypes);
	}

	public abstract boolean newMedia(Media media)
			throws IllegalArgumentException;

	public abstract boolean updateMedia(Media media)
			throws IllegalArgumentException;

	public abstract boolean deleteMedia(Media media)
			throws IllegalArgumentException;

	public abstract List<Media> getMedias();

}
