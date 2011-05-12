package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;

public class CrosshairMarker extends PNode implements PropertyChangeListener, PActivity.PActivityDelegate
{
	private double viewscale = 1;
	PPath left;
	PPath right;
	PPath top;
	PPath bottom;
	private JFrame owner;
	private boolean cursorHidden = false;
	
	private PActivity hideActivity = null;
	private PActivity leftActivity = null;
	private PActivity rightActivity = null;
	private PActivity topActivity = null;
	private PActivity bottomActivity = null;
	
	public CrosshairMarker(JFrame owner)
	{
		this.owner = owner;
		left = PPath.createLine( -30, 0, -5, 0 );
		left.setStrokePaint( Color.green );
		addChild( left );
		right = PPath.createLine( 5, 0, 25, 0 );
		right.setStrokePaint( Color.green );
		addChild( right );
		top = PPath.createLine( 0, -30, 0, -5 );
		top.setStrokePaint( Color.green );
		addChild( top );
		bottom = PPath.createLine( 0, 5, 0, 30 );
		bottom.setStrokePaint( Color.green );
		addChild( bottom );
		setPickable( false );
	}
	
	public void setCursorHidden(boolean cursorHidden) {
		this.cursorHidden = cursorHidden;
	}
	
	public boolean getCursorHidden() {
		return this.cursorHidden;
	}

	public void setPosition( Point2D globalPoint )
	{
//		if( hideActivity != null )
//		{
//			hideActivity.terminate();
//		}
//		if( bottomActivity != null )
//		{
//			leftActivity.terminate();
//			rightActivity.terminate();
//			topActivity.terminate();
//			bottomActivity.terminate();
//		}
		setTransparency( 1f );
		moveToFront();
		setOffset( globalPoint );
		left.setBounds( -600, 0, 25, 1 );
		leftActivity = left.animateToBounds( -30, 0, 25, 1, 250l );
		right.setBounds( 600, 0, 25, 1 );
		rightActivity = right.animateToBounds( 5, 0, 25, 1, 250l );
		top.setBounds( 0, -600, 1, 25 );
		topActivity = top.animateToBounds( 0, -30, 1, 25, 250l );
		bottom.setBounds( 0, 600, 1, 25 );
		bottomActivity = bottom.animateToBounds( 0, 5, 1, 25, 250l );
		bottomActivity.setDelegate( this );
	}
	
	public void moveCrosshair( Point2D globalPoint ) {
		setTransparency( 1f );
		moveToFront();
		setOffset( globalPoint );
		left.setBounds( -600, 0, 25, 1 );
		leftActivity = left.animateToBounds( -30, 0, 25, 1, 0l );
		right.setBounds( 600, 0, 25, 1 );
		rightActivity = right.animateToBounds( 5, 0, 25, 1, 0l );
		top.setBounds( 0, -600, 1, 25 );
		topActivity = top.animateToBounds( 0, -30, 1, 25, 0l );
		bottom.setBounds( 0, 600, 1, 25 );
		bottomActivity = bottom.animateToBounds( 0, 5, 1, 25, 0l );
//		bottomActivity.setDelegate( this );
	}
	
	public void propertyChange( PropertyChangeEvent event ) 
	{
		if( event.getSource() instanceof PCamera )
		{
			PCamera camera = (PCamera)event.getSource();
			
//			System.out.println("change ......"+camera.);
			viewscale = camera.getViewScale();
			setScale( 1.0/viewscale );
			hideActivity = animateToTransparency( 0f, 250l );
			hideActivity.setDelegate( this );
//			if (cursorHidden) {
//				this.owner.getContentPane().setCursor(Cursor.getDefaultCursor());
//				this.cursorHidden = false;
//			}
		}
		
	}

	public void activityFinished( PActivity activity ) {
		if( activity == hideActivity )
		{
			hideActivity = null;
		}
		if( activity == bottomActivity )
		{
			leftActivity = null;
			rightActivity = null;
			topActivity = null;
			bottomActivity = null;
		}
//		System.out.println("change ...... finnish");
		
	}

	public void activityStarted( PActivity activity ) 
	{
	}

	public void activityStepped( PActivity activity ) 
	{	
	}
	
	
}
