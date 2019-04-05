package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.P3DRect;

public class PButton extends PNode implements PInputEventListener
{
	public static final String DEFAULT_COMMAND = "Pressed";

	private Vector<ActionListener> listeners;
	private String command = DEFAULT_COMMAND;
	private boolean clickInitiated = false;
	private PText text = null;
	private P3DRect rect = null;
	private PImage pImage = null;
	private PImage highLight = null;
	private Image image = null;
	private Image highImage = null;
	private Image pressedImage = null;
	private boolean sticky = false;
	private boolean pressed = false;

	public PButton()
	{
		listeners = new Vector<ActionListener>();
		addInputEventListener( this );
	}

	public PButton( String string )
	{
		this();
		text = new PText( string );
		PBounds bounds = text.getFullBounds();
		rect = new P3DRect( bounds );
		rect.setPaint( Color.gray );
		addChild( rect );
		addChild( text );
	}

	public PButton( String text, boolean sticky )
	{
		this( text );
		this.sticky = sticky;
	}

	public PButton( Image image, Image highImage )
	{
		this();
		this.image = image;
		this.highImage = highImage;
		pImage = new PImage( image );
		highLight = new PImage( highImage );
		highLight.centerBoundsOnPoint( pImage.getBounds().getCenter2D().getX(), pImage.getBounds().getCenter2D().getY() );
		highLight.setPickable( false );
		highLight.setVisible( false );
		addChild( highLight );
		addChild( pImage );
	}

	public PButton( Image image, Image highImage, Image pressedImage )
	{
		this();
		this.image = image;
		this.highImage = highImage;
		this.pressedImage = pressedImage;
		pImage = new PImage( image );
		highLight = new PImage( highImage );
		highLight.centerBoundsOnPoint( pImage.getBounds().getCenter2D().getX(), pImage.getBounds().getCenter2D().getY() );
		highLight.setPickable( false );
		highLight.setVisible( false );
		addChild( highLight );
		addChild( pImage );
	}


	public void setImages( Image image, Image highImage, boolean highlighted )
	{
		this.image = image;
		this.highImage = highImage;
		if( pImage == null )
		{
			pImage = new PImage( image );
			addChild( pImage );
		}
		else
		{
			pImage.setImage( image );
		}
		if( highlighted )
		{
			highLight.setVisible( true );
		}
	}

	public void setImages( Image image, Image highImage, Image pressedImage, boolean highlighted )
	{
		this.image = image;
		this.highImage = highImage;
		this.pressedImage = pressedImage;
		if( pImage == null )
		{
			pImage = new PImage( image );
			addChild( pImage );
		}
		else
		{
			pImage.setImage( image );
		}
		if( highlighted )
		{
			highLight.setVisible( true );
		}
	}


	public void addActionListener( ActionListener listener )
	{
		listeners.add( listener );
	}

	public void removeActionListener( ActionListener listener )
	{
		listeners.remove( listener );
	}

	private boolean isThis( PNode node )
	{
		return( node == this || isAncestorOf( node ) );
	}

	public void processEvent( PInputEvent event, int type )
	{
		boolean clickCompleted = false;
		PNode node = event.getPickedNode();
		Point2D point = event.getCanvasPosition();
		PNode clickedNode = event.getCamera().pick( point.getX(), point.getY(), 3 ).getPickedNode();

		if( node == this || isAncestorOf( node ) )
		{
			if( pImage != null )
			{
				// Handle highlighting
				if( type == MouseEvent.MOUSE_ENTERED )
				{
					//pImage.setImage( highImage );
					highLight.setVisible( true );
				}
				if( type == MouseEvent.MOUSE_EXITED  )
				{
					//pImage.setImage( image );
					highLight.setVisible( false );

				}
			}	    
			// Clicks
			if( (type == MouseEvent.MOUSE_RELEASED) && clickInitiated  )
			{
				if( isThis( clickedNode ) )
				{
					clickCompleted = true;
					//System.out.println( "Released" );
					//System.out.println( node );
				}
				else
				{
					clickInitiated = false;
					setPressed( pressed );
				}

			}
			if( clickCompleted )
			{
				//System.out.println( "Clicked" );
				ActionEvent actionEvent = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command, event.getModifiers() );
				for( ActionListener listener : listeners )
				{
					listener.actionPerformed( actionEvent );
				}
				clickInitiated = false;
				if( sticky )
				{
					pressed = !pressed;
				}
				setPressed( pressed );
				event.setHandled( true );
			}
			if( type == MouseEvent.MOUSE_PRESSED )
			{
				//System.out.println( "Pressed" );
				clickInitiated = true;
				setPressed( true );
				event.setHandled( true );
			}
		}
		else
		{
			if( type == MouseEvent.MOUSE_RELEASED )
			{
				//System.out.println( "No click" );
				clickInitiated = false;
				setPressed( false );
			}
		}
	}

	private void setPressed( boolean down )
	{
		if( pImage != null )
		{
			if( down )
			{
				if( pressedImage == null )
				{
					pImage.setTransparency( 0.8f );
				}
				else
				{
					pImage.setImage( pressedImage );
					pImage.centerBoundsOnPoint( getBounds().getCenterX(), getBounds().getCenterY() );

				}
			}
			else
			{
				pImage.setImage( image );
				pImage.centerBoundsOnPoint( getBounds().getCenterX(), getBounds().getCenterY() );
				pImage.setTransparency( 1.0f );
			}
		}
		if( rect != null )
		{
			rect.setRaised( !down );
		}
		if( text != null )
		{
			if( down )
			{
				text.offset( 1, 1 );
			}
			else
			{
				text.offset( -1, -1 );
			}
		}
		repaint();
	}

	public boolean setBounds( double x, double y, double width, double height )
	{
		boolean changed = super.setBounds( x, y, width, height );
		PBounds bounds = getBounds();
		if( rect != null )
		{
			rect.setBounds( bounds );
			rect.repaint();
		}
		if( text != null )
		{
			text.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}
		if( pImage != null )
		{
			pImage.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}
		if( highLight != null )
		{
			highLight.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}

		return changed;
	}


	public boolean setBounds( Rectangle2D newBounds )
	{
		boolean changed = super.setBounds( newBounds );
		PBounds bounds = getBounds();
		if( rect != null )
		{
			rect.setBounds( bounds );
			rect.repaint();
		}
		if( text != null )
		{
			text.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}
		if( pImage != null )
		{
			pImage.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}	
		if( highLight != null )
		{
			highLight.centerBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
		}	
		return changed;
	}
}