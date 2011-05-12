package fi.oulu.mediabrowserlite.media.mediaresources;

import java.util.List;

import fi.oulu.mediabrowserlite.media.Media;

public interface MediaResourceLoader {

	public List<String> getSupportedMimeTypes();

	public Object loadMediaResource(Media media);

	public void releaseMediaResource(Media media);

}
