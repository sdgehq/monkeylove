package fi.oulu.mediabrowserlite;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaManager;
import fi.oulu.mediabrowserlite.media.MediaObject;
import fi.oulu.mediabrowserlite.media.provider.DefaultMediaProvider;
import fi.oulu.mediabrowserlite.ui.MediaBrowserFrame;
import fi.oulu.mediabrowserlite.ui.MediaBrowserFrame.ApplicationMode;

public class MediaBrowser {

	public static final String SETTINGS = "MediaBrowserSettings";
	private static final String DEFAULT_SETTINGS_FILE = "settings.properties";
	private static ResourceBundle localizedStrings = null;

	public static void main(String[] args) {
		String settingsFilePath = null;
		if (args.length > 0) {
			File file = new File(args[0]);
			if (file.exists()) {
				settingsFilePath = file.getAbsolutePath();
			} else {
				System.out
						.println("The first parameter for the application should be a path to a settings file. File \""
								+ args[0] + "\" does not exist.");
			}
		} else {
			System.out.println("No settings file provided as a parameter.");
		}
		if (settingsFilePath == null) {
			File file = new File(DEFAULT_SETTINGS_FILE);
			if (file.exists()) {
				System.out.println("Using default settings file.");
				settingsFilePath = file.getAbsolutePath();
			} else {
				System.out.println("The default settings file "
						+ DEFAULT_SETTINGS_FILE + " does not exist.");
			}
		}

		if (settingsFilePath == null) {
			System.out
					.println("No settings file available. Using hard coded default values.");
		} else {
			System.out.println("Settings file: " + settingsFilePath);
			MediaBrowserSettings.getInstance().load(settingsFilePath);
		}

		System.out.println(MediaBrowserSettings.getInstance());

		new MediaBrowser();
	}

	private static MediaBrowserFrame mediaBrowserFrame;

	public static void resetUndoManager() {
		System.out.println("MediaBrowser: resetting undo manager");
		mediaBrowserFrame.resetUndoManager();
	}

	public MediaBrowser() {
		init();
		mediaBrowserFrame = new MediaBrowserFrame();
		loadMedias();
		// importTestFiles();
	}

	public static String getString(String key) {
		String string = null;
		if (localizedStrings == null) {
			localizedStrings = ResourceBundle
					.getBundle("fi/oulu/mediabrowserlite/LocalizableStrings");
		}
		try {
			string = localizedStrings.getString(key);
		} catch (Exception e) {
			e.printStackTrace();
			string = key;
		}
		return string;
	}

	private void init() {
		File dir = new File(MediaBrowserSettings.getInstance()
				.getThumbnailDirectory());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		dir = new File(MediaBrowserSettings.getInstance().getMediaDirectory());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				storeMedias();
				storeApplicationData();
				MediaManager.getInstance().prepareForShutdown();
				MediaBrowserSettings.getInstance().store("settings.properties");
				// deleteThumbnails();
			}
		}));

		MediaManager.getInstance().addMediaProvider(new DefaultMediaProvider());
	}

	public static boolean hasMediasToUpload() {
		List<Media> medias = MediaManager.getInstance().getMedias();
		for (Media media : medias) {
			if (media.getMediaType().equals(MediaObject.MEDIA_TYPE_DEFAULT)) {
				return true;
			}
		}

		return false;
	}

	private void loadMedias() {
		List<Media> medias = MediaManager.getInstance().load();

		// if (MediaManager.getInstance().getMedias().isEmpty()) {
		// importTestFiles();
		// }
	}

	private void storeMedias() {
		MediaManager.getInstance().updateLocalStorage();
	}

	private void storeApplicationData() {
		MediaManager.getInstance().storeApplicationData();
	}

	private void importTestFiles() {
		MediaImporter.importFiles("test_files", null);
	}

	private void deleteThumbnails() {
		File thumbDir = new File(MediaBrowserSettings.getInstance()
				.getThumbnailDirectory());
		if (thumbDir.exists()) {
			File[] thumbs = thumbDir.listFiles();
			for (File thumb : thumbs) {
				thumb.delete();
			}
		}
	}
}
