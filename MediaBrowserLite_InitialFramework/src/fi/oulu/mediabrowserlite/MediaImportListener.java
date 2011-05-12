package fi.oulu.mediabrowserlite;

public interface MediaImportListener {

	public void mediaImportStarted(int mediaCount);

	public void mediaImportStarted(String path);

	public void mediaImportFinished(String path);

	public void mediaImportFinished();

}
