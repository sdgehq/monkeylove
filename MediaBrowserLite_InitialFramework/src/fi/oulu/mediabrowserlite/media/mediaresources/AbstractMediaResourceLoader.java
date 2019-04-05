package fi.oulu.mediabrowserlite.media.mediaresources;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.mediabrowserlite.media.Media;

public abstract class AbstractMediaResourceLoader implements
		MediaResourceLoader {

	private List<String> supportedMimes = new ArrayList<String>();

	public AbstractMediaResourceLoader() {

	}

	public AbstractMediaResourceLoader(String... mimes) {
		if (mimes != null) {
			for (String mime : mimes) {
				supportedMimes.add(mime);
			}
		}
	}

	public List<String> getSupportedMimeTypes() {
		return supportedMimes;
	}

	public void setSupportedMimes(List<String> mimes) {
		this.supportedMimes = mimes;
	}

	public void releaseMediaResource(Media media) {
		// TODO Auto-generated method stub
	}
}
