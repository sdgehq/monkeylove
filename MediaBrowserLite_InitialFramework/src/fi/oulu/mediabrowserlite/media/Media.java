package fi.oulu.mediabrowserlite.media;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public interface Media {

	public static final double NOT_SPECIFIED_LATITUDE = 1000;
	public static final double NOT_SPECIFIED_LONGITUDE = 1000;
	public static final double NOT_SPECIFIED_ALTITUDE = -1;
	public static final double NOT_SPECIFIED_DIRECTION = -1;

	public static final String META_KEY_MAKE = "META_KEY_MAKE";
	public static final String META_KEY_MODEL = "META_KEY_MODEL";

	public enum Type {
		POSITIVE, NEUTRAL, NEGATIVE
	};

	public String getId();

	public String getMediaType();

	public void setMediaType(String type);

	public void setId(String id);

	public String getAuthor();

	public void setAuthor(String author);

	public String getTitle();

	public void setTitle(String title);

	public String getDescription();

	public void setDescription(String description);

	public Date getDateCreated();

	public void setDateCreated(Date date);

	public Date getDateCreatedGmt();

	public void setDateCreatedGmt(Date date);

	public Date getDateModified();

	public void setDateModified(Date date);

	public Date getDateModifiedGmt();

	public void setDateModifiedGmt(Date date);

	public Date getDatePublished();

	public void setDatePublished(Date date);

	public Date getDatePublishedGmt();

	public void setDatePublishedGmt(Date date);

	public String getMimeType();

	public void setMimeType(String mime);

	public String getPath();

	public String getFileName();

	public void setPath(String path);

	public String getContentImagePath();

	public void setContentImagePath(String path);

	public String getThumbnailImagePath();

	public void setThumbnailImagePath(String path);

	public Map<String, String> getMeta();

	public String getMeta(String key);

	public void setMeta(String key, String o);

	public Collection<String> getMetaKeys();

	public double getLatitude();

	public void setLatitude(double latitude);

	public double getLongitude();

	public void setLongitude(double longitude);

	public double getAltitude();

	public void setAltitude(double altitude);

	public double getDirection();

	public void setDirection(double direction);

	public boolean isDeleted();

	public void setDeleted(boolean deleted);

	public Type getType();

	public void setType(Type type);

	public void setSelected(boolean selected);

	public boolean isSelected();

	public void setVisible(boolean visible);

	public boolean isVisible();

	public void addMediaListener(MediaListener listener);

	public boolean removeMediaListener(MediaListener listener);

	public void broadcastThumbnailChanged();

	public void broadcastThumbnailChanged(BufferedImage thumbnail);

	public void broadcastContentImageChanged();

	public void broadcastContentImageChanged(BufferedImage contentImage);

	public void broadcastMediaContentChanged();

	public void broadcastMediaContentChanged(Object content);

	public boolean mediaFileChanged();

	public void setMediaFileChanged(boolean changed);

	public boolean isPublished();

	public void setPublished(boolean published);

	public boolean isMediaFileAvailable();

}
