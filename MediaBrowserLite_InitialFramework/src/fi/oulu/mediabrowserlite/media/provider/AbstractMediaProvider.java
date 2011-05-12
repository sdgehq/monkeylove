package fi.oulu.mediabrowserlite.media.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.oulu.mediabrowserlite.ThumbnailGenerator;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaHelper;
import fi.oulu.mediabrowserlite.media.provider.storage.MediaStorage;

public abstract class AbstractMediaProvider implements MediaProvider {

	private MediaStorage storage;

	private Map<String, List<String>> supportedConversions = new HashMap<String, List<String>>();

	public AbstractMediaProvider(MediaStorage storage) {
		this.storage = storage;
	}

	public MediaStorage getStorage() {
		return storage;
	}

	public boolean isSupported(Media media) {
		return storage.isSupported(media);
	}

	protected void addSupportedConversion(String sourceType,
			String destinationType) {
		List<String> types = supportedConversions.get(sourceType.intern());
		if (types == null) {
			types = new ArrayList<String>();
			supportedConversions.put(sourceType.intern(), types);
		}
		types.add(destinationType);
	}

	protected void removeSupportedConversion(String sourceType,
			String destinationType) {
		List<String> types = supportedConversions.get(sourceType.intern());
		if (types != null) {
			types.remove(destinationType);
			if (types.isEmpty()) {
				supportedConversions.remove(sourceType.intern());
			}
		}
	}

	protected List<String> getConversionDestinationMediaTypes(
			String sourceMediaType) {
		List<String> types = supportedConversions.get(sourceMediaType.intern());
		if (types == null) {
			types = new ArrayList<String>();
		}
		return types;
	}

	public boolean canConvert(Media media, String destinationMediaType) {
		for (String supported : getConversionDestinationMediaTypes(media
				.getMediaType())) {
			if (destinationMediaType.equals(supported)) {
				return true;
			}
		}
		return false;
	}

	public Media convert(Media media, String destinationMediaType)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("You wanted to convert "
				+ media.getMediaType() + " to " + destinationMediaType
				+ ". This needs to be implemented in " + getClass().getName());
	}

	public List<Media> load() {
		List<Media> medias = new ArrayList<Media>();

		for (Media media : storage.getMedias()) {
			String path = media.getPath();
			// if (path == null || path.equals("")) {
			// System.out
			// .println("AbstractMediaProvider.load, no path --> deleting media from database: "
			// + media);
			// storage.deleteMedia(media);
			// } else {
			File file = new File(path);
			if (!file.exists()) {
				// System.out
				// .println("AbstractMediaProvider.load, no file with the specified path exists --> deleting media from database: "
				// + media);
				// storage.deleteMedia(media);
			} else {
				media.setThumbnailImagePath(ThumbnailGenerator
						.generateThumbnail(media, false));
				media.setContentImagePath(ThumbnailGenerator
						.generateContentImage(media, false));
				medias.add(media);
			}
			// }
		}
		return medias;
	}

	protected void deleteFiles(Media media) {
		String path = media.getPath();
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
		path = media.getThumbnailImagePath();
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
		path = media.getContentImagePath();
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public void storeApplicationData() {
		// do nothing
	}

	public void prepareForShutdown() {
		// do nothing
	}

	public boolean newMedia(Media media, boolean internalAction) {
		return storage.newMedia(media);
	}

	public boolean updateMedia(Media media, boolean internalAction) {
		return storage.updateMedia(media);
	}

	public boolean deleteMedia(Media media, boolean internalAction) {
		return storage.deleteMedia(media);
	}

	public boolean updateMediaContent(Media media, boolean internalAction) {
		media.setMediaFileChanged(false);
		updateMedia(media, internalAction);
		return true;
	}

	public boolean executeUpdate(Media media) {
		// implement in sub classes if necessary
		return true;
	}
}
