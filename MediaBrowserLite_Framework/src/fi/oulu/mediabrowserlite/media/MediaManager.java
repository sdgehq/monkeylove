package fi.oulu.mediabrowserlite.media;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.mediabrowserlite.media.provider.MediaProvider;

/**
 * This is a dummy class for testing that does nothing
 * @author virtu
 *
 */
public class MediaManager implements MediaProvider {

	private static MediaManager instance;

	public synchronized static MediaManager getInstance() {
		if (instance == null) {
			instance = new MediaManager();
		}
		return instance;
	}

	private List<MediaManagerObserver> observers = new ArrayList<MediaManagerObserver>();
	private List<MediaProvider> providers = new ArrayList<MediaProvider>();
	private List<Media> medias = new ArrayList<Media>();

	private MediaManager() {

	}

	public void addObserver(MediaManagerObserver observer) {
	}

	public void removeObserver(MediaManagerObserver observer) {
	}

	public boolean isSupported(Media media) {
		return false;
	}

	public boolean canConvert(Media media, String destinationMediaType) {
		return false;
	}

	public boolean contains(Media media) {
		boolean contains = false;
		return contains;
	}

	public List<Media> getMedias() {
		List<Media> mediaList = new ArrayList<Media>();
		return mediaList;
	}

	public void updateLocalStorage() {
	}

	public boolean newMedia(Media media) {
		return newMedia(media, false);
	}

	public boolean newMedia(Media media, boolean internalAction) {
		return false;
	}

	public boolean updateMedia(Media media) {
		return updateMedia(media, false);
	}

	public boolean updateMedia(Media media, boolean internalAction) {
		return false;
	}

	public boolean deleteMedia(Media media) {
		return deleteMedia(media, false);
	}

	public boolean deleteMedia(Media media, boolean internalAction) {
		return false;
	}

	public boolean undeleteMedia(Media media, boolean internalAction) {
		return false;
	}

	public boolean updateMediaContent(Media media, boolean internalAction) {
		return false;
	}

	public boolean executeUpdate(Media media) {
		return false;
	}

	public void prepareForShutdown() {
	}

	public Media convert(Media media, String destinationMediaType) {
		return null;
	}

	public void addMediaProvider(MediaProvider provider) {
	}

	public boolean removeMediaProvider(MediaProvider provider) {
		return false;
	}

	public List<MediaProvider> getMediaProviders() {
		return new ArrayList<MediaProvider>(providers);
	}

	public MediaProvider getMediaProvider(Media media) {
		return null;
	}

	public List<Media> load() {
		return getMedias();
	}

	public void reload() {
	}

	public List<Media> getSelectedMedias() {
		List<Media> selectedMedias = new ArrayList<Media>();
		return selectedMedias;
	}

	public boolean clearSelection() {
		return false;
	}
	
	public void selectAll()
	{
	}

	public void storeApplicationData() {
	}
}
