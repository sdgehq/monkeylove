package fi.oulu.mediabrowserlite.map;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CoordinateConverter {

	private static CoordinateReferenceSystem GM;

	private static String GM_DEFINITION = "PROJCS[\"Google Mercator\", "
			+ "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", "
			+ "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, "
			+ "AUTHORITY[\"EPSG\",\"7030\"]], "
			+ "AUTHORITY[\"EPSG\",\"6326\"]], " + "PRIMEM[\"Greenwich\", 0.0, "
			+ "AUTHORITY[\"EPSG\",\"8901\"]], "
			+ "UNIT[\"degree\", 0.017453292519943295], "
			+ "AXIS[\"Geodetic latitude\", NORTH], "
			+ "AXIS[\"Geodetic longitude\", EAST], "
			+ "AUTHORITY[\"EPSG\",\"4326\"]], "
			+ "PROJECTION[\"Mercator_1SP\"], "
			+ "PARAMETER[\"semi_minor\", 6378137.0], "
			+ "PARAMETER[\"latitude_of_origin\", 0.0], "
			+ "PARAMETER[\"central_meridian\", 0.0], "
			+ "PARAMETER[\"scale_factor\", 1.0], "
			+ "PARAMETER[\"false_easting\", 0.0], "
			+ "PARAMETER[\"false_northing\", 0.0], UNIT[\"m\", 1.0], "
			+ "AXIS[\"Easting\", EAST], AXIS[\"Northing\", NORTH], "
			+ "AUTHORITY[\"EPSG\",\"900913\"]]";

	static {
		try {
			// DefaultCoordinateOperationFactory trFactory = new
			// DefaultCoordinateOperationFactory();
			GM = CRS.parseWKT(GM_DEFINITION);

		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MismatchedDimensionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private Rectangle2D mapArea;
	// private Rectangle viewport;
	//
	// public CoordinateConverter(Rectangle2D mapArea, Rectangle viewport) {
	// this.mapArea = mapArea;
	// this.viewport = viewport;
	// }
	//
	// public void setMapArea(Rectangle2D mapArea) {
	// this.mapArea = mapArea;
	// }
	//
	// public void setViewport(Rectangle viewport) {
	// this.viewport = viewport;
	// }
	//
	// public Point2D screenPointToGeoPoint(Point2D sPoint) {
	// if (mapArea != null && viewport != null) {
	// return screenPointToGeoPoint(mapArea, viewport, sPoint);
	// } else {
	// throw new IllegalStateException("Not initialized, map area: "
	// + mapArea + ", viewport: " + viewport);
	// }
	// }
	//
	// public Point2D screenPointToGeoPoint(Rectangle viewport, Point2D sPoint)
	// {
	// if (mapArea != null) {
	// return screenPointToGeoPoint(mapArea, viewport, sPoint);
	// } else {
	// throw new IllegalStateException("Not initialized, map area: "
	// + mapArea);
	// }
	// }
	//
	// public Point2D geoPointToScreenPoint(double latitude, double longitude) {
	// if (mapArea != null && viewport != null) {
	// return geoPointToScreenPoint(mapArea, viewport, latitude, longitude);
	// } else {
	// throw new IllegalStateException("Not initialized, map area: "
	// + mapArea + ", viewport: " + viewport);
	// }
	// }
	//
	// public Point2D geoPointToScreenPoint(Rectangle viewport, double latitude,
	// double longitude) {
	// if (mapArea != null) {
	// return geoPointToScreenPoint(mapArea, viewport, latitude, longitude);
	// } else {
	// throw new IllegalStateException("Not initialized, map area: "
	// + mapArea);
	// }
	// }
	//
	// public static Point2D geoPointToScreenPoint(Rectangle2D mapArea,
	// Rectangle viewport, double latitude, double longitude) {
	// return geoPointToScreenPoint(mapArea, viewport, new Point2D.Double(
	// longitude, latitude));
	// }
	//
	// private static Point2D geoPointToScreenPoint(Rectangle2D mapArea,
	// Rectangle viewport, Point2D gPoint) {
	//
	// //
	// System.out.println("*** CoordinateConverter.geoPointToScreenPoint ***");
	// // System.out.println("\tmap area: " + mapArea);
	// // System.out.println("\tviewport: " + viewport);
	// // System.out.println("\tgeo point: " + gPoint);
	//
	// Point2D.Double sPoint = new Point2D.Double();
	//
	// AffineTransform tr = getGeoPointToScreenPointTransform(mapArea,
	// viewport);
	// if (tr != null) {
	// tr.transform(gPoint, sPoint);
	// }
	// // System.out.println("\tscreen point: " + sPoint);
	// // System.out.println("******");
	// return sPoint;
	// }
	//
	// public static Point2D screenPointToGeoPoint(Rectangle2D mapArea,
	// Rectangle viewport, double x, double y) {
	// return screenPointToGeoPoint(mapArea, viewport,
	// new Point2D.Double(x, y));
	// }
	//
	// public static Point2D screenPointToGeoPoint(Rectangle2D mapArea,
	// Rectangle viewport, Point2D sPoint) {
	// //
	// System.out.println("*** CoordinateConverter.screenPointToGeoPoint ***");
	// // System.out.println("\tmap area: " + mapArea);
	// // System.out.println("\tviewport: " + viewport);
	// // System.out.println("\tscreen point: " + sPoint);
	//
	// Point2D gPoint = new Point2D.Double();
	// AffineTransform tr = getScreenPointToGeoPointTransform(mapArea,
	// viewport);
	// if (tr != null) {
	// tr.transform(sPoint, gPoint);
	// }
	// // System.out.println("\tgeo point : " + gPoint);
	// // System.out.println("******");
	// return gPoint;
	// }
	//
	// private static AffineTransform getGeoPointToScreenPointTransform(
	// Rectangle2D mapArea, Rectangle viewport) {
	// ReferencedEnvelope area = new ReferencedEnvelope(mapwest, mapeast,
	// mapnorth, mapsouth, GM);
	// AffineTransform tr = RendererUtilities.worldToScreenTransform(area,
	// viewport);
	//
	// // double scaleX = viewport.getWidth() / mapArea.getWidth();
	// // double scaleY = viewport.getHeight() / mapArea.getHeight();
	// // AffineTransform tr = new AffineTransform();
	// // tr.translate(viewport.getWidth() / 2, -viewport.getHeight() / 2);
	// // tr.scale(scaleX, -scaleY);
	// // tr.translate(-mapArea.getCenterX(), -mapArea.getCenterY());
	// return tr;
	// }
	//
	// private static AffineTransform getScreenPointToGeoPointTransform(
	// Rectangle2D mapArea, Rectangle viewport) {
	// AffineTransform tr = getGeoPointToScreenPointTransform(mapArea,
	// viewport);
	// try {
	// tr = tr.createInverse();
	// return tr;
	// } catch (NoninvertibleTransformException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// private static void testGeoPointToScreenPoint() {
	// System.out.println("\ngeo point to screen point");
	//
	// Rectangle2D mapArea = new Rectangle2D.Double(mapwest, mapnorth, Math
	// .abs(mapeast - mapwest), Math.abs(mapnorth - mapsouth));
	// Rectangle viewport = new Rectangle(512, 512);
	//
	// Point2D gPoint = new Point2D.Double(mapwest, mapnorth);
	// Point2D sPoint = geoPointToScreenPoint(mapArea, viewport, gPoint);
	// System.out.println("geo point: " + gPoint);
	// System.out.println("screen point: " + sPoint);
	// }
	//
	// private static void testScreenPointToGeoPoint() {
	// System.out.println("screen point to geo point");
	//
	// Rectangle2D mapArea = new Rectangle2D.Double(mapwest, mapnorth, Math
	// .abs(mapeast - mapwest), Math.abs(mapnorth - mapsouth));
	// Rectangle viewport = new Rectangle(512, 512);
	//
	// Point2D sPoint = new Point2D.Double(0, 0);
	// Point2D gPoint = screenPointToGeoPoint(mapArea, viewport, sPoint);
	// System.out.println("screen point: " + sPoint);
	// System.out.println("geo point: " + gPoint);
	// }
	//
	// static double mapwest = 24.609375;
	// static double mapnorth = 65.80277639340238;
	// static double mapeast = 25.3125;
	// static double mapsouth = 65.5129625532949;
	//
	// public static void main(String[] args) {
	//
	// Rectangle viewport = new Rectangle(512, 512);
	//
	// Point2D sPoint = new Point2D.Double(0, 0);
	// Point2D gPoint = screenPointToGeoPoint(mapwest, mapnorth, mapeast,
	// mapsouth, viewport, sPoint);
	//
	// sPoint = geoPointToScreenPoint(mapwest, mapnorth, mapeast, mapsouth,
	// viewport, gPoint);
	//
	// System.out.println("screen point: " + sPoint);
	// System.out.println("geo point: " + gPoint);
	//
	// }

	public static Point2D geoPointToScreenPoint(double mapwest,
			double mapnorth, double mapeast, double mapsouth,
			Rectangle viewport, Point2D lonLat) {
		AffineTransform tr = getGeoPointToScreenPointTransform(mapwest,
				mapnorth, mapeast, mapsouth, viewport);
		if (tr != null) {
			return tr.transform(lonLat, null);
		}
		return null;
	}

	public static Point2D screenPointToGeoPoint(double mapwest,
			double mapnorth, double mapeast, double mapsouth,
			Rectangle viewport, Point2D xy) {
		AffineTransform tr = getScreenPointToGeoPointTransform(mapwest,
				mapnorth, mapeast, mapsouth, viewport);
		if (tr != null) {
			return tr.transform(xy, null);
		}
		return null;
	}

	private static AffineTransform getGeoPointToScreenPointTransform(
			double mapwest, double mapnorth, double mapeast, double mapsouth,
			Rectangle viewport) {
		ReferencedEnvelope area = new ReferencedEnvelope(mapwest, mapeast,
				mapnorth, mapsouth, GM);
		AffineTransform tr = RendererUtilities.worldToScreenTransform(area,
				viewport);
		return tr;
	}

	private static AffineTransform getScreenPointToGeoPointTransform(
			double mapwest, double mapnorth, double mapeast, double mapsouth,
			Rectangle viewport) {
		AffineTransform tr = getGeoPointToScreenPointTransform(mapwest,
				mapnorth, mapeast, mapsouth, viewport);
		try {
			tr = tr.createInverse();
			return tr;
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return null;
	}

}
