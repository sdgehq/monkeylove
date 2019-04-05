package fi.oulu.mediabrowserlite;

public class MediaBrowserSettings extends Settings {

	private static final String APPLICATION_DIRECTORY_KEY = "application_directory";
	private static final String CONTENT_IMAGE_FILENAME_PREFIX_KEY = "content_image_prefix";
	private static final String CONTENT_IMAGE_SIZE_KEY = "content_image_size";
	private static final String MAP_NAME_KEY = "map_name";
	private static final String STORE_THUMBNAILS_PERMANENTLY_KEY = "permanent_thumbnails";
	private static final String THUMBNAIL_IMAGE_FILENAME_PREFIX_KEY = "thumbnail_prefix";
	private static final String THUMBNAIL_SIZE_KEY = "thumbnail_size";

	private static final String MEDIA_DIR = "media"
			+ System.getProperty("file.separator");
	private static final String APPLICATION_DATA_DIR = "data"
			+ System.getProperty("file.separator");
	private static final String MAP_DIR = "maps"
			+ System.getProperty("file.separator");
	private static final String STORAGE_DIR = "storage"
			+ System.getProperty("file.separator");
	private static final String THUMBNAIL_DIR = MEDIA_DIR + "thumbnails"
			+ System.getProperty("file.separator");

	private static final int DEFAULT_CONTENT_IMAGE_SIZE = 1000;
	private static final String DEFAULT_CONTENT_IMAGE_FILENAME_PREFIX = "content";
	private static final String DEFAULT_MAP_NAME = "Oulu";
	private static final String DEFAULT_THUMBNAIL_FILENAME_PREFIX = "thumb";
	private static final int DEFAULT_THUMBNAIL_SIZE = 100;

	private static MediaBrowserSettings instance;

	public synchronized static MediaBrowserSettings getInstance() {
		if (instance == null) {
			instance = new MediaBrowserSettings();
		}
		return instance;
	}

	private MediaBrowserSettings() {
		super();
	}

	@Override
	protected void setDefaultSettings() {
		set(APPLICATION_DIRECTORY_KEY, ".");
		set(CONTENT_IMAGE_FILENAME_PREFIX_KEY,
				DEFAULT_CONTENT_IMAGE_FILENAME_PREFIX);
		set(CONTENT_IMAGE_SIZE_KEY, DEFAULT_CONTENT_IMAGE_SIZE);
		set(STORE_THUMBNAILS_PERMANENTLY_KEY, true);
		set(THUMBNAIL_IMAGE_FILENAME_PREFIX_KEY,
				DEFAULT_THUMBNAIL_FILENAME_PREFIX);
		set(THUMBNAIL_SIZE_KEY, DEFAULT_THUMBNAIL_SIZE);
		set(MAP_NAME_KEY, "Oulu");
	}

	@Override
	protected String getPropertiesFileComments() {
		return "MediaBrowser settings";
	}

	public String getApplicationDirectory() {
		String dir = getString(APPLICATION_DIRECTORY_KEY, ".").trim();

		if (dir.equals("")) {
			dir = System.getProperty("user.dir");
		} else if (dir.startsWith(".")) {
			dir = System.getProperty("user.dir")
					+ (dir.length() > 1 ? dir.substring(1) : "");
		} else if (dir.startsWith("~")) {
			dir = System.getProperty("user.home") + dir.substring(1);
		}

		if (!dir.endsWith(System.getProperty("file.separator"))) {
			dir += System.getProperty("file.separator");
		}
		return dir;
	}

	public String getMediaDirectory() {
		return getApplicationDirectory() + MEDIA_DIR;
	}

	public String getThumbnailDirectory() {
		boolean permanentThumbnails = getBoolean(
				STORE_THUMBNAILS_PERMANENTLY_KEY, true);

		if (permanentThumbnails) {
			return getApplicationDirectory() + THUMBNAIL_DIR;
		} else {
			return System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + "MediaBrowserLite"
					+ System.getProperty("file.separator") + "thumbs"
					+ System.getProperty("file.separator");
		}
	}

	public String getMapDirectory() {
		return getApplicationDirectory() + MAP_DIR;
	}

	public String getMapName() {
		return getString(MAP_NAME_KEY, null);
	}

	public String getApplicationDataDirectory() {
		return getApplicationDirectory() + APPLICATION_DATA_DIR;
	}

	public String getStorageDirectory() {
		return getApplicationDirectory() + STORAGE_DIR;
	}

	public int getThumbnailSize() {
		return getInt(THUMBNAIL_SIZE_KEY, DEFAULT_THUMBNAIL_SIZE);
	}

	public String getThumbnailFileNamePrefix() {
		return getString(THUMBNAIL_IMAGE_FILENAME_PREFIX_KEY,
				DEFAULT_THUMBNAIL_FILENAME_PREFIX);
	}

	public String getContentImageFileNamePrefix() {
		return getString(CONTENT_IMAGE_FILENAME_PREFIX_KEY,
				DEFAULT_CONTENT_IMAGE_FILENAME_PREFIX);
	}

	public int getContentImageSize() {
		return getInt(CONTENT_IMAGE_SIZE_KEY, DEFAULT_CONTENT_IMAGE_SIZE);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("MediaBrowser settings\n");
		sb.append("\tapplication directory: " + getApplicationDirectory()
				+ "\n");
		sb.append("\tmedia directory: " + getMediaDirectory() + "\n");
		sb.append("\tthumbnail directory: " + getThumbnailDirectory() + "\n");
		sb.append("\tapplication data directory: "
				+ getApplicationDataDirectory() + "\n");
		sb.append("\tstorage directory: " + getStorageDirectory() + "\n");

		sb.append("\n\tmap directory: " + getMapDirectory() + "\n");
		sb.append("\tmap name: " + getMapName() + "\n");

		sb.append("\n\tcontent image filename prefix: "
				+ getContentImageFileNamePrefix() + "\n");
		sb.append("\tcontent image size: " + getContentImageSize() + "\n");
		sb.append("\tthumbnaile filename prefix: "
				+ getThumbnailFileNamePrefix() + "\n");
		sb.append("\tthumbnail size: " + getThumbnailSize() + "\n");
		return sb.toString();
	}

}
