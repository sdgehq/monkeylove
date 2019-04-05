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
		if( event.getSource() == camera && !fromSlider )
		{
			validateZoomLevel();
		}
	}

	public void validateZoomLevel()
	{
		double viewScale = camera.getViewScale();
		if( previousViewScale != viewScale )
		{	    
			int zoomLevel = mapNode.getZoomLevel( viewScale );
			int levels = mapNode.getZoomLevels();
			double zoom = zoomLevel/(double)levels;
			double remainder = 0;

			if( zoomLevel >= 1 )
			{
				// Display fancy intermediary steps
				double previousScale = Math.pow( 2, zoomLevel-1 );
				//System.out.println( "Level: " + zoomLevel + " previous scale: " + previousScale );
				remainder = (viewScale-previousScale)/previousScale;
				//System.out.println( "View scale: " + viewScale + " Level: " + zoomLevel + " Remainder: " + remainder );
				remainder = remainder/levels-1.0/levels;
				//System.out.println( "Scaled Remainder: " + remainder );
				zoom+=remainder;
			}
			zoom = Math.min( zoom, 1.0 );
			zoom = Math.max( zoom, 0.0 );
			//System.out.println( "Value: " + zoom );
			if( zoomSlider != null )
			{
				zoomSlider.setValue( zoom );
			}
			previousViewScale = viewScale;
		}
	}	

	public void actionPerformed( ActionEvent e )
	{
		Point2D point = camera.getViewBounds().getCenter2D();
		if( e.getSource() == zoomInButton )
		{
			zoomCamera( point, true );
		}
		if( e.getSource() == zoomOutButton )
		{
			zoomCamera( point, false );
		}
	}

	public void processEvent( PInputEvent event, int type )
	{	
		if( type == MouseEvent.MOUSE_RELEASED )
		{
			if( event.getPickedNode() == mapNode && event.getClickCount() == 2 )
			{
				Point2D point = event.getPositionRelativeTo( mapNode );
//				System.out.println( "Clicked at: " + point );
				boolean in = !(event.isControlDown() || event.isMetaDown() || event.isShiftDown() );
				zoomCamera( point, in );
			}
		}
	}

	public void zoomCamera( Point2D point, boolean in )
	{
		if( zooming )
		{
			return;
		}
		zooming = true;
		double viewScale = camera.getViewScale()/mapNode.getScale();
		int zoomLevel = mapNode.getZoomLevel(viewScale);
		PBounds viewBounds = camera.getViewBounds();
		double differenceToZoom = 1.0;
		int targetZoomLevel = zoomLevel;
		if( !in )
		{
			targetZoomLevel = Math.max( zoomLevel-1, 0 );
		}
		else
		{
			targetZoomLevel = Math.min( zoomLevel+1, mapNode.getZoomLevels() );
		}
		differenceToZoom = Math.pow( 2.0, targetZoomLevel )/viewScale;
		if( differenceToZoom == 1.0 )
		{
			zooming = false;
			return;
		}

//		System.out.println( "View scale: " + viewScale + " needs to zoom by: " + differenceToZoom + " to: " + viewScale*differenceToZoom );
		point = mapNode.localToGlobal( point );
		//System.out.println( "View bounds" + viewBounds );
		PBounds newBounds = new PBounds( point.getX(), point.getY(), viewBounds.getWidth()/differenceToZoom, viewBounds.getHeight()/differenceToZoom );
		newBounds.moveBy( -newBounds.getWidth()/2.0, -newBounds.getHeight()/2.0 );
		camera.globalToLocal( newBounds );
		//System.out.println( "New bounds" + newBounds );
		camera.animateViewToCenterBounds( newBounds, true, 1000l ).setDelegate( this );	
	}

	public void zoomCameraToLevel( Point2D point, int level )
	{
		if( zooming )
		{
			return;
		}
		zooming = true;
		double viewScale = camera.getViewScale()/mapNode.getScale();
		int zoomLevel = mapNode.getZoomLevel(viewScale);
		PBounds viewBounds = camera.getViewBounds();
		double differenceToZoom = 1.0;
		int targetZoomLevel = level;
		targetZoomLevel = Math.max( 0, targetZoomLevel );
		targetZoomLevel = Math.min( mapNode.getZoomLevels(), targetZoomLevel );

		differenceToZoom = Math.pow( 2.0, targetZoomLevel )/viewScale;
		if( differenceToZoom == 1.0 )
		{
			zooming = false;
			return;
		}	

//		System.out.println( "View scale: " + viewScale + " needs to zoom by: " + differenceToZoom + " to: " + viewScale*differenceToZoom );
		point = mapNode.localToGlobal( point );
		//System.out.println( "View bounds" + viewBounds );
		PBounds newBounds = new PBounds( point.getX(), point.getY(), viewBounds.getWidth()/differenceToZoom, viewBounds.getHeight()/differenceToZoom );
		newBounds.moveBy( -newBounds.getWidth()/2.0, -newBounds.getHeight()/2.0 );
		camera.globalToLocal( newBounds );
		//System.out.println( "New bounds" + newBounds );
		camera.animateViewToCenterBounds( newBounds, true, 1000l ).setDelegate( this );	
	}

	public void setZoomSlider( ZoomSlider zoomSlider )
	{
		if( this.zoomSlider != null )
		{
			this.zoomSlider.removeChangeListener( this );
			zoomInButton.removeActionListener( this );
			zoomOutButton.removeActionListener( this );
		}
		this.zoomSlider = zoomSlider;
		zoomInButton = zoomSlider.getZoomInButton();
		zoomOutButton = zoomSlider.getZoomOutButton();
		zoomInButton.addActionListener( this );
		zoomOutButton.addActionListener( this );
		this.zoomSlider.addChangeListener( this );
	}
	
	public void stateChanged( ChangeEvent e )
	{
		if( e.getSource() == zoomSlider && !zooming )
		{
			int targetLevel = (int)(zoomSlider.getValue()*(mapNode.getZoomLevels()+1));
			//System.out.println( "Value: " + zoomSlider.getValue() + " Target: " + targetLevel );

			Point2D point = camera.getViewBounds().getCenter2D();
			if( !zoomSlider.getValueIsAdjusting() )
			{
				fromSlider = true;
				zoomCameraToLevel( point, targetLevel );
			}
		}
	}

	public void activityFinished( PActivity activity )
	{
		zooming = false;
		fromSlider = false;
		//mapNode.invalidatePaint();
		mapNode.validateFullPaint();
		mapNode.repaint();
		validateZoomLevel();
	}

	public void activityStarted( PActivity activity )
	{
	}

	public void activityStepped( PActivity activity )
	{
	}

}