package fi.oulu.mediabrowserlite.ui;

import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Another dummy class that does nothing useful
 * @author virtu
 *
 */
public class MediaGrid extends MediaCanvas {

	public enum Mode {
		HORIZONTAL_BAND, VERTICAL_BAND, HORIZONTAL_GRID, VERTICAL_GRID
	}

	private Mode mode = Mode.VERTICAL_GRID;
	

	public MediaGrid(Mode mode) {
		super();
		this.mode = mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() {
		return this.mode;
	}

	@Override
	public synchronized void organizeMediaNodes(List<MediaNode> nodes) {
	}
}
