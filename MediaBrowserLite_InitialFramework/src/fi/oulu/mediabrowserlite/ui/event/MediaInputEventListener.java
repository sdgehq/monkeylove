package fi.oulu.mediabrowserlite.ui.event;

import fi.oulu.mediabrowserlite.media.Media;

public interface MediaInputEventListener {

	public void mediaSelected(Object source,
			Media media);

	public void mediaUnselected(Object source,
			Media media);

	public void mediasUnselected(Object source);

	public void mediaActivated(Object source,
			Media media);

	public void mediaDeactivated(Object source,
			Media media);
	
	public void mediaHovered( Object source, Media media );

}
