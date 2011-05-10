package fi.oulu.mediabrowserlite.media;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;

import fi.oulu.mediabrowserlite.MediaBrowser;
import fi.oulu.mediabrowserlite.media.Media.Type;
import fi.oulu.mediabrowserlite.media.mediaresources.MediaResourceManager;

public class UndoableMediaEdit extends AbstractUndoableEdit {
	public static final String MEDIA_TYPE_PRESENTATION_LABEL = MediaBrowser
			.getString("MEDIA_TYPE_PRESENTATION_LABEL");
	public static final String AUTHOR_PRESENTATION_LABEL = MediaBrowser
			.getString("AUTHOR_PRESENTATION_LABEL");
	public static final String TITLE_PRESENTATION_LABEL = MediaBrowser
			.getString("TITLE_PRESENTATION_LABEL");
	public static final String DESCRIPTION_PRESENTATION_LABEL = MediaBrowser
			.getString("DESCRIPTION_PRESENTATION_LABEL");
	public static final String MIME_TYPE_PRESENTATION_LABEL = MediaBrowser
			.getString("MIME_TYPE_PRESENTATION_LABEL");
	public static final String PATH_PRESENTATION_LABEL = MediaBrowser
			.getString("PATH_PRESENTATION_LABEL");
	public static final String LATITUDE_PRESENTATION_LABEL = MediaBrowser
			.getString("LATITUDE_PRESENTATION_LABEL");
	public static final String LONGITUDE_PRESENTATION_LABEL = MediaBrowser
			.getString("LONGITUDE_PRESENTATION_LABEL");
	public static final String POSITION_PRESENTATION_LABEL = MediaBrowser
			.getString("POSITION_PRESENTATION_LABEL");
	public static final String DIRECTION_PRESENTATION_LABEL = MediaBrowser
			.getString("DIRECTION_PRESENTATION_LABEL");
	public static final String ALTITUDE_PRESENTATION_LABEL = MediaBrowser
			.getString("ALTITUDE_PRESENTATION_LABEL");
	public static final String META_PRESENTATION_LABEL = MediaBrowser
			.getString("META_PRESENTATION_LABEL");
	public static final String TYPE_PRESENTATION_LABEL = MediaBrowser
			.getString("TYPE_PRESENTATION_LABEL");
	public static final String DELETE_PRESENTATION_LABEL = MediaBrowser
			.getString("DELETE_PRESENTATION_LABEL");
	public static final String ROTATE_PRESENTATION_LABEL = MediaBrowser
			.getString("ROTATE_PRESENTATION_LABEL");

	public enum Attributes {
		MEDIA_TYPE, AUTHOR, TITLE, DESCRIPTION, MIME_TYPE, PATH, META, LATITUDE, LONGITUDE, POSITION, DIRECTION, ALTITUDE, TYPE, DELETE, ROTATE
	};

	private Media media;
	private Attributes attribute;
	private Object value1 = null;
	private Object value2 = null;
	private Object oldValue1 = null;
	private Object oldValue2 = null;
	private String presentationLabel;
	private Date modifiedDate = null;
	private Date modifiedDateGmt = null;

	private List<Object> values = new ArrayList<Object>();
	private List<Object> oldValues = new ArrayList<Object>();

	private UndoableMediaEdit(Media media, Attributes attribute, Object value1,
			Object value2) {
		super();
		this.media = media;
		this.attribute = attribute;
		this.value1 = value1;
		this.value2 = value2;
	}

