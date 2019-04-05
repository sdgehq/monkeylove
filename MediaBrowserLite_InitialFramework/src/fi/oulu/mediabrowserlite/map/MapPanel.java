package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Properties;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import fi.oulu.mediabrowserlite.media.BasicMediaListener;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaListener;

public class MapPanel extends PCanvas {

	public static final String MAP_WEST_KEY = "mapwest";
	public static final String MAP_NORTH_KEY = "mapnorth";
	public static final String MAP_EAST_KEY = "mapeast";
	public static final String MAP_SOUTH_KEY = "mapsouth";
	public static final String MAP_PATH_KEY = "map_files_dir";
	public static final String MAP_URL_KEY = "map_files_base_url";
	public static final String INITIAL_ZOOM_LEVEL = "initial_zoom_level";
	public static final String MAX_ZOOM_LEVEL = "max_zoom_level";
	public static final String TILE_WIDTH = "tile_width";
	public static final String TILE_HEIGHT = "tile_height";
	public static final String X_TILES = "x_tiles";
	public static final String Y_TILES = "y_tiles";
	public static final String INITIAL_X_TILE_INDEX = "initial_x_tile";
	public static final String INITIAL_Y_TILE_INDEX = "initial_y_tile";

	private MapNode mapNode;
	private CameraMapController cameraController = null;
	private MarkerController markerController = null;
	private double thumbWidth = 40;
	private double thumbHeight = 30;

	private ArrayList<Marker> markers;
	private MediaListener mediaListener = new MapMediaObserver();

	double mapWest = 0;
	double mapNorth = 0;
	double mapEast = 0;
	double mapSouth = 0;
	Rectangle2D mapArea = new Rectangle2D.Double(mapWest, mapNorth, Math
			.abs(mapEast - mapWest), Math.abs(mapNorth - mapSouth));

	public MapPanel() {
		super();
		mapNode = new MapNode();
		getLayer().addChild(mapNode);
		markers = new ArrayList<Marker>();

		cameraController = new CameraMapController(mapNode, null, getCamera());
		markerController = new MarkerController(mapNode, getCamera(), markers);

		// addCoordinateConversionTestMarkers();
		updateMarkerPositions();
	}
	
	public void addCustomMouseMotionListener(MouseMotionListener mml) {
		this.addMouseMotionListener(mml);
	}

	public Point2D geoPointToScreenPoint(double latitude, double longitude) {
		Rectangle viewport = mapNode.getViewport(getCamera());
		Point2D sPoint = CoordinateConverter.geoPointToScreenPoint(mapWest,
				mapNorth, mapEast, mapSouth, viewport, new Point2D.Double(
						longitude, latitude));
		return sPoint;
	}

	public Point2D geoPointToScreenPoint(Point2D latLon) {
		return geoPointToScreenPoint(latLon.getY(), latLon.getX());

		// Rectangle viewport = new
		// Rectangle(mapNode.getTotalWidth(getCamera()),
		// mapNode.getTotalHeight(getCamera()));
		//
		// Point2D mPoint = CoordinateConverter.geoPointToScreenPoint(mapArea,
		// viewport, geoPoint.getY(), geoPoint.getX());
		// return mPoint;
	}

	public Point2D screenPointToGeoPoint(double x, double y) {
		Point2D sPoint = new Point2D.Double(x, y);
		sPoint = getCamera().localToView(sPoint);
		Rectangle viewport = mapNode.getViewport(getCamera());
		return CoordinateConverter.screenPointToGeoPoint(mapWest, mapNorth,
				mapEast, mapSouth, viewport, sPoint);
	}

	public Point2D screenPointToGeoPoint(Point2D xy) {
		return screenPointToGeoPoint(xy.getX(), xy.getY());

		// Rectangle viewport = new
		// Rectangle(mapNode.getTotalWidth(getCamera()),
		// mapNode.getTotalHeight(getCamera()));
		// Point2D globalPoint = getCamera().localToView(screenPoint);
		// Point2D mPoint = CoordinateConverter.screenPointToGeoPoint(mapArea,
		// viewport, globalPoint);
		// return mPoint;
	}

	private void addCoordinateConversionTestMarkers() {
		addConversionTestMarker(mapNorth, mapWest);
		addConversionTestMarker(mapNorth, mapEast);
		addConversionTestMarker(mapSouth, mapWest);
		addConversionTestMarker(mapSouth, mapEast);
		addConversionTestMarker(mapNorth - (mapNorth - mapSouth) / 2, mapWest
				+ (mapEast - mapWest) / 2);
	}

