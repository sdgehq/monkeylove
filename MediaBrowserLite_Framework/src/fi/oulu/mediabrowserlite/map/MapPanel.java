package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Properties;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import fi.oulu.mediabrowserlite.media.Media;

/**
 * This is a dummy version of mappanel that does not show an actual map and pretty much does nothing
 * useful.
 * @author virtu
 *
 */
public class MapPanel extends PCanvas {

	private PPath path;

	public MapPanel() {
		super();
		path = PPath.createRectangle( 0, 0, 800, 800 );
		path.setPaint( new Color( (float)Math.random(), (float)Math.random(), (float)Math.random() ) );
		getLayer().addChild( path );

	}

	/**
	 * Dummy method. This should return a screen coordinate that corresponds
	 * with a geographic position. Instead, it just returns the coordinates as they are
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Point2D geoPointToScreenPoint(double latitude, double longitude) {
		Point2D sPoint = new Point2D.Double( longitude, latitude );
		return sPoint;
	}

	public Point2D geoPointToScreenPoint(Point2D latLon) {
		return geoPointToScreenPoint(latLon.getY(), latLon.getX());
	}

	public Point2D screenPointToGeoPoint(double x, double y) {
		Point2D sPoint = new Point2D.Double( y, x );
		return sPoint;
	}

	public Point2D screenPointToGeoPoint(Point2D xy) {
		return screenPointToGeoPoint(xy.getX(), xy.getY());
	}


	public void centerMap() {
		getCamera().animateViewToCenterBounds( path.getBounds(), true, 500l);
	}

	public void centerMapOnCoordinates(double latitude, double longitude,
			long time) {
	}

	public void centerMapOnCoordinates(double latitude, double longitude) {
		centerMapOnCoordinates(latitude, longitude, 0l);
	}

	public Marker getMarker(Media media) {
		return null;
	}

	public MarkerController getMarkerController() {
		return new MarkerController(null, null, null);
	}

	public void setMarkerController(MarkerController markerController) {
	}

	public CameraMapController getCameraController() {
		return new CameraMapController(new MapNode(), null, getCamera() );
	}

	public void setCameraController(CameraMapController cameraController) {
	}

	public synchronized void setThumbSize(double width, double height) {
	}

	public synchronized double getThumbWidth() {
		return 0;
	}

	public synchronized double getThumbHeight() {
		return 0;
	}

	public void initialize(String mapBasePath, Properties properties) {
		System.out.println("Dummy panel intialized");
	}

	public void setZoomSlider(ZoomSlider zoomSlider) {
	}

	public void updateMarkerPositions() {
	}

	public void zoomToLevel(int level) {
	}

	public void addMedia(Media media) {
	}

	public void removeAllMedia() {
	}

	public void removeMedia(Media media) {
	}

	public void resetMap() {
	}
}
