package fi.oulu.mediabrowserlite.media.provider.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fi.oulu.mediabrowserlite.MediaBrowserSettings;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaHelper;
import fi.oulu.mediabrowserlite.media.MediaObject;

public class DefaultMediaStorage extends DatabaseMediaStorage {

	public DefaultMediaStorage() {
		super("default_", MediaObject.MEDIA_TYPE_DEFAULT);
	}

	@Override
	public boolean newMedia(Media media) throws IllegalArgumentException {
		if (isSupported(media)) {
			if (!contains(media)) {
				try {
					Connection connection = getConnection();
					PreparedStatement stmt = connection
							.prepareStatement(INSERT_MEDIA_STATEMENT);
					setMediaValues(stmt, media);
					stmt.executeUpdate();

					ResultSet generatedKeys = stmt.getGeneratedKeys();
					media.setId(createId(generatedKeys.getInt(1)));
					stmt.close();

					closeConnection(connection);

					for (String metaKey : media.getMetaKeys()) {
						insertMeta(media, metaKey, media.getMeta(metaKey));
					}
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException(
						"DefaultMediaStorage.newMedia, seems that this media already exists in the storage. Did you mean to update it instead? "
								+ media);
			}
		} else {
			throw new IllegalArgumentException(
					"DefaultMediaStorage.newMedia, media not supported: "
							+ media);
		}
		return false;
	}

	public boolean updateMedia(Media media) throws IllegalArgumentException {
		if (isSupported(media)) {
			if (contains(media)) {
				try {
					Connection connection = getConnection();
					PreparedStatement stmt = connection
							.prepareStatement(UPDATE_MEDIA_STATEMENT);
					setMediaValues(stmt, media);
					stmt.setInt(19, parseId(media.getId()));
					stmt.executeUpdate();

					closeConnection(connection);

					for (String metaKey : media.getMetaKeys()) {
						String oldValue = getMeta(media, metaKey);
						if (oldValue == null) {
							insertMeta(media, metaKey, media.getMeta(metaKey));
						} else {
							updateMeta(media, metaKey, media.getMeta(metaKey));
						}
					}
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException(
						"DefaultMediaStorage.updateMedia, seems that this media does not exist in the storage. Did you mean to call newMedia() instead? "
								+ media);
			}
		} else {
			throw new IllegalArgumentException(
					"DefaultMediaStorage.updateMedia, media not supported: "
							+ media);
		}
		return false;
	}

	public boolean deleteMedia(Media media) throws IllegalArgumentException {
		if (isSupported(media)) {
			if (contains(media)) {
				try {
					Connection connection = getConnection();

					PreparedStatement stmt = connection
							.prepareStatement(DELETE_ALL_META_FOR_MEDIA_STATEMENT);
					stmt.setInt(1, parseId(media.getId()));
					stmt.executeUpdate();
					stmt.close();
					stmt = getConnection().prepareStatement(
							DELETE_MEDIA_STATEMENT);
					stmt.setInt(1, parseId(media.getId()));
					stmt.executeUpdate();
					stmt.close();

					closeConnection(connection);

					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException(
						"DefaultMediaStorage.deleteMedia, seems that this media does not exist in this storage: "
								+ media);
			}
		} else {
			throw new IllegalArgumentException(
					"DefaultMediaStorage.deleteMedia, media not supported: "
							+ media);
		}
		return false;
	}

	private String getMeta(Media media, String key) {
		try {
			Connection connection = getConnection();

			PreparedStatement stmt = connection
					.prepareStatement(GET_META_STATEMENT);
			stmt.setInt(1, parseId(media.getId()));
			stmt.setString(2, key);
			ResultSet result = stmt.executeQuery();
			String value = null;
			if (result.next()) {
				value = result.getString(META_VALUE);
			}
			stmt.close();
			closeConnection(connection);
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void insertMeta(Media media, String key, String value) {
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection
					.prepareStatement(INSERT_META_STATEMENT);
			stmt.setString(1, key);
			stmt.setString(2, media.getMeta(key));
			stmt.setInt(3, parseId(media.getId()));
			stmt.executeUpdate();
			stmt.close();
			closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateMeta(Media media, String key, String value) {
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection
					.prepareStatement(UPDATE_META_STATEMENT);
			stmt.setString(1, media.getMeta(key));
			stmt.setString(2, key);
			stmt.setInt(3, parseId(media.getId()));
			stmt.executeUpdate();
			stmt.close();
			closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteMeta(Media media, String key) {
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection
					.prepareStatement(DELETE_META_STATEMENT);
			stmt.setString(1, key);
			stmt.setInt(2, parseId(media.getId()));
			stmt.executeUpdate();
			stmt.close();
			closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Media> getMedias() {
		List<Media> medias = new ArrayList<Media>();

		try {
			Connection connection = getConnection();
			Statement stmt = connection.createStatement();
			ResultSet result = stmt.executeQuery(GET_ALL_MEDIAS_STATEMENT);
			if (result != null) {
				while (result.next()) {
					Media media = new MediaObject();
					media.setId(createId(result.getInt(MEDIA_ID)));
					media.setMimeType(result.getString(MIME));
					media.setPath(result.getString(PATH));
					media.setAuthor(result.getString(AUTHOR));
					media.setTitle(result.getString(TITLE));
					media.setDescription(result.getString(DESCRIPTION));
					media.setDateCreated(MediaHelper.parseDate(result
							.getString(DATE_CREATED)));
					media.setDateModified(MediaHelper.parseDate(result
							.getString(DATE_MODIFIED)));
					media.setLatitude(result.getDouble(LATITUDE));
					media.setLongitude(result.getDouble(LONGITUDE));
					media.setAltitude(result.getDouble(ALTITUDE));
					media.setDirection(result.getDouble(DIRECTION));
					media
							.setType(MediaHelper.parseType(result
									.getString(TYPE)));

					media.setDeleted(Boolean.parseBoolean(result
							.getString(DELETED)));
					media.setPublished(Boolean.parseBoolean(result
							.getString(PUBLISHED)));

					media.setDateCreatedGmt(MediaHelper.parseDate(result
							.getString(DATE_CREATED_GMT)));
					media.setDateModifiedGmt(MediaHelper.parseDate(result
							.getString(DATE_MODIFIED_GMT)));

					media.setDatePublished(MediaHelper.parseDate(result
							.getString(DATE_PUBLISHED)));
					media.setDatePublishedGmt(MediaHelper.parseDate(result
							.getString(DATE_PUBLISHED_GMT)));

					PreparedStatement pStmt = connection
							.prepareStatement(GET_META_FOR_MEDIA_STATEMENT);
					pStmt.setInt(1, parseId(media.getId()));
					ResultSet metaResult = pStmt.executeQuery();
					if (metaResult != null) {
						while (metaResult.next()) {
							String key = metaResult.getString(META_KEY);
							String value = metaResult.getString(META_VALUE);
							media.setMeta(key, value);
						}
						metaResult.close();
					}
					medias.add(media);
				}
				result.close();
			}
			closeConnection(connection);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return medias;
	}

	private void setMediaValues(PreparedStatement stmt, Media media) {
		try {
			stmt.setString(1, media.getMimeType());
			stmt.setString(2, media.getPath());
			stmt.setString(3, media.getAuthor());
			stmt.setString(4, media.getTitle());
			stmt.setString(5, media.getDescription());
			stmt.setString(6, MediaHelper.toString(media.getDateCreated()));
			stmt.setString(7, MediaHelper.toString(media.getDateModified()));
			stmt.setDouble(8, media.getLatitude());
			stmt.setDouble(9, media.getLongitude());
			stmt.setDouble(10, media.getAltitude());
			stmt.setDouble(11, media.getDirection());
			stmt.setString(12, MediaHelper.toString(media.getType()));
			stmt.setString(13, Boolean.toString(media.isDeleted()));
			stmt.setString(14, Boolean.toString(media.isPublished()));
			stmt.setString(15, MediaHelper.toString(media.getDateCreatedGmt()));
			stmt
					.setString(16, MediaHelper.toString(media
							.getDateModifiedGmt()));
			stmt.setString(17, MediaHelper.toString(media.getDatePublished()));
			stmt.setString(18, MediaHelper
					.toString(media.getDatePublishedGmt()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getDatabasePath() {
		return MediaBrowserSettings.getInstance().getStorageDirectory()
				+ "default_media_storage"
				+ System.getProperty("file.separator")
				+ "default_media_storage.db";
	}

	@Override
	protected List<String> getInitStatements() {
		List<String> statements = new ArrayList<String>();
		statements.add(CREATE_MEDIA_TABLE_STATEMENT);
		statements.add(CREATE_META_TABLE_STATEMENT);
		return statements;
	}

	private static final String MEDIA_TABLE = "media";
	private static final String MEDIA_ID = "media_id";
	private static final String MIME = "mime";
	private static final String PATH = "path";
	private static final String AUTHOR = "author";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String DATE_CREATED = "date_created";
	private static final String DATE_CREATED_GMT = "date_created_gmt";
	private static final String DATE_MODIFIED = "date_modified";
	private static final String DATE_MODIFIED_GMT = "date_modified_gmt";
	private static final String DATE_PUBLISHED = "date_published";
	private static final String DATE_PUBLISHED_GMT = "date_published_gmt";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String ALTITUDE = "altitude";
	private static final String DIRECTION = "direction";
	private static final String TYPE = "type";
	private static final String DELETED = "deleted";
	private static final String PUBLISHED = "published";

	private static final String META_TABLE = "media_meta";
	private static final String META_ID = "meta_id";
	private static final String META_KEY = "meta_key";
	private static final String META_VALUE = "meta_value";

	private static final String CREATE_MEDIA_TABLE_STATEMENT = "create table if not exists "
			+ MEDIA_TABLE
			+ "("
			+ MEDIA_ID
			+ " integer primary key autoincrement, "
			+ MIME
			+ " varchar(16), "
			+ PATH
			+ " varchar(256), "
			+ AUTHOR
			+ " varchar(32), "
			+ TITLE
			+ " varchar(128), "
			+ DESCRIPTION
			+ " varchar(256), "
			+ DATE_CREATED
			+ " varchar(32), "
			+ DATE_MODIFIED
			+ " varchar(32), "
			+ LATITUDE
			+ " double, "
			+ LONGITUDE
			+ " double, "
			+ ALTITUDE
			+ " double, "
			+ DIRECTION
			+ " double, "
			+ TYPE
			+ " varchar(10), "
			+ DELETED
			+ " varchar(5),"
			+ PUBLISHED
			+ " varchar(5),"
			+ DATE_CREATED_GMT
			+ " varchar(32), "
			+ DATE_MODIFIED_GMT
			+ " varchar(32), "
			+ DATE_PUBLISHED
			+ " varchar(32), " + DATE_PUBLISHED_GMT + " varchar(32))";

	private static final String CREATE_META_TABLE_STATEMENT = "create table if not exists "
			+ META_TABLE
			+ "("
			+ META_ID
			+ " integer primary key autoincrement, "
			+ META_KEY
			+ " varchar(32), "
			+ META_VALUE
			+ " varchar(256), "
			+ MEDIA_ID
			+ " integer(9))";

	private static final String GET_ALL_MEDIAS_STATEMENT = "select * from "
			+ MEDIA_TABLE;

	private static final String INSERT_MEDIA_STATEMENT = "insert into "
			+ MEDIA_TABLE + "(" + MIME + ", " + PATH + ", " + AUTHOR + ", "
			+ TITLE + ", " + DESCRIPTION + ", " + DATE_CREATED + ", "
			+ DATE_MODIFIED + ", " + LATITUDE + ", " + LONGITUDE + ", "
			+ ALTITUDE + ", " + DIRECTION + ", " + TYPE + ", " + DELETED + ", "
			+ PUBLISHED + ", " + DATE_CREATED_GMT + ", " + DATE_MODIFIED_GMT
			+ ", " + DATE_PUBLISHED + ", " + DATE_PUBLISHED_GMT
			+ ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_MEDIA_STATEMENT = "update "
			+ MEDIA_TABLE + " set " + MIME + "=?," + PATH + "=?," + AUTHOR
			+ "=?," + TITLE + "=?," + DESCRIPTION + "=?," + DATE_CREATED
			+ "=?," + DATE_MODIFIED + "=?, " + LATITUDE + "=?," + LONGITUDE
			+ "=?," + ALTITUDE + "=?," + DIRECTION + "=?," + TYPE + "=?, "
			+ DELETED + "=?, " + PUBLISHED + "=?, " + DATE_CREATED_GMT + "=?, "
			+ DATE_MODIFIED_GMT + "=?, " + DATE_PUBLISHED + "=?, "
			+ DATE_PUBLISHED_GMT + "=? " + "where " + MEDIA_ID + "=?";

	private static final String DELETE_MEDIA_STATEMENT = "delete from "
			+ MEDIA_TABLE + " where " + MEDIA_ID + "=?";

	private static final String GET_META_FOR_MEDIA_STATEMENT = "select * from "
			+ META_TABLE + " where " + MEDIA_ID + "=?";

	private static final String GET_META_STATEMENT = "select " + META_VALUE
			+ " from " + META_TABLE + " where " + MEDIA_ID + " =? and "
			+ META_KEY + "=?";

	private static final String INSERT_META_STATEMENT = "insert into "
			+ META_TABLE + "(" + META_KEY + ", " + META_VALUE + ", " + MEDIA_ID
			+ ") values(?,?,?)";

	private static final String UPDATE_META_STATEMENT = "update " + META_TABLE
			+ " set " + META_VALUE + "=? where " + META_KEY + "=? and "
			+ MEDIA_ID + "=?";

	private static final String DELETE_META_STATEMENT = "delete from "
			+ META_TABLE + " where " + META_KEY + "=? and " + MEDIA_ID + "=?";

	private static final String DELETE_ALL_META_FOR_MEDIA_STATEMENT = "delete from "
			+ META_TABLE + " where " + MEDIA_ID + "=?";

}
