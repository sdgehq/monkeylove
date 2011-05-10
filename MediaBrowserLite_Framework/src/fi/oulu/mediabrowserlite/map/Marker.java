package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.Media.Type;
import fi.oulu.mediabrowserlite.ui.MediaNode;

public class Marker extends PNode implements PActivity.PActivityDelegate {
	public final static int DEFAULT_SIZE = 11;
	public static final int ZOOMED_SIZE = 23;
	public static final double IMAGE_SCALE = 24 / (double) 40;
	public static final int THUMB_NAIL_WIDTH = 40;
	public static final int THUMB_NAIL_HEIGHT = 30;

	private static BufferedImage[] iconImages = null;
	private static BufferedImage angleMarkerImage = null;

	private Media media;

	private PNode marker;
	// private PNode thumbNail;
	private MediaNode thumbNail;
	private PNode angleMarker = null;
	private boolean zoomed = false;
	private boolean thumbNailShown = false;
	private PActivity zoomActivity = null;
	private PActivity thumbActivity = null;
	private PActivity angleActivity = null;
	private Point2D thumbNailPosition = new Point2D.Double(0, 0);
	private boolean thumbZoomed = false;
	private double thumbWidth = THUMB_NAIL_WIDTH;
	private double thumbHeight = THUMB_NAIL_HEIGHT;
	private PBounds thumbBounds = null;
	private PBounds zoomedBounds = null;
	private Point2D markerPosition;
	private boolean alwaysShowDirection = false;

	public Marker(Media media) {
		if (iconImages == null) {
			iconImages = new BufferedImage[6];
			iconImages[0] = getImage("Positive.png");
			iconImages[1] = getImage("PositiveGrey.png");
			iconImages[2] = getImage("Neutral.png");
			iconImages[3] = getImage("NeutralGrey.png");
			iconImages[4] = getImage("Negative.png");
			iconImages[5] = getImage("NegativeGrey.png");
			angleMarkerImage = getImage("AngleMarker.png");
		}
		this.media = media;

		PNode angleNode = new PNode();
		angleNode.setRotation(0);
		double angle = media.getDirection();
		// System.out.println( "Media direction: " + angle );
		angleNode.rotateAboutPoint(Math.toRadians(angle), 0, 0);

		angleMarker = new PImage(angleMarkerImage);
		angleMarker.setBounds(-1, -1, 2, 2);
		angleMarker.setPickable(false);
		angleNode.addChild(angleMarker);
		addChild(angleNode);

		Point2D position = new Point2D.Double(0, 0);

		// thumbNail = PPath.createRectangle( -1, -1, 2, 2 );
		// thumbNail.setPaint( new Color( (float)Math.random(),
		// (float)Math.random(), (float)Math.random() ) );
		thumbNail = new MediaNode(media);
		thumbNail.setBorder(1, 1);
		thumbNail.setBorderColor(Color.black);
		addChild(thumbNail);
		// marker = PPath.createEllipse( (float)(-DEFAULT_SIZE/2.0),
		// (float)(-DEFAULT_SIZE/2.0), (float)DEFAULT_SIZE, (float)DEFAULT_SIZE
		// );
		marker = new PImage(getIconImage(media.getType(), true));
		marker.setBounds(-(DEFAULT_SIZE / 2.0) / IMAGE_SCALE,
				-(DEFAULT_SIZE / 2.0) / IMAGE_SCALE,
				DEFAULT_SIZE / IMAGE_SCALE, DEFAULT_SIZE / IMAGE_SCALE);
		addChild(marker);
		marker.setOffset(0, 0);
		setOffset(position);
		thumbBounds = new PBounds(-1, -1, 2.0, 1.5);
		thumbNail.storeBounds(thumbBounds);
		markerPosition = position;
		zoomedBounds = new PBounds(-ZOOMED_SIZE / 2.0, -ZOOMED_SIZE / 2.0,
				ZOOMED_SIZE, ZOOMED_SIZE);
	}

	public Image getIconImage(Media.Type type, boolean active) {
		int index = 2;
		if (type == Media.Type.POSITIVE) {
			index = 0;
		}
		if (type == Media.Type.NEGATIVE) {
			index = 4;
		}
		if (!active) {
			index++;
		}
		return iconImages[index];
	}