	private void addConversionTestMarker(double latitude, double longitude) {
		int zoomLevel = mapNode.getZoomLevel(getCamera().getViewScale()) + 1;
		// int width = mapNode.xTiles * mapNode.tileWidth * zoomLevel;
		// int height = mapNode.yTiles * mapNode.tileHeight * zoomLevel;
		int width = mapNode.getTotalWidth(getCamera());
		int height = mapNode.getTotalHeight(getCamera());

		Rectangle viewport = new Rectangle(width, height);

		PPath marker = PPath.createRectangle(0, 0, 5, 5);
		marker.setPaint(Color.RED);
		getLayer().addChild(marker);

		// Point2D mPoint = CoordinateConverter.geoPointToScreenPoint(mapArea,
		// viewport, latitude, longitude);

		Point2D sPoint = geoPointToScreenPoint(latitude, longitude);

		mapNode.globalToLocal(sPoint);
		marker.setOffset(sPoint.getX() - marker.getWidth() / 2, sPoint.getY()
				- marker.getHeight() / 2);
	}

	public void centerMap() {
		PCamera camera = getCamera();
		PBounds bounds = mapNode.getBounds();
		PBounds newBounds = new PBounds((bounds.getWidth() - getWidth()) / 2.0,
				(bounds.getHeight() - getHeight()) / 2.0, getWidth(),
				getHeight());

		camera.animateViewToCenterBounds(newBounds, true, 500l);
	}

	public void centerMapOnCoordinates(double latitude, double longitude,
			long time) {
		PCamera camera = getCamera();
		Rectangle viewport = new Rectangle(mapNode.getTotalWidth(camera),
				mapNode.getTotalHeight(camera));

		// Point2D mPoint = CoordinateConverter.geoPointToScreenPoint(mapArea,
		// viewport, latitude, longitude);

		Point2D mPoint = geoPointToScreenPoint(latitude, longitude);

		PBounds newBounds = new PBounds(mPoint.getX(), mPoint.getY(), 100.0,
				100.0);
		newBounds.moveBy(-newBounds.getWidth() / 2.0,
				-newBounds.getHeight() / 2.0);
		camera.animateViewToCenterBounds(newBounds, false, time);
	}

	public void centerMapOnCoordinates(double latitude, double longitude) {
		centerMapOnCoordinates(latitude, longitude, 0l);
	}

	public Marker getMarker(Media media) {
		for (Marker marker : markers) {
			if (marker.getMedia() == media) {
				return marker;
			}
		}
		return null;
	}

	public MarkerController getMarkerController() {
		return markerController;
	}

	public void setMarkerController(MarkerController markerController) {
		if (this.markerController != null) {
			getCamera().removePropertyChangeListener(this.markerController);
			mapNode.removeInputEventListener(this.markerController);
		}
		this.markerController = markerController;
	}

	public CameraMapController getCameraController() {
		return cameraController;
	}

	public void setCameraController(CameraMapController cameraController) {
		if (this.cameraController != null) {
			getCamera().removePropertyChangeListener(this.cameraController);
			mapNode.removeInputEventListener(this.cameraController);
		}
		this.cameraController = cameraController;
	}

	public synchronized void setThumbSize(double width, double height) {
		this.thumbWidth = width;
		this.thumbHeight = height;
		for (Marker marker : markers) {
			marker.setThumbSize(width, height);
		}
	}

	public synchronized double getThumbWidth() {
		return this.thumbWidth;
	}

	public synchronized double getThumbHeight() {
		return this.thumbHeight;
	}

