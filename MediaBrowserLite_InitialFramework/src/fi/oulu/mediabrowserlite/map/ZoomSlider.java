package fi.oulu.mediabrowserlite.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class ZoomSlider extends PNode implements PInputEventListener
{
	public static final String DEFAULT_COMMAND = "Pressed";

	private Vector<ChangeListener> listeners;
	private Image knobImage = null;
	private Image knobImagePressed = null;
	private Image knobImageHigh = null;
	private PImage knob;
	private PNode knobShadow;
	private boolean slideInitiated = false;
	private boolean clickInitiated = false;
	private double value = 0;
	private int steps = 6;

	private PButton plusButton;
	private PButton minusButton;
	private BufferedImage shadow;
	private boolean highlight = false;
	private PBounds rect = new PBounds( 16, 25, 8, 150 ); //40 x 200
	private Color tickColor = Color.decode( "#0586d8" );

	public ZoomSlider()
	{
		listeners = new Vector<ChangeListener>();
		shadow = getImage( "Shadow.png" );	
		addInputEventListener( this );
		//knob = PPath.createEllipse( 0, 0, 8, 8 );
		knobImage = getImage( "Knob.png" );
		knobImagePressed = getImage( "KnobPressed.png" );
		knobImageHigh = getImage( "KnobHigh.png" );
		knob = new PImage( knobImage );
		knobShadow = new PImage( getClass().getResource( "KnobShadow.png" ) );
		knobShadow.setPickable( false ); 
		knob.centerBoundsOnPoint( 0, 0 );
		knob.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+rect.getHeight() );
		knobShadow.centerBoundsOnPoint( 0, 0 );
		knobShadow.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+rect.getHeight() );	
		Image plusImage = getImage( "PlusButton.png" );
		Image plusImageHigh = getImage( "PlusButtonHigh.png" );
		Image plusImagePressed = getImage( "PlusButtonPressed.png" );
		plusButton = new PButton( plusImage, plusImageHigh, plusImagePressed );
		plusButton.setBounds( 0, 0, 18, 18 );
		plusButton.centerBoundsOnPoint( 0, 0 );
		plusButton.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getBounds().getY()-plusButton.getBounds().getHeight()/2.0 );
		addChild( plusButton );
		Image minusImage = getImage( "MinusButton.png" );
		Image minusImageHigh = getImage( "MinusButtonHigh.png" );
		Image minusImagePressed = getImage( "MinusButtonPressed.png" );
		minusButton = new PButton( minusImage, minusImageHigh, minusImagePressed );
		minusButton.setBounds( 0, 0, 18, 18 );
		minusButton.centerBoundsOnPoint( 0, 0 );
		minusButton.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getBounds().getY()+rect.getBounds().getHeight()+minusButton.getBounds().getHeight()/2.0 );
		addChild( minusButton );
		addChild( knobShadow );
		addChild( knob );
		setBounds( 0, 0, shadow.getWidth(), shadow.getHeight() );
	}

	public PButton getZoomInButton()
	{
		return plusButton;
	}

	public PButton getZoomOutButton()
	{
		return minusButton;
	}

	public BufferedImage getImage( String fileName )
	{
		BufferedImage image = null;
		try
		{
			image = ImageIO.read( getClass().getResource( fileName ) );
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
		return image;
	}

	public void addChangeListener( ChangeListener listener )
	{
		listeners.add( listener );
	}

	public void removeChangeListener( ChangeListener listener )
	{
		listeners.remove( listener );
	}

	public void processEvent( PInputEvent event, int type )
	{
		PNode node = event.getPickedNode();
		Point2D point = event.getCanvasPosition();
		PNode clickedNode = event.getCamera().pick( point.getX(), point.getY(), 3 ).getPickedNode();

		if( node == knob && type == MouseEvent.MOUSE_ENTERED && !slideInitiated )
		{
			knob.setImage( knobImageHigh );
			knob.centerBoundsOnPoint( 0, 0 );
		}
		if( node == knob && type == MouseEvent.MOUSE_EXITED && !slideInitiated )
		{
			knob.setImage( knobImage );
			knob.centerBoundsOnPoint( 0, 0 );
		}

		if( node == knob && type == MouseEvent.MOUSE_PRESSED )
		{
			slideInitiated = true;
			knob.setImage( knobImagePressed );
			knob.centerBoundsOnPoint( 0, 0 );
			event.setHandled( true );
			//System.out.println( "It's the knob" );
		}
		if( node == this && type == MouseEvent.MOUSE_PRESSED )
		{
			//System.out.println( "Rect" );
			point = event.getPositionRelativeTo( this );
			if( rect.contains( point ) )
			{
				event.setHandled( true );
				clickInitiated = true;
			}
		}
		if( type == MouseEvent.MOUSE_DRAGGED && slideInitiated )
		{
			//System.out.println( "Dragged" );
			point = event.getPositionRelativeTo( this );
			double position = point.getY();
			if( position < rect.getY() )
			{
				position = rect.getY();
			}
			if( position > (rect.getY()+rect.getHeight()) )
			{
				position = rect.getY()+rect.getHeight();
			}
			event.setHandled( true );
			postChange( position );
		}
		if( type == MouseEvent.MOUSE_RELEASED )
		{
			if( (clickInitiated && clickedNode == this ) || slideInitiated )
			{	    
				slideInitiated = false;
				clickInitiated = false;

				if( node == knob )
				{
					knob.setImage( knobImageHigh );
				}
				else
				{
					knob.setImage( knobImage );
				}
				knob.centerBoundsOnPoint( 0, 0 );

				//System.out.println( "Clicked" );
				point = event.getPositionRelativeTo( this );
				double position = point.getY();
				if( position < rect.getY() )
				{
					position = rect.getY();
				}
				if( position > (rect.getY()+rect.getHeight()) )
				{
					position = rect.getY()+rect.getHeight();
				}		
				// Calculate the zoom step, if used
				if( steps > 0 )
				{
					double pos = position-rect.getY();
					int step = (int)(pos/rect.getHeight()*(steps+1));
					step = Math.min( steps, step );
					position = rect.getHeight()*step/(double)steps+rect.getY();
					step = steps-step;
					//System.out.println( "Step: " + step );
				}

				event.setHandled( true );
				postChange( position );
			}	    
		}
	}

	public double getValue()
	{
		return value;
	}

	public boolean getValueIsAdjusting()
	{
		return slideInitiated;
	}

	public void setValue( double value ) throws IllegalArgumentException
	{
		if( value < 0 || value > 1.0 )
		{
			throw new IllegalArgumentException();
		}
		this.value = value;
		knob.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getHeight() );
		knobShadow.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getHeight() );
	}

	private void postChange( double position )
	{	
		knob.setOffset(  rect.getBounds().getX()+rect.getBounds().getWidth()/2.0, position );	
		knobShadow.setOffset( rect.getBounds().getX()+rect.getBounds().getWidth()/2.0, position  );

		value = 1.0-(position-rect.getY())/rect.getHeight();	
		//System.out.println( "Value: " + value );
		ChangeEvent event = new ChangeEvent( this );
		for( ChangeListener listener : listeners )
		{
			listener.stateChanged( event );
		}
	}

	protected void paint( PPaintContext context )
	{
		Graphics2D g2 = context.getGraphics();
		g2.drawImage( shadow, 0, 0, null );
		PBounds bounds = getBounds();
		int arcWidth = (int)(bounds.getWidth()/2.0);
		g2.setColor( Color.white );
		g2.fillRect( (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight() ) ;
		g2.setColor( Color.black );
		g2.drawRect( (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight() ) ;
		g2.setColor( tickColor );
		double tickPosition = 0;
		Rectangle2D tick;
		for( int i=0; i <= steps; i++ )
		{
			tickPosition = rect.getY()+1+i/(double)steps*(rect.getHeight()-3);
			tick = new Rectangle2D.Double(  rect.getX()+2, tickPosition, rect.getWidth()-4, 1.0 );
			g2.draw( tick );
		}

	}

	public boolean setBounds( double x, double y, double width, double height )
	{
		boolean changed = super.setBounds( x, y, width, height );
		PBounds bounds = getBounds();
		knob.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getBounds().getHeight() );
		knobShadow.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getBounds().getHeight() );
		//plusButton.setOffset( -plusButton.getBounds().getWidth()/4.0, -plusButton.getBounds().getHeight() );
		//minusButton.setOffset( -minusButton.getBounds().getWidth()/4.0, getBounds().getHeight() );

		return changed;
	}

	public boolean setBounds( Rectangle2D newBounds )
	{
		boolean changed = super.setBounds( newBounds );
		PBounds bounds = getBounds();
		knob.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getBounds().getHeight() );
		knobShadow.setOffset( rect.getX()+rect.getBounds().getWidth()/2.0, rect.getY()+(1.0-value)*rect.getBounds().getHeight() );
		//plusButton.setOffset( -plusButton.getBounds().getWidth()/4.0, -plusButton.getBounds().getHeight() );
		//minusButton.setOffset( -minusButton.getBounds().getWidth()/4.0, getBounds().getHeight() );

		return changed;
	}


}