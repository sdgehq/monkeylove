package fi.oulu.mediabrowserlite.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMediaObject implements Media {

	private String id = null;
	private String mediaType;
	private String mimeType;
	private String contentImagePath;
	private String thumbnailImagePath;
	private String path;
	private Map<String, String> metadata = new HashMap<String, String>();
	private boolean selected = false;
	private boolean visible = true;
	private boolean mediaFileChanged = false;
	private boolean deleted = false;

	private List<MediaListener> listeners = new ArrayList<MediaListener>();

	public void addMediaListener(MediaListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public boolean removeMediaListener(MediaListener listener) {
		synchronized (listeners) {
			return listeners.remove(listener);
		}
	}

	protected void broadcastAuthorChanged(String oldAuthor) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.authorChanged(this, oldAuthor);
			}
		}
	}

	protected void broadcastTitleChanged(String oldTitle) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.titleChanged(this, oldTitle);
			}
		}
	}

	protected void broadcastDescriptionChanged(String oldDescription) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.descriptionChanged(this, oldDescription);
			}
		}
	}

	protected void broadcastDateCreatedChanged(Date oldDateCreated) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.dateCreatedChanged(this, oldDateCreated);
			}
		}
	}

	protected void broadcastTypeChanged(Type oldType) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.typeChanged(this, oldType);
			}
		}
	}

	protected void broadcastMetadataChanged(String key, String oldValue) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.metadataChanged(this, key, oldValue);
			}
		}
	}

	protected void broadcastMediaSetSelected(boolean selected) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.mediaSetSelected(this, selected);
			}
		}
	}

	protected void broadcastMediaSetVisible(boolean visible) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.mediaSetVisible(this, visible);
			}
		}
	}

	public void broadcastThumbnailChanged(BufferedImage thumbnail) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.thumbnailChanged(this, thumbnail);
			}
		}
	}

	public void broadcastContentImageChanged(BufferedImage contentImage) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.contentImageChanged(this, contentImage);
			}
		}
	}

	public void broadcastMediaContentChanged(Object content) {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.mediaContentChanged(this, content);
			}
		}
	}

	public void broadcastThumbnailChanged() {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.thumbnailChanged(this);
			}
		}
	}

	public void broadcastContentImageChanged() {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.contentImageChanged(this);
			}
		}
	}

	public void broadcastMediaContentChanged() {
		synchronized (listeners) {
			for (MediaListener listener : listeners) {
				listener.mediaContentChanged(this);
			}
		}
	}

	public String getFileName() {
		String path = getPath();
		if (path != null) {
			return path.substring(path.lastIndexOf(System
					.getProperty("file.separator")) + 1);
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String type) {
		this.mediaType = type;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mime) {
		this.mimeType = mime;
	}

	public String getContentImagePath() {
		return contentImagePath;
	}

	public void setContentImagePath(String path) {
		this.contentImagePath = path;
	}

	public String getThumbnailImagePath() {
		return thumbnailImagePath;
	}

	public void setThumbnailImagePath(String path) {
		this.thumbnailImagePath = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMeta(String key) {
		return metadata.get(key);
	}

	public void setMeta(String key, String value) {
		String oldValue = getMeta(key);
		metadata.put(key, value);
		if (oldValue != null) {
			broadcastMetadataChanged(key, oldValue);
		}
	}

	public void setMeta(Map<String, String> metadata) {
		this.metadata = new HashMap<String, String>(metadata);
	}

	public Collection<String> getMetaKeys() {
		return metadata.keySet();
	}

	public Map<String, String> getMeta() {
		return metadata;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
		if (deleted && selected) {
			setSelected(false);
		}
		broadcastMediaSetVisible(!deleted && visible);

	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		broadcastMediaSetSelected(selected);

	}

	public boolean isSelected() {
		return selected;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		broadcastMediaSetVisible(visible);
	}

	public boolean isVisible() {
		return (visible && !deleted);
	}

	public boolean mediaFileChanged() {
		return mediaFileChanged;
	}

	public void setMediaFileChanged(boolean changed) {
		this.mediaFileChanged = changed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Media\n");
		sb.append("\tid: " + getId() + "\n");
		sb.append("\tmedia type: " + getMediaType() + "\n");
		sb.append("\tauthor: " + getAuthor() + "\n");
		sb.append("\ttitle: " + getTitle() + "\n");
		sb.append("\tdescription: " + getDescription() + "\n");
		sb.append("\tdate created: " + getDateCreated() + "\n");
		sb.append("\tdate created gmt: " + getDateCreatedGmt() + "\n");
		sb.append("\tdate modified: " + getDateModified() + "\n");
		sb.append("\tdate modified gmt: " + getDateModifiedGmt() + "\n");
		sb.append("\tdate published: " + getDatePublished() + "\n");
		sb.append("\tdate published gmt: " + getDatePublishedGmt() + "\n");
		sb.append("\tmime: " + getMimeType() + "\n");
		sb.append("\tpath: " + getPath() + "\n");
		sb.append("\tthumbnail path: " + getThumbnailImagePath() + "\n");
		sb.append("\tcontent image path: " + getContentImagePath() + "\n");
		sb.append("\tlatitude: " + getLatitude() + "\n");
		sb.append("\tlongitude: " + getLongitude() + "\n");
		sb.append("\taltitude: " + getAltitude() + "\n");
		sb.append("\tdirection: " + getDirection() + "\n");
		sb.append("\tmeta: " + getMeta() + "\n");
		sb.append("\tdeleted: " + isDeleted() + "\n");
		sb.append("\tpublished: " + isPublished());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractMediaObject) {
			AbstractMediaObject other = (AbstractMediaObject) o;
			return getId() != null && other.getId() != null
					&& getId().equals(other.getId());
		}
		return false;
	}

	public boolean isMediaFileAvailable() {
		String path = getPath();
		if (path != null && !path.equals("")) {
			File file = new File(path);
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}
}