	public static UndoableMediaEdit setMediaType(Media media, String mediaType) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.MEDIA_TYPE, mediaType, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setAuthor(Media media, String author) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.AUTHOR, author, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setTitle(Media media, String title) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media, Attributes.TITLE,
				title, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setDescription(Media media,
			String description) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.DESCRIPTION, description, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setMimeType(Media media, String mimeType) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.MIME_TYPE, mimeType, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setPath(Media media, String path) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media, Attributes.PATH,
				path, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setMeta(Media media, String key,
			String value) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media, Attributes.META,
				key, value);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setLatitude(Media media, double latitude) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.LATITUDE, new Double(latitude), null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setLongitude(Media media, double longitude) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.LONGITUDE, new Double(longitude), null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setPosition(Media media, double latitude,
			double longitude) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.POSITION, new Double(latitude),
				new Double(longitude));
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setDirection(Media media, double direction) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.DIRECTION, new Double(direction), null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setAltitude(Media media, double altitude) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.ALTITUDE, new Double(altitude), null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit setType(Media media, Media.Type type) {
		String typeString = "neutral";
		if (type == Media.Type.POSITIVE) {
			typeString = "positive";
		} else if (type == Media.Type.NEGATIVE) {
			typeString = "negative";
		} 
		UndoableMediaEdit edit = new UndoableMediaEdit(media, Attributes.TYPE,
				typeString, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit delete(Media media) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.DELETE, null, null);
		edit.initialize();
		return edit;
	}

	public static UndoableMediaEdit rotate(Media media, int degrees) {
		UndoableMediaEdit edit = new UndoableMediaEdit(media,
				Attributes.ROTATE, degrees, null);
		edit.initialize();
		return edit;
	}

	private void setOldValues(Object... values) {
		oldValues.clear();
		for (Object value : values) {
			oldValues.add(value);
		}
	}

	private Object getOldValue(int index) {
		return oldValues.get(index);
	}

	public void initialize() {
				
		boolean updateMedia = true;

		switch (attribute) {
		case MEDIA_TYPE:
			oldValue1 = media.getMediaType();
			media.setMediaType((String) value1);
			presentationLabel = MEDIA_TYPE_PRESENTATION_LABEL;
			break;
		case AUTHOR:
			oldValue1 = media.getAuthor();
			media.setAuthor((String) value1);
			presentationLabel = AUTHOR_PRESENTATION_LABEL;
			break;
		case TITLE:
			oldValue1 = media.getTitle();
			media.setTitle((String) value1);
			presentationLabel = TITLE_PRESENTATION_LABEL;
			break;
		case DESCRIPTION:
			oldValue1 = media.getDescription();
			media.setDescription((String) value1);
			presentationLabel = DESCRIPTION_PRESENTATION_LABEL;
			break;
		case MIME_TYPE:
			oldValue1 = media.getMimeType();
			media.setMimeType((String) value1);
			presentationLabel = MIME_TYPE_PRESENTATION_LABEL;
			break;
		case PATH:
			oldValue1 = media.getPath();
			media.setPath((String) value1);
			presentationLabel = PATH_PRESENTATION_LABEL;
			break;
		case META:
			oldValue1 = value1;
			oldValue2 = media.getMeta((String) value1);
			media.setMeta((String) value1, (String) value2);
			presentationLabel = META_PRESENTATION_LABEL + " " + value1;
			break;
		case LATITUDE:
			oldValue1 = new Double(media.getLatitude());
			media.setLatitude(((Double) value1).doubleValue());
			presentationLabel = LATITUDE_PRESENTATION_LABEL;
			break;
		case LONGITUDE:
			oldValue1 = new Double(media.getLongitude());
			media.setLongitude(((Double) value1).doubleValue());
			presentationLabel = LONGITUDE_PRESENTATION_LABEL;
			break;
		case POSITION:
			oldValue1 = new Double(media.getLatitude());
			oldValue2 = new Double(media.getLongitude());
			media.setLatitude(((Double) value1).doubleValue());
			media.setLongitude(((Double) value2).doubleValue());
			presentationLabel = POSITION_PRESENTATION_LABEL;
			break;
		case DIRECTION:
			oldValue1 = new Double(media.getDirection());
			media.setDirection(((Double) value1).doubleValue());
			presentationLabel = DIRECTION_PRESENTATION_LABEL;
			break;
		case ALTITUDE:
			oldValue1 = new Double(media.getAltitude());
			media.setAltitude(((Double) value1).doubleValue());
			presentationLabel = ALTITUDE_PRESENTATION_LABEL;
			break;
		case TYPE:
			Media.Type type = media.getType();
			if (type == Media.Type.POSITIVE) {
				oldValue1 = "positive";
			} else if (type == Media.Type.NEGATIVE) {
				oldValue1 = "negative";
			} else {
				oldValue1 = "neutral";
			}
			Media.Type newType;
			if (value1.equals("positive")) {
				newType = Media.Type.POSITIVE;
			} else if (value1.equals("negative")) {
				newType = Media.Type.NEGATIVE;
			} else {
				newType = Media.Type.NEUTRAL;
			}
			media.setType(newType);
			presentationLabel = TYPE_PRESENTATION_LABEL;
			break;
		case DELETE:
			// media.setDeleted(true);
			updateMedia = false;
			MediaManager.getInstance().deleteMedia(media, false);
			MediaManager.getInstance().executeUpdate(media);
			presentationLabel = DELETE_PRESENTATION_LABEL;
			break;
		case ROTATE:
			presentationLabel = ROTATE_PRESENTATION_LABEL;
			updateMedia = false;
			oldValue1 = -(Integer) value1;
			List<Media> medias = new ArrayList<Media>();
			medias.add(media);
			MediaResourceManager.getInstance().rotate(medias, (Integer) value1,
					new BasicRotateListener() {
						public void rotateFinished(Media media) {
							MediaManager.getInstance().updateMediaContent(
									media, false);
							MediaManager.getInstance().executeUpdate(media);
						}
					});
			break;
		}

		if (updateMedia) {
			modifiedDate = media.getDateModified();
			modifiedDateGmt = media.getDateModifiedGmt();

			Date newModifiedDate = new Date();
			Date newModifiedGmtDate = MediaHelper.toGmt(newModifiedDate);

			media.setDateModified(newModifiedDate);
			media.setDateModifiedGmt(newModifiedGmtDate);
			MediaManager.getInstance().updateMedia(media);
			MediaManager.getInstance().executeUpdate(media);
		}
	}

	@Override
	public String getPresentationName() {
		return presentationLabel;
	}

	@Override
	public void redo() {
		super.redo();
		initialize();
		// MediaManager.getInstance().executeUpdate(media);
	}

	@Override
	public void undo() {
		boolean updateMedia = true;

		super.undo();
		switch (attribute) {
		case MEDIA_TYPE:
			media.setMediaType((String) oldValue1);
			break;
		case AUTHOR:
			media.setAuthor((String) oldValue1);
			break;
		case TITLE:
			media.setTitle((String) oldValue1);
			break;
		case DESCRIPTION:
			media.setDescription((String) oldValue1);
			break;
		case MIME_TYPE:
			media.setMimeType((String) oldValue1);
			break;
		case PATH:
			media.setPath((String) oldValue1);
			break;
		case META:
			media.setMeta((String) oldValue1, (String) oldValue2);
			break;
		case LATITUDE:
			media.setLatitude(((Double) oldValue1).doubleValue());
			break;
		case LONGITUDE:
			media.setLongitude(((Double) oldValue1).doubleValue());
			break;
		case POSITION:
			media.setLatitude(((Double) oldValue1).doubleValue());
			media.setLongitude(((Double) oldValue2).doubleValue());
			break;
		case DIRECTION:
			media.setDirection(((Double) oldValue1).doubleValue());
			break;
		case ALTITUDE:
			media.setAltitude(((Double) oldValue1).doubleValue());
			break;
		case TYPE:
			Media.Type newType;
			if (oldValue1.equals("positive")) {
				newType = Media.Type.POSITIVE;
			} else if (oldValue1.equals("negative")) {
				newType = Media.Type.NEGATIVE;
			} else {
				newType = Media.Type.NEUTRAL;
			}
			media.setType(newType);
			break;
		case DELETE:
			updateMedia = false;
			MediaManager.getInstance().undeleteMedia(media, false);
			MediaManager.getInstance().executeUpdate(media);
			// media.setDeleted(false);
			break;
		case ROTATE:
			updateMedia = false;
			List<Media> medias = new ArrayList<Media>();
			medias.add(media);
			MediaResourceManager.getInstance().rotate(medias,
					(Integer) oldValue1, new BasicRotateListener() {
						public void rotateFinished(Media media) {
							MediaManager.getInstance().updateMediaContent(
									media, false);
							MediaManager.getInstance().executeUpdate(media);
						}
					});
			break;

		}
		if (updateMedia) {
			media.setDateModified(modifiedDate);
			media.setDateModifiedGmt(modifiedDateGmt);
			MediaManager.getInstance().updateMedia(media);
			MediaManager.getInstance().executeUpdate(media);
		}
	}
}
