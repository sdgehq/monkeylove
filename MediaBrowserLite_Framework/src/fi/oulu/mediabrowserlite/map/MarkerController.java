package fi.oulu.mediabrowserlite.map;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import fi.oulu.mediabrowserlite.ui.event.MediaInputEventListener;

/**
 * Dummy class
 * @author virtu
 *
 */
public class MarkerController implements PropertyChangeListener,
		PInputEventListener, PActivity.PActivityDelegate {
	public enum States {
		NORMAL, SELECTED, ZOOMED
	};

	public MarkerController(MapNode mapNode, PCamera camera,
			ArrayList<Marker> markers) {
	}

	public void activityFinished(PActivity activity) {
	}

	public void activityStarted(PActivity activity) {
	}

	public void activityStepped(PActivity activity) {
	}

	public void addMediaInputEventListener(MediaInputEventListener listener) {
	}

	public void removeMediaInputEventListener(MediaInputEventListener listener) {
	}

	public void propertyChange(PropertyChangeEvent event) {
	}

	public boolean isMarker(PNode node) {
		return false;
	}

	public Marker getMarker(PNode node) {
		return null;
	}

	public void selectMarkers(Point2D point, double viewScale) {
	}

	public boolean isActiveMarker(Point2D point, double viewScale) {
		return false;
	}

	public void showThumb(Marker marker) {
	}

	public boolean fitsScreen(Rectangle2D rectangle) {
		return true;
	}

	public boolean canFit(Rectangle2D rectangle, Rectangle2D otherRectangle) {
		return true;
	}

	public void showThumbs(Point2D center) {
	}

	public void layoutToGrid(Rectangle2D layoutRect) {
	}

	public void clearActiveMarkers() {
	}

	public void lockSelection() {
	}

	public void showFullScreen(Marker marker) {

	}

	public void returnFromFullScreen() {

	}

	public void processEvent(PInputEvent event, int type) {
	}

}