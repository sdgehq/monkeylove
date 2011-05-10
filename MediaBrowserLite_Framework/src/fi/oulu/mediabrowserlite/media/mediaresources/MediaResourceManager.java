package fi.oulu.mediabrowserlite.media.mediaresources;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.RotateListener;

/**
 * This is a dummy version of the class for testing
 * @author virtu
 *
 */

public class MediaResourceManager {

	private static MediaResourceManager instance;

	public synchronized static MediaResourceManager getInstance() {
		if (instance == null) {
			instance = new MediaResourceManager();
		}
		return instance;
	}

	private MediaResourceManager() {

	}

	public void clearResources(Media media) {
	}

	public void putMediaResourceLoader(String mimeType,
			MediaResourceLoader loader) {
	}

	public void removeMediaResourceLoader(MediaResourceLoader loader) {
	}

	public MediaResourceLoader getMediaResourceLoader(Media media) {
		return null;
	}

	public MediaResourceLoader getMediaResourceLoader(String mimeType) {
		return null;
	}

	public void addMediaResourceObserver(MediaResourceObserver observer) {
	}

	public void removeMediaResourceObserver(MediaResourceObserver observer) {
	}

	

	public BufferedImage getThumbnail(Media media) {
		BufferedImage thumb = null;
		return thumb;
	}

	public void getThumbnail(final Media media,
			final MediaResourceObserver observer) {
	}

	public void releaseContentImage(Media media) {
	}

	public BufferedImage getContentImage(Media media) {
		BufferedImage image = null;
		return image;
	}

	public void getContentImage(final Media media,
			final MediaResourceObserver observer) {
	}

	public void releaseMediaResource(Media media) {
	}

	public Object getMediaResource(Media media) {
		Object resource = null;
		return resource;
	}

	public void getMediaResource(final Media media,
			final MediaResourceObserver observer) {
	}

	private Set<RotateListener> rotateListeners = new HashSet<RotateListener>();

	public void addRotateListener(RotateListener listener) {
	}

	public void removeRotateListener(RotateListener listener) {
	}

	public void rotate(Media media, double angle) {
	}

	public void rotate(List<Media> medias, double angle) {
	}

	public void rotate(final List<Media> medias, final double angle,
			final RotateListener listener) {
	}
}
