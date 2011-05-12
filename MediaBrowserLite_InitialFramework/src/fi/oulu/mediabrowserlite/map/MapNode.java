package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class MapNode extends PNode implements PInputEventListener,
		PActivity.PActivityDelegate {

	public static final String FILE_SUFFIX = ".png";
	public static final String MAP_COPYRIGHT = "Karttatiedot \u00A9 Maanmittauslaitos 2010";

	// private String mapPath = null;
	private Image image;
	private int initialZoomLevel = 10;
	private int maxZoomLevel = 16;
	private int tileWidth = 256;
	private int tileHeight = 256;
	private int xTiles = 2; // Tiles at default zoom level
	private int yTiles = 2;
	private int initialXTile = 0;
	private int initialYTile = 0;

	private HashMap<String, BufferedImage> tiles;
	private TileLoader loader;
	private int zoomLevels = (maxZoomLevel - initialZoomLevel);
	private int totalWidth = 256;
	private int totalHeight = 256;
	private boolean initialized = false;

	private String mapUrl;

	public MapNode() {
		image = new ImageIcon(getClass().getResource("Grid.png")).getImage();
		setBounds(0, 0, xTiles * tileWidth, yTiles * tileWidth);
		tiles = new HashMap<String, BufferedImage>();

		// addInputEventListener( this );

		// addInputEventListener(coordinateConversionHandler);
	}

	private void calculateSize() {
		totalWidth = (int) Math.pow(2, zoomLevels) * xTiles * tileWidth;
		totalHeight = (int) Math.pow(2, zoomLevels) * yTiles * tileWidth;
		setBounds(0, 0, xTiles * tileWidth, yTiles * tileWidth);
	}

	// private void createTileLoader() {
	// if (mapUrl != null) {
	// loader = new UrlTileLoader(mapUrl, this);
	// } else {
	// loader = new TileLoader(mapPath, this);
	// }
	// }

	public void loadTile(String tileName) {
		if (!initialized) {
			return;
		}
		if (loader != null) {
			if (!loader.isAlive()) {
				loader = new TileLoader(mapUrl, this);
				loader.addTile(tileName);
				loader.start();
			} else {
				loader.addTile(tileName);
			}
		}
	}

	public String getTileName(int tileX, int tileY, int zoomLevel) {
		int x = initialXTile * (int) Math.pow(2, zoomLevel) + tileX;
		int y = initialYTile * (int) Math.pow(2, zoomLevel) + tileY;
		int actualZoomLevel = initialZoomLevel + zoomLevel;
		return x + "_" + y + "_" + actualZoomLevel + FILE_SUFFIX;
	}

	public synchronized void addTile(String tileName, BufferedImage tileImage) {
		tiles.put(tileName, tileImage);
		// System.out.println( "Put tile: " + tileName );
	}

	public void processEvent(PInputEvent event, int type) {
		if (type == MouseEvent.MOUSE_RELEASED) {
			if (event.getClickCount() == 2) {
				Point2D point = event.getPositionRelativeTo(this);
				// System.out.println("Clicked at: " + point);
				double viewScale = event.getCamera().getViewScale()
						/ getScale();
				int zoomLevel = getZoomLevel(viewScale);
				PBounds viewBounds = event.getCamera().getViewBounds();
				double differenceToZoom = 1.0;
				// System.out.println( "Apparent tile width: " +
				// tileWidth*viewScale );
				double apparentTileWidth = tileWidth * viewScale;
				double neededTileWidth = tileWidth;
				int targetZoomLevel = zoomLevel;
				if (event.isControlDown()) {
					targetZoomLevel = Math.max(zoomLevel - 1, 0);
				} else {
					targetZoomLevel = Math.min(zoomLevel + 1, zoomLevels);
				}
				differenceToZoom = Math.pow(2.0, targetZoomLevel) / viewScale;

				// System.out.println("View scale: " + viewScale
				// + " needs to zoom by: " + differenceToZoom + " to: "
				// + viewScale * differenceToZoom);
				point = localToGlobal(point);
				// System.out.println("View bounds" + viewBounds);
				PBounds newBounds = new PBounds(point.getX(), point.getY(),
						viewBounds.getWidth() / differenceToZoom, viewBounds
								.getHeight()
								/ differenceToZoom);
				newBounds.moveBy(-newBounds.getWidth() / 2.0, -newBounds
						.getHeight() / 2.0);
				event.getCamera().globalToLocal(newBounds);
				newBounds.expandNearestIntegerDimensions();
				// System.out.println("New bounds" + newBounds);
				event.getCamera().animateViewToCenterBounds(newBounds, true,
						1000l).setDelegate(this);
				setPaintInvalid(true);
			}
		}
	}

	public void resetTiles() {
		tiles.clear();
	}

	public void activityFinished(PActivity activity) {
		setPaintInvalid(true);
		repaint();
	}

	public void activityStarted(PActivity activity) {
	}

	public void activityStepped(PActivity activity) {
	}

	private Point getTile(Point2D coord, double viewScale) {
		Point2D localPoint = globalToLocal(coord);
		int x = 0;
		int y = 0;
		int zoomLevel = 0;
		int xTilesOnLevel = xTiles;
		int yTilesOnLevel = yTiles;
		if (viewScale > 1) {
			zoomLevel = getZoomLevel(viewScale);
			xTilesOnLevel = xTiles * (int) Math.pow(2, zoomLevel);
			yTilesOnLevel = yTiles * (int) Math.pow(2, zoomLevel);
		}
		int absoluteX = (int) (localPoint.getX() / (double) getWidth() * totalWidth);
		int absoluteY = (int) (localPoint.getY() / (double) getHeight() * totalHeight);
		x = (int) (absoluteX / (totalWidth / (double) xTilesOnLevel));
		y = (int) (absoluteY / (totalHeight / (double) yTilesOnLevel));
		// System.out.println( "Local point: " + localPoint.getX() +
		// " Absolute: " + absoluteX + " Zoom Level: " + zoomLevel + " x: " + x
		// + " of " + xTilesOnLevel);
		return new Point(x, y);
	}

	public Image getTileImage(int x, int y, int zoomLevel) {
		Image tile = tiles.get(getTileName(x, y, zoomLevel));
		if (tile == null) {
			// If the tile is not found and we need it, load it
			if (zoomLevel <= zoomLevels) {
				loadTile(getTileName(x, y, zoomLevel));
			}
			// In the meanwhile, check if we can use a slice from the upper
			// level
			if (zoomLevel > 0) {
				tile = tiles.get(getTileName(x / 2, y / 2, zoomLevel - 1));
				if (tile != null) {
					int xIndex = x % 2;
					int yIndex = y % 2;
					tile = ((BufferedImage) tile).getSubimage(xIndex
							* tileWidth / 2, yIndex * tileWidth / 2,
							tileWidth / 2, tileWidth / 2);
					invalidatePaint();
				}
			}
		}
		if (tile == null) {
			tile = image;
			invalidatePaint();
		}
		return tile;
	}

	public int getTotalWidth(PCamera camera) {
		// int zoomLevel = getZoomLevel(camera.getViewScale()) + 1;
		int width = (int) (xTiles * tileWidth * camera.getViewScale());

		// return totalWidth;
		return width;
	}

	public int getTotalHeight(PCamera camera) {
		// int zoomLevel = getZoomLevel(camera.getViewScale()) + 1;
		int height = (int) (yTiles * tileHeight * camera.getViewScale());

		return totalHeight;
	}

	public int getZoomLevel(double viewScale) {
		int level = 0;
		double scale = 1.0;
		while (scale < viewScale) {
			level++;
			scale *= 2.0;
		}
		level = Math.min(level, zoomLevels);
		return level;
	}

	public Rectangle getViewport(PCamera camera) {
		int zoomLevel = getZoomLevel(camera.getViewScale()) + 1;
		int width = (int) (xTiles * tileWidth);
		int height = (int) (yTiles * tileHeight);
		return new Rectangle(width, height);
	}

	public int getZoomLevels() {
		return zoomLevels;
	}

	// PBasicInputEventHandler coordinateConversionHandler = new
	// PBasicInputEventHandler() {
	//
	// double mapwest = 24.609375;
	// double mapnorth = 65.80277639340238;
	// double mapeast = 25.3125;
	// double mapsouth = 65.5129625532949;
	// private Rectangle2D mapArea = new Rectangle2D.Double(mapwest, mapnorth,
	// Math.abs(mapeast - mapwest), Math.abs(mapnorth - mapsouth));
	//
	// @Override
	// public void mouseClicked(PInputEvent event) {
	//
	// int zoomLevel = getZoomLevel(event.getCamera().getViewScale()) + 1;
	// int width = xTiles * tileWidth * zoomLevel;
	// int height = yTiles * tileHeight * zoomLevel;
	//
	// Rectangle viewport = new Rectangle(width, height);
	//
	// Point2D tl = new Point2D.Double(0, 0);
	// Point2D tr = new Point2D.Double(width, 0);
	// Point2D bl = new Point2D.Double(0, height);
	// Point2D br = new Point2D.Double(width, height);
	//
	// System.out.println("Map area: ");
	// System.out.println("\ttop left: "
	// + tl
	// + " --> "
	// + CoordinateConverter.screenPointToGeoPoint(mapArea,
	// viewport, tl));
	//
	// System.out.println("\ttop right: "
	// + tr
	// + " --> "
	// + CoordinateConverter.screenPointToGeoPoint(mapArea,
	// viewport, tr));
	//
	// System.out.println("\tbottom left: "
	// + bl
	// + " --> "
	// + CoordinateConverter.screenPointToGeoPoint(mapArea,
	// viewport, bl));
	//
	// System.out.println("bottom right: "
	// + br
	// + " --> "
	// + CoordinateConverter.screenPointToGeoPoint(mapArea,
	// viewport, br));
	//
	// Point2D localPoint = globalToLocal(event.getPosition());
	// Point2D geoPoint = CoordinateConverter.screenPointToGeoPoint(
	// mapArea, viewport, localPoint);
	//
	// System.out.println("Click point:");
	// System.out.println("\tGlobal point: " + event.getPosition());
	// System.out.println("\tLocal point: " + localPoint);
	// System.out.println("\tGeo point: " + geoPoint);
	// }
	// };

	public Point2D globalToGeoCoordinates(Point2D globalPoint) {
		Point2D geoPoint = null;

		Point2D localPoint = globalToLocal(globalPoint);
		int absoluteX = (int) (localPoint.getX() / (double) getWidth() * totalWidth);
		int absoluteY = (int) (localPoint.getY() / (double) getHeight() * totalHeight);

		// System.out.println("Local point: " + localPoint);
		// System.out.println("Global point: " + globalPoint + " x: " +
		// absoluteX
		// + ", y: " + absoluteY);
		// TODO Actual transformation to geographic coordinates

		return geoPoint;
	}

	// public void setMapPath(String path) {
	// this.mapPath = path;
	// loader = new TileLoader(mapPath, this);
	// for (int y = 0; y < yTiles; y++) {
	// for (int x = 0; x < xTiles; x++) {
	// loader.addTile(getTileName(x, y, 0));
	// }
	// }
	// loader.start();
	// invalidatePaint();
	// }

	public void setMapUrl(String url) {
		this.mapUrl = url;
		loader = new TileLoader(url, this);
		for (int y = 0; y < yTiles; y++) {
			for (int x = 0; x < xTiles; x++) {
				loader.addTile(getTileName(x, y, 0));
			}
		}
		loader.start();
		invalidatePaint();
	}

	public void setTiles(int xTiles, int yTiles, int initialXTile,
			int initialYTile) {
		this.xTiles = xTiles;
		this.yTiles = yTiles;
		this.initialXTile = initialXTile;
		this.initialYTile = initialYTile;
		calculateSize();
		initialized = true;
	}

	public void setTileSize(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		calculateSize();
	}

	public void setZoomLevels(int initialZoomLevel, int maxZoomLevel) {
		this.initialZoomLevel = initialZoomLevel;
		this.maxZoomLevel = Math.max(initialZoomLevel, maxZoomLevel);
		zoomLevels = (this.maxZoomLevel - this.initialZoomLevel);
	}

	protected void paint(PPaintContext context) {
		Graphics2D g2 = context.getGraphics();
		g2.setColor(Color.yellow);
		PBounds bounds = getBounds();
		if (!initialized) {
			g2.fill(bounds);
			return;
		}
		PBounds view = context.getCamera().getViewBounds();
		double viewScale = context.getCamera().getViewScale() / getScale();
		Rectangle2D intersection = bounds.createIntersection(view);
		double width = intersection.getWidth();
		int scaledWidth = (int) (width * viewScale);
		Rectangle2D screenIntersection = (Rectangle2D) intersection.clone();
		int cameraWidth = (int) (context.getCamera().viewToLocal(
				screenIntersection).getWidth());

		int zoomLevel = getZoomLevel(viewScale);

		if (viewScale <= 1.0) {
			Point startTile = getTile(localToGlobal(new Point2D.Double(
					intersection.getX(), intersection.getY())), viewScale);
			Point endTile = getTile(localToGlobal(new Point2D.Double(
					intersection.getMaxX(), intersection.getMaxY())), viewScale);
			int endX = Math.min(endTile.x + 1, xTiles);
			int endY = Math.min(endTile.y + 1, yTiles);
			for (int y = startTile.y; y < endY; y++) {
				for (int x = startTile.x; x < endX; x++) {
					Image tile = getTileImage(x, y, 0); // Use the minimum zoom
					// level
					g2.drawImage(tile, x * tileWidth, y * tileWidth, tileWidth,
							tileWidth, Color.white, null);
				}
			}
			/*
			 * g2.setColor( Color.black ); g2.scale( 1.0/viewScale,
			 * 1.0/viewScale ); String s = "Level: " + zoomLevel; Point2D
			 * screenPoint = new Point2D.Double(
			 * intersection.getX()+10/viewScale,
			 * intersection.getY()+15/viewScale ); g2.drawString( s,
			 * (float)(screenPoint.getX()*viewScale),
			 * (float)(screenPoint.getY()*viewScale) );
			 */

		} else {
			if (viewScale > 2.0) {
				g2.setColor(Color.red);
			}
			// g2.fill(intersection);

			double apparentWidth = getWidth() * viewScale;
			double apparentHeight = getHeight() * viewScale;
			double apparentScale = Math.floor(viewScale);
			// System.out.println( "Zoom level: " + zoomLevel + " at scale: " +
			// viewScale + " apparent: " + apparentScale );
			g2.scale(1.0 / viewScale, 1.0 / viewScale);
			g2.setColor(Color.black);

			Point startTile = getTile(localToGlobal(new Point2D.Double(
					intersection.getX(), intersection.getY())), viewScale);
			Point endTile = getTile(localToGlobal(new Point2D.Double(
					intersection.getMaxX(), intersection.getMaxY())), viewScale);
			int xTilesOnLevel = xTiles * (int) Math.pow(2, zoomLevel);
			int yTilesOnLevel = yTiles * (int) Math.pow(2, zoomLevel);

			int startX = Math.max(startTile.x - 1, 0);
			int startY = Math.max(startTile.y - 1, 0);
			int endX = Math.min(endTile.x + 1, xTilesOnLevel);
			int endY = Math.min(endTile.y + 1, yTilesOnLevel);

			double apparentTileWidth = (apparentWidth / xTilesOnLevel);
			double apparentTileHeight = (apparentHeight / yTilesOnLevel);

			for (int y = startY; y < endY; y++) {
				for (int x = startX; x < endX; x++) {
					Image tile = getTileImage(x, y, zoomLevel);
					g2
							.drawImage(tile, (int) Math.floor(x
									* apparentTileWidth), (int) Math.floor(y
									* apparentTileWidth), (int) Math
									.ceil(apparentTileWidth), (int) Math
									.ceil(apparentTileWidth), Color.white, null);
				}
			}

			String s = "Level: " + zoomLevel + " Start: " + startX + " End: "
					+ endX;
			Point2D screenPoint = new Point2D.Double(intersection.getX() + 10
					/ viewScale, intersection.getY() + 15 / viewScale);
			// g2.drawString( s, (float)(screenPoint.getX()*viewScale),
			// (float)(screenPoint.getY()*viewScale) );
			g2.scale(viewScale, viewScale); // Need to reset the scaling here
		}
		// Add copyright text
		g2.scale(1 / viewScale, 1 / viewScale);
		g2.setColor(Color.black);
		Point2D screenPoint = new Point2D.Double(view.getX() + 5 / viewScale,
				view.getMaxY() - 7 / viewScale);
		g2.drawString(MAP_COPYRIGHT, (float) (screenPoint.getX() * viewScale),
				(float) (screenPoint.getY() * viewScale));
		g2.scale(viewScale, viewScale);

	}
}