	public BufferedImage getImage(String fileName) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource(fileName));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return image;
	}

	public MediaNode getMediaNode() {
		return thumbNail;
	}

	public Media getMedia() {
		return media;
	}

	public void updateView(double viewScale) {
		setScale(1.0 / viewScale);
		// marker.setScale( 1.0/viewScale );
		// thumbNail.setScale( 1.0/viewScale );
	}

	public double getThumbWidth() {
		return thumbWidth;
	}

	public double getThumbHeight() {
		return thumbHeight;
	}

	public void setThumbSize(double width, double height) {
		thumbWidth = width;
		thumbHeight = height;
		double ratio = thumbNail.getImageSizeRatio();
		if (ratio >= 1) {
			thumbHeight = thumbWidth / ratio;
		} else {
			thumbWidth = thumbHeight * ratio;
		}
		if (thumbNailShown) {
			showThumbNail(localToGlobal(thumbNailPosition));
		}
	}

	public void setAlwaysShowDirection(boolean alwaysShowDirection) {
		this.alwaysShowDirection = alwaysShowDirection;
		if (alwaysShowDirection) {
			zoomAngleMarker(true);
		}
	}

	public void showThumbNail(Point2D thumbNailPosition) {
		if (thumbActivity != null) {
			thumbActivity.terminate();
		}
		moveToFront();
		Point2D point = globalToLocal((Point2D) thumbNailPosition.clone());
		this.thumbNailPosition = point;
		thumbBounds.setFrame(point.getX() - thumbWidth / 2.0, point.getY()
				- thumbHeight / 2.0, thumbWidth, thumbHeight);
		moveToFront();
		animateThumbNail(point, thumbWidth, thumbHeight, 250l);
		thumbNail.storeBounds(thumbBounds);
		thumbNailShown = true;
	}

	public void showThumbNail(Point2D thumbNailPosition, double thumbNailWidth) {
		Point2D point = globalToLocal((Point2D) thumbNailPosition.clone());
		this.thumbNailPosition = point;
		thumbBounds.setFrame(point.getX() - thumbNailWidth / 2.0, point.getY()
				- thumbNailWidth / 2.0, thumbNailWidth, thumbNailWidth);
		moveToFront();
		animateThumbNail(point, thumbNailWidth, thumbNailWidth, 250l);
		thumbNail.storeBounds(thumbBounds);
		thumbNailShown = true;

	}

	public void setActive(boolean active) {
		((PImage) marker).setImage(getIconImage(media.getType(), active));
		if (zoomed) {
			marker.setBounds(-(ZOOMED_SIZE / 2.0) / IMAGE_SCALE,
					-(ZOOMED_SIZE / 2.0) / IMAGE_SCALE, ZOOMED_SIZE
							/ IMAGE_SCALE, ZOOMED_SIZE / IMAGE_SCALE);
		} else {
			marker.setBounds(-(DEFAULT_SIZE / 2.0) / IMAGE_SCALE,
					-(DEFAULT_SIZE / 2.0) / IMAGE_SCALE, DEFAULT_SIZE
							/ IMAGE_SCALE, DEFAULT_SIZE / IMAGE_SCALE);
		}
	}

	protected void animateThumbNail(Point2D thumbNailPosition, double width,
			double height, long time) {
		if (thumbActivity != null) {
			thumbActivity.terminate();
		}
		double ratio = thumbNail.getImageSizeRatio();
		double zoomedWidth = width;
		double zoomedHeight = height;
		if (ratio >= 1) {
			zoomedHeight = zoomedWidth / ratio;
			if (zoomedHeight > height) {
				zoomedHeight = height;
				zoomedWidth = zoomedHeight * ratio;
			}
		} else {
			zoomedWidth = zoomedHeight * ratio;
		}
		thumbActivity = thumbNail.animateToBounds(thumbNailPosition.getX()
				- zoomedWidth / 2.0, thumbNailPosition.getY() - zoomedHeight
				/ 2.0, zoomedWidth, zoomedHeight, time);
		thumbActivity.setDelegate(this);
	}

	public void zoomAngleMarker(boolean in) {
		if (angleActivity != null) {
			angleActivity.terminate();
		}
		if ((in || alwaysShowDirection) && (media.getDirection() >= 0)) {
			angleActivity = angleMarker.animateToBounds(-100, -100, 200, 200,
					250l);
		} else {
			angleActivity = angleMarker.animateToBounds(-1, -1, 2, 2, 250l);
		}
		angleActivity.setDelegate(this);
	}

	public void zoomThumbTo(Point2D globalPoint, double width, double height) {
		thumbNail.moveToFront();
		thumbZoomed = true;
		thumbNail.setMode(MediaNode.Mode.CONTENT);
		thumbNail.setBorder(0, 0);
		double ratio = thumbNail.getImageSizeRatio();
		double zoomedWidth = width;
		double zoomedHeight = height;
		/*
		 * if (ratio >= 1) { zoomedHeight = zoomedWidth / ratio; if
		 * (zoomedHeight > height) { zoomedHeight = height; zoomedWidth =
		 * zoomedHeight * ratio; } } else { zoomedWidth = zoomedHeight * ratio;
		 * }
		 */
		animateThumbNail(globalToLocal(globalPoint), zoomedWidth, zoomedHeight,
				500l);
	}

	public void zoomThumb(boolean in) {
		zoomAngleMarker(in);
		if (in) {
			if (!thumbZoomed) {
				thumbZoomed = true;
				animateThumbNail(thumbNailPosition,
						thumbBounds.getWidth() * 1.5,
						thumbBounds.getHeight() * 1.5, 250l);
			}
		} else {
			if (thumbZoomed) {
				marker.moveToFront();
				animateThumbNail(thumbNailPosition, thumbBounds.getWidth(),
						thumbBounds.getHeight(), 250l);
				thumbNail.setBorder(1, 1);
				thumbNail.setBorderColor(Color.black);
				thumbZoomed = false;
			}
		}
	}

	public boolean isThumbNail(PNode node) {
		return (thumbNail == node);
	}

	public void hideThumbNail() {
		thumbBounds.setFrame(-1.0, -1.0, 2.0, 1.5);
		thumbNailPosition = new Point2D.Double(0, 0);
		animateThumbNail(thumbNailPosition, 2.0, 2.0, 250l);
		thumbZoomed = false;
		thumbNailShown = false;
	}

	public boolean getThumbNailShown() {
		return thumbNailShown;
	}

	public Rectangle2D getMarkerBounds() {
		if (zoomed) {
			return localToGlobal(new PBounds(-ZOOMED_SIZE / 2.0,
					-ZOOMED_SIZE / 2.0, ZOOMED_SIZE, ZOOMED_SIZE));
		}
		return localToGlobal(marker.getBounds());
	}

	public Rectangle2D getZoomedBounds() {
		PBounds bounds = new PBounds();
		bounds.add(zoomedBounds);
		bounds.add(thumbBounds);
		return localToGlobal(bounds);
	}

	public void zoomIn() {
		zoomed = true;
		if (zoomActivity != null) {
			zoomActivity.terminate();
		}
		// zoomActivity = marker.animateToBounds( -ZOOMED_SIZE/2.0,
		// -ZOOMED_SIZE/2.0, ZOOMED_SIZE, ZOOMED_SIZE, 250l );
		zoomActivity = marker.animateToBounds(-(ZOOMED_SIZE / 2.0)
				/ IMAGE_SCALE, -(ZOOMED_SIZE / 2.0) / IMAGE_SCALE, ZOOMED_SIZE
				/ IMAGE_SCALE, ZOOMED_SIZE / IMAGE_SCALE, 250l);

		zoomActivity.setDelegate(this);
	}

	public void zoomOut() {
		zoomed = false;
		if (zoomActivity != null) {
			zoomActivity.terminate();
		}
		// zoomActivity = marker.animateToBounds( -DEFAULT_SIZE/2.0,
		// -DEFAULT_SIZE/2.0, DEFAULT_SIZE, DEFAULT_SIZE, 250l );
		zoomActivity = marker.animateToBounds(-(DEFAULT_SIZE / 2.0)
				/ IMAGE_SCALE, -(DEFAULT_SIZE / 2.0) / IMAGE_SCALE,
				DEFAULT_SIZE / IMAGE_SCALE, DEFAULT_SIZE / IMAGE_SCALE, 250l);

		zoomActivity.setDelegate(this);
	}

	public boolean isZoomed() {
		return zoomed;
	}

	public void activityFinished(PActivity activity) {
		if (activity == zoomActivity) {
			zoomActivity = null;
		}
		if (activity == thumbActivity) {
			thumbActivity = null;
			if (thumbZoomed) {
				thumbNail.setMode(MediaNode.Mode.CONTENT);
			} else {
				thumbNail.setMode(MediaNode.Mode.THUMBNAIL);
			}
		}
		if (activity == angleActivity) {
			angleActivity = null;
		}
	}

	public void activityStarted(PActivity activity) {
	}

	public void activityStepped(PActivity activity) {
	}

}