	public void initialize(String mapBasePath, Properties properties) {
		try {
			mapWest = Double.parseDouble(properties.getProperty(MAP_WEST_KEY));
			mapNorth = Double
					.parseDouble(properties.getProperty(MAP_NORTH_KEY));
			mapEast = Double.parseDouble(properties.getProperty(MAP_EAST_KEY));
			mapSouth = Double
					.parseDouble(properties.getProperty(MAP_SOUTH_KEY));
			String mapUrl = properties.getProperty(MAP_URL_KEY, null);

			int initialZoomLevel = Integer.parseInt(properties
					.getProperty(INITIAL_ZOOM_LEVEL));
			int maxZoomLevel = Integer.parseInt(properties
					.getProperty(MAX_ZOOM_LEVEL));
			int tileWidth = Integer.parseInt(properties.getProperty(TILE_WIDTH,
					"256"));
			int tileHeight = Integer.parseInt(properties.getProperty(
					TILE_HEIGHT, "256"));
			int xTiles = Integer.parseInt(properties.getProperty(X_TILES, "1"));
			int yTiles = Integer.parseInt(properties.getProperty(Y_TILES, "1"));
			int initialXTile = Integer.parseInt(properties
					.getProperty(INITIAL_X_TILE_INDEX));
			int initialYTile = Integer.parseInt(properties
					.getProperty(INITIAL_Y_TILE_INDEX));
			mapNode.setTileSize(tileWidth, tileHeight);
			mapNode.setZoomLevels(initialZoomLevel, maxZoomLevel);
			mapNode.setTiles(xTiles, yTiles, initialXTile, initialYTile);
			// mapNode.setMapPath(mapBasePath
			// + System.getProperty("file.separator") + mapPath);

			if (!mapUrl.startsWith("http")) {
				mapUrl = mapBasePath + System.getProperty("file.separator")
						+ mapUrl;
			}

			mapNode.setMapUrl(mapUrl);

			// if (mapUrl != null) {
			// mapNode.setMapUrl(mapUrl);
			// } else {
			// mapNode.setMapPath(mapBasePath
			// + System.getProperty("file.separator") + mapPath);
			// }

		} catch (Exception e) {
			System.err.println("Map properties not found!");
			e.printStackTrace();
		}
		System.out.println("Map panel intialized");
	}

	public void setZoomSlider(ZoomSlider zoomSlider) {
		if (cameraController != null) {
			cameraController.setZoomSlider(zoomSlider);
		}
		PCamera camera = getCamera();
		camera.removeAllChildren();
		camera.addChild(zoomSlider);
	}

	public void updateMarkerPositions() {
		Rectangle viewport = new Rectangle((int) mapNode
				.getTotalWidth(getCamera()), (int) mapNode
				.getTotalHeight(getCamera()));
		// viewport.width = (int)(viewport.width/mapNode.getWidth());
		// viewport.height = (int)(viewport.height/mapNode.getHeight());
		for (Marker marker : markers) {

			// Point2D mPoint =
			// CoordinateConverter.geoPointToScreenPoint(mapArea,
			// viewport, marker.getMedia().getLatitude(), marker
			// .getMedia().getLongitude());

			Point2D mPoint = geoPointToScreenPoint(marker.getMedia()
					.getLatitude(), marker.getMedia().getLongitude());

			mapNode.globalToLocal(mPoint);
			marker.setOffset(mPoint.getX(), mPoint.getY());
		}
	}

	public void zoomToLevel(int level) {
		if (cameraController != null) {
			cameraController.zoomCameraToLevel(new Point2D.Double(getBounds()
					.getCenterX(), getBounds().getCenterY()), level);
		}
	}

	public void addMedia(Media media) {
		media.addMediaListener(mediaListener);
		Marker marker = new Marker(media);
		markers.add(marker);
		getLayer().addChild(marker);
		Rectangle viewport = new Rectangle(mapNode.getTotalWidth(getCamera()),
				mapNode.getTotalHeight(getCamera()));

		// Point2D mPoint = CoordinateConverter.geoPointToScreenPoint(mapArea,
		// viewport, marker.getMedia().getLatitude(), marker.getMedia()
		// .getLongitude());

		Point2D mPoint = geoPointToScreenPoint(marker.getMedia().getLatitude(),
				marker.getMedia().getLongitude());

		mapNode.globalToLocal(mPoint);
		marker.setOffset(mPoint.getX(), mPoint.getY());
		if (markerController != null) {
			marker.addInputEventListener(markerController);
		} else {
			marker.setAlwaysShowDirection(true);
		}
		// updateMarkerPositions();
	}

	public void removeAllMedia() {
		Marker marker;
		for (int i = 0; i < markers.size(); i++) {
			marker = markers.get(i);
			marker.getMedia().removeMediaListener(mediaListener);
			getLayer().removeChild(marker);
			marker.removeInputEventListener(markerController);
		}
		markers.clear();
	}

	public void removeMedia(Media media) {
		Marker marker;
		for (int i = 0; i < markers.size(); i++) {
			marker = markers.get(i);
			if (marker.getMedia() == media) {
				getLayer().removeChild(marker);
				marker.removeInputEventListener(markerController);
				markers.remove(marker);
				i--;
			}
		}
		media.removeMediaListener(mediaListener);
	}

	public void resetMap() {
		mapNode.resetTiles();
		removeAllMedia();
	}

	class MapMediaObserver extends BasicMediaListener {

		@Override
		public void mediaSetVisible(Media media, boolean visible) {
			Marker marker = getMarker(media);
			if (marker != null) {
				marker.setVisible(visible);
				marker.setPickable(visible);
			}
		}
	}
}