class TileLoader extends Thread {
	private MapNode host;
	private String mapBaseUrl;
	private java.util.List<String> tileNames;
	private TileRetriever tileRetriever;

	public TileLoader(String mapBaseUrl, MapNode host) {
		this.mapBaseUrl = mapBaseUrl;
		this.host = host;
		this.tileNames = Collections.synchronizedList(new LinkedList<String>());
		this.tileRetriever = createTileRetriever(mapBaseUrl);
	}

	private TileRetriever createTileRetriever(String baseUrl) {
		System.out.println("Tileloader.createTileRetriever, baseUrl: "
				+ baseUrl);

		if (baseUrl.toLowerCase().startsWith("http")) {
			return new UrlTileRetriever(baseUrl);
		} else {
			return new LocalTileRetriever(baseUrl);
		}
	}

	public void addTile(String tileName) {
		if (!tileNames.contains(tileName)) {
			tileNames.add(tileName);
		}
	}

	public void run() {
		String tileName;
		while (!tileNames.isEmpty()) {
			tileName = (String) tileNames.remove(0);
			try {
				BufferedImage image = tileRetriever.getTile(tileName);
				host.addTile(tileName, image);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		System.out.println("Thread death");
	}

	abstract class TileRetriever {
		private String baseUrl;

		public TileRetriever(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String url) {
			this.baseUrl = url;
		}

		public abstract BufferedImage getTile(String tileName)
				throws IOException;
	}

	class LocalTileRetriever extends TileRetriever {
		public LocalTileRetriever(String baseUrl) {
			super(baseUrl);
		}

		public BufferedImage getTile(String tileName) throws IOException {
			File file = new File(getBaseUrl(), tileName);
			BufferedImage image = ImageIO.read(file);
			return image;
		}
	}

	class UrlTileRetriever extends TileRetriever {
		public UrlTileRetriever(String baseUrl) {
			super(baseUrl);
		}

		public BufferedImage getTile(String tileName) throws IOException {
			BufferedImage image = ImageIO
					.read(new URL(getBaseUrl() + tileName));
			return image;
		}
	}
}