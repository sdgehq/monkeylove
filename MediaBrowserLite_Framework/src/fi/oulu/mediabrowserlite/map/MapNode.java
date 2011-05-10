package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * This is a dummy version of mapnode that does nothing useful
 * @author virtu
 *
 */
public class MapNode extends PPath {

	public MapNode() {
		setPathToRectangle( 0, 0, 300, 300 );
		setPaint( new Color( (float)Math.random(), (float)Math.random(), (float)Math.random() ) );
	}

	public synchronized void addTile(String tileName, BufferedImage tileImage) {
	}

	public void resetTiles() {
	}

	public int getTotalWidth(PCamera camera) {
		return 300;
	}

	public int getTotalHeight(PCamera camera) {
		return 300;
	}

	public int getZoomLevel(double viewScale) {
		return 1;
	}

	public Rectangle getViewport(PCamera camera) {
		return new Rectangle(300, 300);
	}

	public int getZoomLevels() {
		return 1;
	}


	public Point2D globalToGeoCoordinates(Point2D globalPoint) {
		return globalPoint;
	}
	
	public void setMapUrl(String url) {
	
	}

	public void setTiles(int xTiles, int yTiles, int initialXTile,
			int initialYTile) {
	
	}

	public void setTileSize(int tileWidth, int tileHeight) {

	}

	public void setZoomLevels(int initialZoomLevel, int maxZoomLevel) {
	
	}


}