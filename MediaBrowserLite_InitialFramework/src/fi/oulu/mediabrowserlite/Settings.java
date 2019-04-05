package fi.oulu.mediabrowserlite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class Settings {

	private static Map<String, Settings> settingsCollection = new HashMap<String, Settings>();

	public static Settings getSettings(String name) {
		return settingsCollection.get(name);
	}

	public static void putSettings(String name, Settings settings) {
		settingsCollection.put(name, settings);
	}

	public static Settings removeSettings(String name) {
		return settingsCollection.remove(name);
	}

	private String settingsFile;
	private Properties properties = new Properties();;

	public Settings() {
		setDefaultSettings();
	}

	public Settings(String settingsFilePath) {
		load(settingsFilePath);
	}

	public String getString(String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return defaultValue;
	}

	public int getInt(String key, int defaultValue) {
		if (properties.containsKey(key)) {
			try {
				return Integer.parseInt(properties.getProperty(key));
			} catch (NumberFormatException e) {
				System.out.println("Settings.getInt, " + key
						+ " is not an integer");
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	public long getLong(String key, long defaultValue) {
		if (properties.containsKey(key)) {
			try {
				return Long.parseLong(properties.getProperty(key));
			} catch (NumberFormatException e) {
				System.out.println("Settings.getLong, " + key
						+ " is not a long");
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	public double getDouble(String key, double defaultValue) {
		if (properties.containsKey(key)) {
			try {
				return Double.parseDouble(properties.getProperty(key));
			} catch (NumberFormatException e) {
				System.out.println("Settings.getDouble, " + key
						+ " is not a double");
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		if (properties.containsKey(key)) {
			try {
				return Boolean.parseBoolean(properties.getProperty(key));
			} catch (NumberFormatException e) {
				System.out.println("Settings.getBoolean, " + key
						+ " is not a boolean");
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	public void set(String key, Object value) {
		if (value instanceof String) {
			properties.setProperty(key, (String) value);
		} else if (value instanceof Integer || value instanceof Double
				|| value instanceof Long) {
			properties.setProperty(key, "" + value);
		} else if (value instanceof Boolean) {
			properties.setProperty(key, Boolean.toString((Boolean) value));
		}
	}

	protected abstract void setDefaultSettings();

	public void load(String settingsFile) {
		try {
			this.settingsFile = settingsFile;
			File file = new File(settingsFile);
			this.properties = new Properties();
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void store() {
		if (this.settingsFile == null) {
			throw new IllegalStateException("Settings file is null");
		}
		store(settingsFile);
	}

	public void store(String file) {
		File f = new File(file);
		if (f.getParentFile() != null) {
			f.getParentFile().mkdirs();
		}
		try {
			if (this.properties != null) {
				this.properties.store(new FileOutputStream(f),
						getPropertiesFileComments());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getPropertiesFileComments() {
		return "";
	}
}
