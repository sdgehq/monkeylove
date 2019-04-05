package fi.oulu.mediabrowserlite.ui;

import java.util.List;

import edu.umd.cs.piccolo.activities.PActivity;
import fi.oulu.mediabrowserlite.MediaBrowser;

/**
 * Yet another stupid dummy class that does nothing
 * @author virtu
 *
 */
public class MediaGroupCanvas extends MediaCanvas implements
		PActivity.PActivityDelegate {

	public static final String UNKNOWN_AUTHOR = MediaBrowser
			.getString("UNKNOWN_LABEL");

	public enum Grouping {
		AUTHOR, DATE
	};

	public Grouping getGrouping() {
		return Grouping.AUTHOR;
	}

	public void setGrouping(Grouping grouping) {
	}

	public double getTitleSectionWidth() {
		return 100;
	}

	public void setTitleSectionWidth(double width) {
	}

	public double getGroupSpacing() {
		return 0;
	}

	public void setGroupSpacing(double spacing) {
	}

	@Override
	protected void organizeMediaNodes(List<MediaNode> nodes) {
	
	}

	public synchronized void reGroupMediaNodes() {
	}


	public void activityFinished(PActivity activity) {
	}

	public void activityStarted(PActivity activity) {
	}

	public void activityStepped(PActivity activity) {
	}
}
