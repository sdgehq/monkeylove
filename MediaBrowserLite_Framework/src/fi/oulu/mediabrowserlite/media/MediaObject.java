package fi.oulu.mediabrowserlite.media;

import java.util.Date;

public class MediaObject extends AbstractMediaObject {

	public static final String MEDIA_TYPE_DEFAULT = "media_type_default";

	private String author = "";
	private String title = "";
	private String description = "";
	private Date dateCreated;
	private Date dateCreatedGmt;
	private Date dateModified;
	private Date dateModifiedGmt;

	private Date datePublished;
	private Date datePublishedGmt;

	private Type type = Type.NEUTRAL;
	private boolean published = false;

	private double latitude = NOT_SPECIFIED_LATITUDE;
	private double longitude = NOT_SPECIFIED_LONGITUDE;
	private double altitude = NOT_SPECIFIED_ALTITUDE;
	private double direction = NOT_SPECIFIED_DIRECTION;

	public MediaObject() {
		setMediaType(MEDIA_TYPE_DEFAULT);
	}

	public MediaObject(String path, String mimeType) {
		this();
		setPath(path);
		setMimeType(mimeType);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		String oldAuthor = getAuthor();
		this.author = author;
		broadcastAuthorChanged(oldAuthor);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		if (dateModified != null) {
			return dateModified;
		} else {
			return dateCreated;
		}
	}

	public void setDateModified(Date date) {
		this.dateModified = date;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date date) {
		this.datePublished = date;
	}

	public Date getDatePublishedGmt() {
		return datePublishedGmt;
	}

	public void setDatePublishedGmt(Date date) {
		this.datePublishedGmt = date;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		broadcastMediaContentChanged(this);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getDateCreatedGmt() {
		return this.dateCreatedGmt;
	}

	public Date getDateModifiedGmt() {
		return this.dateModifiedGmt;
	}

	public void setDateCreatedGmt(Date date) {
		this.dateCreatedGmt = date;
	}

	public void setDateModifiedGmt(Date date) {
		this.dateModifiedGmt = date;
	}

	// @Override
	// public boolean equals(Object o) {
	// if (o instanceof MediaObject) {
	// MediaObject mo = (MediaObject) o;
	// return getPath() != null && mo.getPath() != null
	// && getPath().equals(mo.getPath());
	// }
	// return false;
	// }

}
