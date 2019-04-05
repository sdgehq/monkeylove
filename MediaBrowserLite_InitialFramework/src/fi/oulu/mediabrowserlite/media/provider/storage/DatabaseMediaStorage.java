package fi.oulu.mediabrowserlite.media.provider.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class DatabaseMediaStorage extends MediaStorage {

	// private Connection connection;

	public DatabaseMediaStorage(String mediaIdPrefix) {
		super(mediaIdPrefix);
		initDB();
	}

	public DatabaseMediaStorage(String mediaIdPrefix,
			String... supportedMediaTypes) {
		super(mediaIdPrefix, supportedMediaTypes);
		initDB();
	}

	private void initDB() {
		String path = getDatabasePath();

		File dbFile = new File(path);
		if (!dbFile.exists()) {
			dbFile.getParentFile().mkdirs();
		}

		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = getConnection();

			List<String> initStatements = getInitStatements();
			if (initStatements != null && !initStatements.isEmpty()) {
				Statement statement = connection.createStatement();
				for (String stmt : initStatements) {
					statement.addBatch(stmt);
				}
				statement.executeBatch();
				statement.close();
			}
			closeConnection(connection);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean autocommit = true;

	private Connection connection;

	protected Connection getConnection() {
		try {
			// if (connection == null) {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:"
					+ getDatabasePath());
			connection.setAutoCommit(autocommit);
			// }

			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void closeConnection(Connection connection) {
		try {
			if (!autocommit) {
				connection.commit();
			}
			// connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected abstract String getDatabasePath();

	protected abstract List<String> getInitStatements();
}
