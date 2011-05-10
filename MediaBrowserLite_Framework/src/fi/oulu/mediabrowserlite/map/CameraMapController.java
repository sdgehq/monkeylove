package fi.oulu.mediabrowserlite.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This is a dummy version of the class for testing
 * @author virtu
 *
 */
public class CameraMapController implements PropertyChangeListener, PInputEventListener, PActivity.PActivityDelegate, ActionListener, ChangeListener
{
	private MapNode mapNode;
	private ZoomSlider zoomSlider;
	private PCamera camera;
	private PButton zoomInButton;
	private PButton zoomOutButton;

	private double previousViewScale = -1.0;
	private boolean zooming = false;
	private boolean fromSlider = false;

	public CameraMapController( MapNode mapNode, ZoomSlider zoomSlider, PCamera camera )
	{
		this.mapNode = mapNode;
		this.zoomSlider = zoomSlider;
		this.camera = camera;

		camera.addPropertyChangeListener( this );
		mapNode.addInputEventListener( this );
		if( zoomSlider != null )
		{
			zoomInButton = zoomSlider.getZoomInButton();
			zoomOutButton = zoomSlider.getZoomOutButton();
			zoomInButton.addActionListener( this );
			zoomOutButton.addActionListener( this );
			zoomSlider.addChangeListener( this );
		}
	}

	public void propertyChange( PropertyChangeEvent event )
	{
	}

	public void validateZoomLevel()
	{
	}	

	public void actionPerformed( ActionEvent e )
	{
	}

	public void processEvent( PInputEvent event, int type )
	{	
	}

	public void zoomCamera( Point2D point, boolean in )
	{
	}

	public void zoomCameraToLevel( Point2D point, int level )
	{
	}

	public void setZoomSlider( ZoomSlider zoomSlider )
	{
	}
	
	public void stateChanged( ChangeEvent e )
	{
	
	}

	public void activityFinished( PActivity activity )
	{
	}

	public void activityStarted( PActivity activity )
	{
	}

	public void activityStepped( PActivity activity )
	{
	}

}