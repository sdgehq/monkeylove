package fi.oulu.mediabrowserlite.media;

import java.awt.image.BufferedImage;
import java.util.Date;

import fi.oulu.mediabrowserlite.media.Media.Type;

public interface MediaListener {

	public void authorChanged(Media media, String oldAuthor);

	public void titleChanged(Media media, String oldTitle);

	public void descriptionChanged(Media media, String oldDescription);

	public void dateCreatedChanged(Media media, Date oldDateCreated);

	public void typeChanged(Media media, Type oldType);

	public void metadataChanged(Media media, String key, String oldValue);

	public void mediaSetSelected(Media media, boolean selected);

	public void mediaSetVisible(Media media, boolean visible);

	public void thumbnailChanged(Media media);

	public void thumbnailChanged(Media media, BufferedImage thumbnail);

	public void contentImageChanged(Media media);

	public void contentImageChanged(Media media, BufferedImage contentImage);

	public void mediaContentChanged(Media media);

	public void mediaContentChanged(Media media, Object content);
}
