package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import fi.oulu.mediabrowserlite.ui.event.MediaInputEventListener;

public class MarkerController implements PropertyChangeListener,
		PInputEventListener, PActivity.PActivityDelegate {
	public enum States {
		NORMAL, SELECTED, ZOOMED
	};

	private boolean unlockSelectionOnReturn = true;

	private States state = States.NORMAL;
	private MapNode mapNode;
	private PCamera camera;
	private ArrayList<Marker> markers;
	private ArrayList<Marker> activeMarkers;
	private PPath markerBoundsRect = null;
	private PPath thumbNailCircle = null;
	private double previousViewScale = 1;
	private boolean selectionLocked = false;
	private Marker fullScreenMarker = null;
	private PActivity sliderFadeActivity = null;
	private PActivity faderFadeActivity = null;
	private PNode fader;
	private ArrayList<MediaInputEventListener> listeners;

	public MarkerController(MapNode mapNode, PCamera camera,
			ArrayList<Marker> markers) {
		this.mapNode = mapNode;
		this.markers = markers;
		this.camera = camera;
		listeners = new ArrayList<MediaInputEventListener>();

		camera.addPropertyChangeListener(this);
		mapNode.addInputEventListener(this);
		for (Marker marker : markers) {
			marker.addInputEventListener(this);
		}
		activeMarkers = new ArrayList<Marker>();
		previousViewScale = camera.getViewScale();

		fader = PPath.createRectangle(0, 0, 10, 10);
		fader.setPaint(Color.black);
		fader.setPickable(false);
		fader.setVisible(false);
		mapNode.getParent().addChild(fader);
	}

	public void activityFinished(PActivity activity) {
		if (activity == sliderFadeActivity) {
			sliderFadeActivity = null;
		}
		if (activity == faderFadeActivity) {
			faderFadeActivity = null;
			if (state != States.ZOOMED) {
				// fader.setVisible( false );
			}
		}
	}

	public void activityStarted(PActivity activity) {
	}

	public void activityStepped(PActivity activity) {
	}

	public void addMediaInputEventListener(MediaInputEventListener listener) {
		listeners.add(listener);
	}

	public void removeMediaInputEventListener(MediaInputEventListener listener) {
		listeners.remove(listener);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == camera) {
			double viewScale = camera.getViewScale();
			Marker marker;
			for (int i=0; i < markers.size(); i++ ) 
			{
				marker = markers.get(i);
				marker.updateView(viewScale);
			}
			if (state == States.ZOOMED) {
				for (MediaInputEventListener listener : listeners) {
					listener
							.mediaDeactivated(this, fullScreenMarker.getMedia());
				}
				// returnFromFullScreen();
			}
			if (viewScale != previousViewScale && !activeMarkers.isEmpty()) {
				clearActiveMarkers();
			}
			if (!activeMarkers.isEmpty()) {
				Rectangle2D markerBounds = activeMarkers.get(0)
						.getMarkerBounds();
				for (int i = 1; i < activeMarkers.size(); i++) {
					markerBounds = markerBounds.createUnion(activeMarkers
							.get(i).getMarkerBounds());
				}
				if (!fitsScreen(camera.viewToLocal(markerBounds))) {
					clearActiveMarkers();
				}
			}
			previousViewScale = viewScale;
		}
	}

	public boolean isMarker(PNode node) {
		PNode theNode = node;
		while (theNode.getParent() != null) {
			if (theNode instanceof Marker) {
				return true;
			}
			theNode = theNode.getParent();
		}
		return false;
	}

	public Marker getMarker(PNode node) {
		PNode theNode = node;
		while (theNode.getParent() != null) {
			if (theNode instanceof Marker) {
				return (Marker) theNode;
			}
			theNode = theNode.getParent();
		}
		return null;
	}

	public void selectMarkers(Point2D point, double viewScale) {
		long startTime = System.currentTimeMillis();
		for (Marker marker : markers) {
			if (!marker.isZoomed()
					& (marker.getOffset().distance(point) <= (Marker.DEFAULT_SIZE / viewScale) & marker
							.getVisible())) {
				activeMarkers.add(marker);
				marker.zoomIn();
			}
		}
		for (int j = 0; j < markers.size(); j++) {
			Marker marker;
			marker = markers.get(j);
			boolean added = marker.isZoomed();
			for (int i = 0; i < activeMarkers.size() && !added; i++) {
				Marker selectedMarker = activeMarkers.get(i);
				if ((!marker.isZoomed())
						&& (marker.getOffset().distance(
								selectedMarker.getOffset()) <= ((Marker.ZOOMED_SIZE) / viewScale))
						& marker.getVisible()) {
					activeMarkers.add(marker);
					marker.zoomIn();
					added = true;
					j = -1; // Start the checking all over again...
				}
			}
		}
		long endTime = System.currentTimeMillis();
		// System.out.println( "Added " + activeMarkers.size() + " markers in "
		// + (endTime-startTime) + " ms" );
		if (activeMarkers.size() > 0) {
			state = States.SELECTED;
		}
	}

	public boolean isActiveMarker(Point2D point, double viewScale) {
		for (Marker marker : activeMarkers) {
			/*
			 * if( marker.getOffset().distance( point ) <=
			 * (Marker.DEFAULT_SIZE/viewScale) ) { return true; }
			 */
			if (marker.getZoomedBounds().contains(point)) {
				return true;
			}
		}
		return false;
	}

	public void showThumb(Marker marker) {
		Point2D point = marker.getOffset();
		Point2D thumbPoint = new Point2D.Double(point.getX(), point.getY()
				- 50.0 / camera.getViewScale());
		// marker.moveToFront();
		marker.showThumbNail(thumbPoint);
	}

	public boolean fitsScreen(Rectangle2D rectangle) {
		Rectangle2D controlsBounds = new Rectangle(0, 0, 0, 0);
		if (camera.getChildrenCount() >= 1) {
			controlsBounds = camera.getChild(0).getBounds();
		}
		controlsBounds = camera.localToGlobal(controlsBounds);
		return (camera.getBounds().contains(rectangle) && !controlsBounds
				.intersects(rectangle));
	}

	public boolean canFit(Rectangle2D rectangle, Rectangle2D otherRectangle) {
		return ((rectangle.getWidth() >= otherRectangle.getWidth()) && (rectangle
				.getHeight() >= otherRectangle.getHeight()));
	}

	public void showThumbs(Point2D center) {
		
		//if( markerBoundsRect != null ) 
		//{ 
		//	  camera.removeChild( markerBoundsRect );
		//	  markerBoundsRect = null; 
		//} 
		//if( thumbNailCircle != null ) 
		//{
		//	  camera.removeChild( thumbNailCircle ); 
		//	  thumbNailCircle = null; 
		//}
		 
		boolean useCircle = false;
		if (activeMarkers.size() > 0) {
			// Calculate bounds for all selected markers
			Rectangle2D markerBounds = activeMarkers.get(0).getMarkerBounds();
			for (int i = 1; i < activeMarkers.size(); i++) {
				markerBounds = markerBounds.createUnion(activeMarkers.get(i)
						.getMarkerBounds());
			}
			double markerWidth = Math.max(activeMarkers.get(0).getThumbWidth(),
					activeMarkers.get(0).getThumbHeight());
			markerWidth = markerWidth / camera.getViewScale();

			if (activeMarkers.size() < 16) {
				// Calculate the radius for enclosing circle
				// System.out.println( "Marker bounds: " + markerBounds );
				double d1 = center.distance(new Point2D.Double(markerBounds
						.getMinX(), markerBounds.getMinY()));
				double d2 = center.distance(new Point2D.Double(markerBounds
						.getMinX(), markerBounds.getMaxY()));
				double d3 = center.distance(new Point2D.Double(markerBounds
						.getMaxX(), markerBounds.getMinY()));
				double d4 = center.distance(new Point2D.Double(markerBounds
						.getMaxX(), markerBounds.getMaxY()));
				double circleRadius = Math.max(Math.max(d1, d2), Math.max(d3,
						d4));
				double markerDiagonal = Math
						.sqrt(2 * markerWidth * markerWidth);
				circleRadius += (0.75 * markerDiagonal);
				circleRadius = Math.max(circleRadius, (0.5
						* activeMarkers.size() * (markerDiagonal) / Math.PI));

				// Check whether it fits the screen
				// Rectangle2D circleRect = new Rectangle2D.Double(
				// center.getX()-circleRadius, center.getY()-circleRadius,
				// 2*circleRadius,2*circleRadius );
				Rectangle2D circleRect = new Rectangle2D.Double(center.getX()
						- circleRadius - markerWidth / 2, center.getY()
						- circleRadius - markerWidth / 2, 2 * circleRadius
						+ markerWidth, 2 * circleRadius + markerWidth);
				circleRect = camera.viewToLocal(circleRect);
				if (fitsScreen(circleRect)) {
					double angle = 0;
					for (int i = 0; i < activeMarkers.size(); i++) {
						angle = i * 360.0 / activeMarkers.size();
						// System.out.println( "Angle for " + i + ": " + angle
						// );
						angle = Math.toRadians(angle);
						Point2D position = new Point2D.Double(center.getX()
								+ Math.sin(angle) * circleRadius, center.getY()
								- Math.cos(angle) * circleRadius);
						activeMarkers.get(i).showThumbNail(position);
						activeMarkers.get(i).setActive(false);
						useCircle = true;
					}
				}
				/*
				  Ellipse2D ellipse = new Ellipse2D.Double(); ellipse.setFrame(
				  circleRect ); thumbNailCircle = new PPath( ellipse );
				  thumbNailCircle.setStrokePaint( Color.green );
				  camera.addChild( thumbNailCircle );
				 */
			}
			markerBounds = camera.viewToLocal(markerBounds);

			
			  //markerBoundsRect = new PPath( markerBounds );
			  //markerBoundsRect.setStrokePaint( Color.cyan ); 
			  //camera.addChild( markerBoundsRect );
			 

			if (!useCircle) {
				// Lay out in grid instead
				int columns = (int) Math.ceil(Math.sqrt(activeMarkers.size()));
				Rectangle2D preferredGridOnScreen = new Rectangle2D.Double(0,
						0, columns * markerWidth * camera.getViewScale(),
						columns * markerWidth * camera.getViewScale());
				// System.out.println( activeMarkers.size() +
				// " selected, grid size: " + columns + " of " +
				// markerWidth*camera.getViewScale() + " = " +
				// columns*markerWidth*camera.getViewScale() );
				// Get the available boxes
				// Rectangle2D preferredGridOnScreen = camera.globalToLocal(
				// preferredGrid );
				Rectangle2D controlsBounds = camera.getChild(0).getBounds();
				double minX = controlsBounds.getX() + controlsBounds.getWidth();
				Rectangle2D viewBounds = camera.getBounds();
				Rectangle2D topBox = new Rectangle2D.Double(0, 0, 0, 0);
				Rectangle2D rightBox = new Rectangle2D.Double(0, 0, 0, 0);
				Rectangle2D bottomBox = new Rectangle2D.Double(0, 0, 0, 0);
				Rectangle2D leftBox = new Rectangle2D.Double(0, 0, 0, 0);
				Rectangle2D markerScreenBounds = markerBounds;
								
				if (markerScreenBounds.getY() >= viewBounds.getY()) {
					topBox.setFrame(minX, viewBounds.getY(), viewBounds
							.getWidth()
							- minX, markerScreenBounds.getY()
							- viewBounds.getY());
					
					 //PPath topBoxRect = new PPath( topBox );
					 //topBoxRect.setStrokePaint( Color.red );
					 //markerBoundsRect.addChild( topBoxRect );
					 
				}
				if (markerScreenBounds.getMaxX() < viewBounds.getMaxX()) {
					rightBox.setFrame(markerScreenBounds.getMaxX(), viewBounds
							.getY(), viewBounds.getWidth()
							- markerScreenBounds.getMaxX(), viewBounds
							.getHeight());
					
					 //PPath rightBoxRect = new PPath( rightBox );
					 //rightBoxRect.setStrokePaint( Color.orange );
					 //markerBoundsRect.addChild( rightBoxRect );
					 
				}
				if (markerScreenBounds.getMaxY() < viewBounds.getMaxY()) {
					bottomBox.setFrame(minX, markerScreenBounds.getMaxY(),
							viewBounds.getWidth() - minX, viewBounds
									.getHeight()
									- markerScreenBounds.getMaxY());
					
					 //PPath bottomBoxRect = new PPath( bottomBox );
					 //bottomBoxRect.setStrokePaint( Color.red );
					 //markerBoundsRect.addChild( bottomBoxRect );
					 
				}
				if (markerScreenBounds.getX() >= minX) {
					leftBox.setFrame(minX, viewBounds.getY(),
							markerScreenBounds.getX() - minX, viewBounds
									.getHeight());
					
					 //PPath leftBoxRect = new PPath( leftBox );
					 //leftBoxRect.setStrokePaint( Color.orange );
					 //markerBoundsRect.addChild( leftBoxRect );
					 
				}
				double x, y, gridWidth, gridHeight;
				x = 0;
				y = 0;
				gridWidth = preferredGridOnScreen.getWidth();
				gridHeight = preferredGridOnScreen.getHeight();
				if (canFit(topBox, preferredGridOnScreen)) {
					x = markerScreenBounds.getCenterX()
							- preferredGridOnScreen.getWidth() / 2.0;
					y = markerScreenBounds.getMinY()
							- preferredGridOnScreen.getHeight() - markerWidth*camera.getViewScale()
							/ 2;
					if (x < minX) {
						x = minX;
					}
					if (x + preferredGridOnScreen.getWidth() > topBox.getMaxX()) {
						x = topBox.getMaxX() - preferredGridOnScreen.getWidth();
					}
				} else if (canFit(bottomBox, preferredGridOnScreen)) {
					x = markerScreenBounds.getCenterX()
							- preferredGridOnScreen.getWidth() / 2.0;
					y = markerScreenBounds.getMaxY() + markerWidth*camera.getViewScale() / 2;
					if (x < minX) {
						x = minX;
					}
					if (x + preferredGridOnScreen.getWidth() > bottomBox.getMaxX()) {
						x = bottomBox.getMaxX() - preferredGridOnScreen.getWidth();
					}
				} else if (canFit(rightBox, preferredGridOnScreen)) {
					x = markerScreenBounds.getMaxX();
					y = markerScreenBounds.getCenterY()
							- preferredGridOnScreen.getHeight() / 2.0;
					if (y < rightBox.getY()) {
						y = rightBox.getY();
					}
					if (y + preferredGridOnScreen.getHeight() > rightBox
							.getMaxY()) {
						y = rightBox.getMaxY()
								- preferredGridOnScreen.getHeight();
					}
				} else if (canFit(leftBox, preferredGridOnScreen)) {
					x = markerScreenBounds.getX()
							- preferredGridOnScreen.getWidth();
					y = markerScreenBounds.getCenterY()
							- preferredGridOnScreen.getHeight() / 2.0;
					if (y < leftBox.getY()) {
						y = leftBox.getY();
					}
					if (y + preferredGridOnScreen.getHeight() > leftBox
							.getMaxY()) {
						y = leftBox.getMaxY()
								- preferredGridOnScreen.getHeight();
					}
				} else {
					// None of the existing boxes can fit the full grid, pick
					// the largest and use it
					x = topBox.getX();
					y = topBox.getY();
					gridWidth = topBox.getWidth();
					gridHeight = topBox.getHeight();
					if (rightBox.getWidth() * rightBox.getHeight() > gridWidth
							* gridHeight) {
						x = rightBox.getX();
						y = rightBox.getY();
						gridWidth = rightBox.getWidth();
						gridHeight = rightBox.getHeight();
					}
					if (bottomBox.getWidth() * bottomBox.getHeight() > gridWidth
							* gridHeight) {
						x = bottomBox.getX();
						y = bottomBox.getY();
						gridWidth = bottomBox.getWidth();
						gridHeight = bottomBox.getHeight();
					}
					if (leftBox.getWidth() * leftBox.getHeight() > gridWidth
							* gridHeight) {
						x = leftBox.getX();
						y = leftBox.getY();
						gridWidth = leftBox.getWidth();
						gridHeight = leftBox.getHeight();
					}
				}
				preferredGridOnScreen.setRect(x, y, gridWidth, gridHeight);
				layoutToGrid(camera.localToView(preferredGridOnScreen));
				
				  //PPath gridRect = new PPath( camera.viewToLocal(
				  //preferredGridOnScreen ) ); gridRect.setStrokePaint(
				  //Color.green ); markerBoundsRect.addChild( gridRect );
				 
			}

		}
	}

	public void layoutToGrid(Rectangle2D layoutRect) {
		double markerWidth = Math.max(activeMarkers.get(0).getThumbWidth(),
				activeMarkers.get(0).getThumbHeight());
		markerWidth = markerWidth / camera.getViewScale();

		// System.out.println( "Laying out to: " + layoutRect );
		double usedMarkerWidth = markerWidth;
		boolean sizeFound = false;
		int cols = 0;
		int rows = 0;
		for (int i = 0; i < activeMarkers.size() && !sizeFound; i++) {
			usedMarkerWidth = Math.min(markerWidth, layoutRect.getWidth()
					/ (i + 1));
			cols = (int) (layoutRect.getWidth() / usedMarkerWidth);
			double width = cols * usedMarkerWidth;
			rows = (int) (layoutRect.getHeight() / usedMarkerWidth);
			double height = rows * usedMarkerWidth;
			// System.out.println( "At " + usedMarkerWidth + " " + cols + " by "
			// + rows + ": " + width + " by " + height );
			if ((rows * cols) >= activeMarkers.size()
					&& (width <= layoutRect.getWidth())
					&& (height <= layoutRect.getHeight())) {
				sizeFound = true;
				// System.out.println( "Will need " + cols + " by " + rows +
				// " at: " + usedMarkerWidth*camera.getViewScale() );
			}
		}
		markerWidth = usedMarkerWidth * camera.getViewScale();
		if (sizeFound) {
			double x = 0;
			double y = 0;
			for (int i = 0; i < activeMarkers.size(); i++) {
				x = (i % cols) * usedMarkerWidth + usedMarkerWidth / 2.0;
				y = (i / cols) * usedMarkerWidth + usedMarkerWidth / 2.0;
				Point2D point = new Point2D.Double(x + layoutRect.getX(), y
						+ layoutRect.getY());
				/*
				 * if( i==0 ) { System.out.println( "Point: " + point ); }
				 */
				activeMarkers.get(i).showThumbNail(point, markerWidth);
				activeMarkers.get(i).setActive(false);
			}
		}
	}

	public void clearActiveMarkers() {
		for (Marker marker : activeMarkers) {
			marker.zoomOut();
			marker.hideThumbNail();
		}
		activeMarkers.clear();
		for (Marker marker : markers) {
			marker.setActive(true);
			marker.setPickable(true);
		}
		selectionLocked = false;
		state = States.NORMAL;
	}

	public void lockSelection() {
		selectionLocked = true;
		for (Marker marker : markers) {
			if (!activeMarkers.contains(marker)) {
				marker.setActive(false);
				marker.setPickable(false);
			}
		}
		state = States.SELECTED;
	}

	public void showFullScreen(Marker marker) {
		PBounds screenBounds = camera.getViewBounds();
		fader.setBounds(screenBounds);
		fader.setVisible(true);
		fader.moveToFront();
		if (faderFadeActivity != null) {
			faderFadeActivity.terminate();
		}
		faderFadeActivity = fader.animateToTransparency(0.8f, 250l);

		fullScreenMarker = marker;
		fullScreenMarker.moveToFront();
		fullScreenMarker.zoomThumbTo(screenBounds.getCenter2D(), screenBounds
				.getWidth()
				* camera.getViewScale(), screenBounds.getHeight()
				* camera.getViewScale());
		if (camera.getChildrenCount() > 0) {
			PNode slider = camera.getChild(0);
			slider.setPickable(false);
			if (sliderFadeActivity != null) {
				sliderFadeActivity.terminate();
			}
			sliderFadeActivity = slider.animateToTransparency(0.0f, 250l);
		}

		state = States.ZOOMED;

	}

	public void returnFromFullScreen() {
		if (fullScreenMarker != null) {
			fullScreenMarker.zoomThumb(false);
		}
		fullScreenMarker = null;
		PNode slider = camera.getChild(0);
		slider.setPickable(true);
		if (sliderFadeActivity != null) {
			sliderFadeActivity.terminate();
		}
		sliderFadeActivity = slider.animateToTransparency(1.0f, 250l);
		if (faderFadeActivity != null) {
			faderFadeActivity.terminate();
		}
		faderFadeActivity = fader.animateToTransparency(0.0f, 250l);

		if (activeMarkers.size() > 1 && !unlockSelectionOnReturn) {
			state = States.SELECTED;
		} else {
			clearActiveMarkers();
			state = States.NORMAL;
		}

	}

	public void processEvent(PInputEvent event, int type) {
		PNode node = event.getPickedNode();
		double viewScale = event.getCamera().getViewScale();
		Point2D point = event.getPosition();

		if (node == mapNode && state == States.SELECTED) {
			if (!activeMarkers.isEmpty() & !isActiveMarker(point, viewScale)
					&& !selectionLocked) {
				clearActiveMarkers();
			}
		}
		if (type == MouseEvent.MOUSE_CLICKED) {
			if (node == mapNode) {
				mapNode.globalToGeoCoordinates(point);
			}
			if (state == States.ZOOMED) {
				for (MediaInputEventListener listener : listeners) {
					listener
							.mediaDeactivated(this, fullScreenMarker.getMedia());
				}
				// returnFromFullScreen();
				return;
			}
			if (isMarker(node)) {
				Marker marker = getMarker(node);
				point = marker.getOffset();
				// showThumb( marker );

				if (state == States.NORMAL || state == States.SELECTED) {
					if (activeMarkers.contains(marker)) {
						if (activeMarkers.size() == 1) {
							// showFullScreen( marker );
							for (MediaInputEventListener listener : listeners) {
								listener
										.mediaActivated(this, marker.getMedia());
							}
						} else {
							if (selectionLocked) {
								// showFullScreen( marker );
								for (MediaInputEventListener listener : listeners) {
									listener.mediaActivated(this, marker
											.getMedia());
								}
							} else {
								showThumbs(point);
								lockSelection();
							}
						}
					} else {
						clearActiveMarkers();
					}
				}

				/*
				 * if( !marker.getThumbNailShown() && activeMarkers.contains(
				 * marker ) ) { showThumbs( point ); } if(
				 * activeMarkers.contains( marker ) ) { lockSelection(); } else
				 * { if( selectionLocked ) { clearActiveMarkers(); } }
				 */
			} else {
				if (state == States.NORMAL || state == States.SELECTED) {
					clearActiveMarkers();
				}
			}
		}
		if (type == MouseEvent.MOUSE_ENTERED && isMarker(node)) {
			Marker marker = getMarker(node);

			point = marker.getOffset();
			if (state == States.NORMAL || state == States.SELECTED) {
				if (!activeMarkers.contains(marker) && !selectionLocked) {
					clearActiveMarkers();
				}
				// System.out.println( "Marker offset" + getMarker( node
				// ).getOffset() + " point: " + point + " Distance: " +
				// getMarker( node ).getOffset().distance( point ));
				if (activeMarkers.isEmpty()) {
					selectMarkers(point, viewScale);
				}
				/*
				 * if( !activeMarkers.get( activeMarkers.size()-1
				 * ).getThumbNailShown() ) { showThumbs( point ); }
				 */
				if (activeMarkers.size() == 1) {
					showThumbs(point);
				}
				if (activeMarkers.contains(marker)) {
					marker.moveToFront();
					marker.setActive(true);
					marker.zoomThumb(true);
				}
			}
			for( MediaInputEventListener listener: listeners )
			{
				listener.mediaHovered( this, marker.getMedia() );
			}
			// getMarker( node ).zoomIn();
		}
		if (type == MouseEvent.MOUSE_EXITED && isMarker(node)) {
			if (state == States.SELECTED) {
				getMarker(node).zoomThumb(false);
				getMarker(node).setActive(false);
				if (!isActiveMarker(point, viewScale) && !selectionLocked) {
					clearActiveMarkers();
				}
			}
		}

	}

}