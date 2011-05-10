package fi.oulu.mediabrowserlite.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.Scrollable;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import fi.oulu.mediabrowserlite.media.Media;

/** Dummy class that does nothing useful
 * 
 * @author virtu
 *
 */
public abstract class MediaCanvas extends PCanvas implements
		PActivity.PActivityDelegate, ComponentListener, Scrollable {

	private PPath path;


	public MediaCanvas() {
		setZoomEventHandler(null);
		setPanEventHandler(null);
		path = PPath.createRectangle( 0, 0, 800, 600 );
		path.setPaint( new Color( (float)Math.random(), (float)Math.random(), (float)Math.random() ) );
		getLayer().addChild( path );

	}

	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension( 800, 600 );
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 40;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	public MediaNode getViewedMediaNode() {
		return null;
	}

	public void setViewedMediaNode(MediaNode node) {
	}

	public void resizeViewedMediaNodeToViewBounds() {
	}

	public synchronized void setThumbnailSize(double width, double height) {

	}

	public void setThumbnailSpacing(double spacing) {
	}

	public double getThumbnailSpacing() {
		return 0;
	}

	public synchronized double getThumbnailWidth() {
		return 40;
	}

	public synchronized double getThumbnailHeight() {
		return 40;
	}

	public synchronized void addMedias(List<Media> medias) {
	}

	public synchronized void setMediaNodes(List<MediaNode> nodes) {
	}

	public void addMedia(Media media) {
	}

	public synchronized void addMediaNode(MediaNode mediaNode) {
	}

	public synchronized void removeMediaNode(MediaNode mediaNode) {
	}

	public synchronized void removeMedia(Media media) {
	}

	public synchronized List<MediaNode> getMediaNodes() {
		return null;
	}

	public MediaNode getMediaNode(Media media) {
		return new MediaNode();
	}

	public void organizeMediaNodes() {
	}

	protected abstract void organizeMediaNodes(List<MediaNode> nodes);

	protected void animateMediaNodeToBounds(MediaNode node, double x, double y,
			double w, double h, long time) {
	}

	protected void animateMediaNodeToBounds(MediaNode node, double x, double y,
			double w, double h, long time, boolean skipIfContentMode) {
	}

	public void addActivity(PActivity activity) {
	}

	public void removeActivity(PActivity activity) {
	}

	public void terminateCurrentActivities() {
	}

	public void showFullScreen(Media media) {
	}

	public void showFullScreen(final MediaNode mediaNode) {
	}

	public void returnFromFullScreen(Media media) {
	}

	public void returnFromFullScreen(final MediaNode mediaNode) {
	}

	public void activityFinished(PActivity activity) {
	}

	public void activityStarted(PActivity arg0) {
		// TODO Auto-generated method stub
	}

	public void activityStepped(PActivity arg0) {
		// TODO Auto-generated method stub
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

}
