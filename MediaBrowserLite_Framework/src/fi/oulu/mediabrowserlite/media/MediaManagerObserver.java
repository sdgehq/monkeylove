package fi.oulu.mediabrowserlite.media;

public interface MediaManagerObserver {

	public void mediaAdded(Media media);

	public void mediaRemoved(Media media);

	public void startedLoading();

	public void finishedLoading();

}
