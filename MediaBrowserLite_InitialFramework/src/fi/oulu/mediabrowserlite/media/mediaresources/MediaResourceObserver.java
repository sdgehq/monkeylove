package fi.oulu.mediabrowserlite.media.mediaresources;

import java.awt.Image;

import fi.oulu.mediabrowserlite.media.Media;

public interface MediaResourceObserver {

	public void thumbnailAdded(Media media, Image thumbnail);

	public void thumbnailRemoved(Media media, Image thumbnail);

	public void thumbnailLoaded(Media media, Image thumbnail);

	public void thumbnailLoadingFailed(Media media);

	public void contentImageAdded(Media media, Image image);

	public void contentImageRemoved(Media media, Image image);

	public void contentImageLoaded(Media media, Image image);

	public void contentImageReleased(Media media, Image image);

	public void contentImageLoadingFailed(Media media);

	public void mediaResourceAdded(Media media, Object resource);

	public void mediaResourceRemoved(Media media, Object resource);

	public void mediaResourceLoaded(Media media, Object object);

	public void mediaResourceReleased(Media media, Object resource);

	public void mediaResourceLoadingFailed(Media media);

}
