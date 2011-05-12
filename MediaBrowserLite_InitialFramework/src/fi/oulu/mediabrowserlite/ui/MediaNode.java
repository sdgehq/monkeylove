package fi.oulu.mediabrowserlite.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import fi.oulu.mediabrowserlite.media.BasicMediaListener;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaListener;
import fi.oulu.mediabrowserlite.media.MediaObject;
import fi.oulu.mediabrowserlite.media.mediaresources.BasicMediaResourceObserver;
import fi.oulu.mediabrowserlite.media.mediaresources.MediaResourceManager;
/**
 * This class represents a media object in the graphical interface. The current
 * implementation displays the media object as an image. If the actual image file is 
 * unavailable or has not been set, a placeholder icon is displayed instead.
 * @author j-p
 *
 */
public class MediaNode extends PPath {

	private static BufferedImage PICTURE_UNAVAILABLE_ICON = null;
	static {
		try {
			PICTURE_UNAVAILABLE_ICON = ImageIO.read(MediaNode.class
					.getResourceAsStream("camera-icon.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final double BORDER_X_THUMB = 0.5;
	private static final double BORDER_Y_THUMB = 0.5;
	public static final Stroke STROKE_THUMB = null;
	public static final Color STROKE_PAINT_THUMB = null;

	private static final double BORDER_X_CONTENT = 0;
	private static final double BORDER_Y_CONTENT = 0;
	private static final Stroke STROKE_CONTENT = null;
	private static final Color STROKE_PAINT_CONTENT = null;

	public enum Mode {
		CONTENT, THUMBNAIL
	}

	private Mode mode = Mode.THUMBNAIL;
	private Media media;
	private PImage pImage;
	private PPath selectionBorder = null;
	private double xBorder = BORDER_X_THUMB;
	private double yBorder = BORDER_Y_THUMB;

	private PBounds storedBounds;

	private MediaListener mediaListener = new MediaObserver();

	private PText infoText = new PText();

	public MediaNode() {
		super(new Rectangle(0, 0, 1, 1));
		setPaint(Color.WHITE);
		setStroke(null);
		setStrokePaint(null);
		this.pImage = new PImage();
		addChild(pImage);
		// addChild(infoText);
		setChildrenPickable(false);
		setMode(mode);
		selectionBorder = PPath.createRectangle(0, 0, 1f, 1f);
		selectionBorder.setStrokePaint(SystemColor.textHighlight);
		selectionBorder.setStroke(new BasicStroke(2.0f));
		selectionBorder.setPaint(null);
		selectionBorder.setPickable(false);
		selectionBorder.setVisible(false);
		addChild(selectionBorder);
	}

	public MediaNode(Media media) {
		this();
		setMedia(media);
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		if (this.media != null) {
			this.media.removeMediaListener(mediaListener);
		}
		this.media = media;
		if (media != null) {
			media.addMediaListener(mediaListener);
			infoText.setText(media.getMediaType());
		}
		setImage(media);
		setBounds(getBounds());
	}

	public Mode getMode() {
		return mode;
	}

	public void setBorderColor(Color color) {
		setStrokePaint(color);
	}

	public void setMode(Mode mode) {
		boolean changing = this.mode != mode;
		this.mode = mode;
		if (mode == Mode.THUMBNAIL) {
			if (media != null) {
				setSelectionVisible(media.isSelected());
			}
			setBorder(BORDER_X_THUMB, BORDER_Y_THUMB);
		} else {
			setSelectionVisible(false);
			setBorder(BORDER_X_CONTENT, BORDER_Y_CONTENT);
		}
		if (changing) {
			setImage(media);
		}
	}

	public void storeBounds() {
		storeBounds(getBounds());
	}

	public synchronized void storeBounds(PBounds bounds) {
		this.storedBounds = bounds;
	}

	public synchronized PBounds getStoredBounds() {
		return storedBounds;
	}

	private void setImage(Media media) {
		setImage(media, getMode() == Mode.CONTENT);
	}

	private void setImage(Media media, boolean asynch) {
		if (pImage != null) {
			if (media != null) {
				if (asynch) {
					// setImage(MediaResourceManager.getInstance().getThumbnail(
					// media));
					loadImage(media);
				} else {
					setImage(getImage(media));
				}
			} else {
				setImage((Image) null);
			}
		}
	}

	private void setImage(Image image) {
		if (pImage != null) {
			if (image == null) {
				pImage.setImage(PICTURE_UNAVAILABLE_ICON);
			} else {
				pImage.setImage(image);
			}
			calculateImageBounds(getBounds());
		}
		repaint();
	}

	protected void loadImage(Media media) {
		if (getMode() == Mode.CONTENT) {
			MediaResourceManager.getInstance().getContentImage(media,
					new BasicMediaResourceObserver() {
						@Override
						public void contentImageLoaded(Media media, Image image) {
							setImage(image);
						}

						@Override
						public void contentImageLoadingFailed(Media media) {
							setImage((Image) null);
						}

					});
		} else {
//			System.out.println("Typee: "+media);
			MediaResourceManager.getInstance().getThumbnail(media,
					new BasicMediaResourceObserver() {
						@Override
						public void thumbnailLoaded(Media media, Image thumb) {
							setImage(thumb);
						}

						@Override
						public void thumbnailLoadingFailed(Media media) {
							setImage((Image) null);
						}
					});
		}
	}

	protected Image getImage(Media media) {
		if (getMode() == Mode.CONTENT) {
			return MediaResourceManager.getInstance().getContentImage(media);
		} else {
			return MediaResourceManager.getInstance().getThumbnail(media);
		}
	}

	public double getXBorder() {
		return xBorder;
	}

	public void setXBorder(double border) {
		this.xBorder = border;
	}

	public double getYBorder() {
		return yBorder;
	}

	public void setYBorder(double border) {
		this.yBorder = border;
	}

	public void setBorder(double xBorder, double yBorder) {
		this.xBorder = xBorder;
		this.yBorder = yBorder;
	}

	private void setSelectionVisible(boolean visible) {
		if (visible) {
			// setStroke((new BasicStroke(1.5f)));
			// setStrokePaint(Color.RED);
			PBounds bounds = getBounds();

			selectionBorder.setBounds((float) bounds.getMinX() - 1f,
					(float) bounds.getMinY() - 1f,
					(float) bounds.getWidth() + 1f,
					(float) bounds.getHeight() + 1f);
			selectionBorder.setVisible(true);

		} else {
			// setStroke(null);
			// setStrokePaint(null);
			selectionBorder.setVisible(false);
		}
	}

	public double getImageSizeRatio() {
		double ratio = 1;
		if (pImage != null) {
			Image image = pImage.getImage();
			if (image != null) {
				ratio = image.getWidth(null) / (double) image.getHeight(null);
			}
		}
		return ratio;
	}

	private void calculateImageBounds(PBounds availableBounds) {
		if (pImage != null) {
			Image image = pImage.getImage();
			if (image != null) {
				double iW = image.getWidth(null);
				double iH = image.getHeight(null);
				double xScale = (availableBounds.getWidth() - getXBorder() * 2)
						/ iW;
				double yScale = (availableBounds.getHeight() - getYBorder() * 2)
						/ iH;
				double scale = Math.min(xScale, yScale);
				iW = iW * scale;
				iH = iH * scale;
				pImage.setBounds(0, 0, iW, iH);
				pImage.setOffset(getX() + getWidth() / 2.0 - pImage.getWidth()
						/ 2.0, getY() + getHeight() / 2.0 - pImage.getHeight()
						/ 2.0);

				infoText.setOffset(getX(), getY());

			}
		}
	}

	@Override
	public boolean setBounds(double x, double y, double w, double h) {
		boolean retVal = super.setBounds(x, y, w, h);
		calculateImageBounds(getBounds());
		if (selectionBorder != null) {
			selectionBorder.setBounds(x, y, w, h);
		}
		double wy = -20;
		double wx = -18;
		return retVal;
	}

	class MediaObserver extends BasicMediaListener {
		@Override
		public void mediaSetSelected(Media media, boolean selected) {
			if (media.equals(getMedia())) {
				setSelectionVisible(selected);
			}
		}

		@Override
		public void mediaSetVisible(Media media, boolean visible) {
			if (media.equals(getMedia())) {
				setVisible(visible);
				setPickable(visible);
			}
		}

		@Override
		public void thumbnailChanged(Media media, BufferedImage thumbnail) {
			if (getMode() == Mode.THUMBNAIL) {
				setImage(thumbnail);
			}
		}

		@Override
		public void thumbnailChanged(Media media) {
			loadImage(media);
		}

		@Override
		public void contentImageChanged(Media media, BufferedImage contentImage) {
			if (getMode() == Mode.CONTENT) {
				setImage(contentImage);
			}
		}

		@Override
		public void mediaContentChanged(Media media, Object content) {
		}

		@Override
		public void contentImageChanged(Media media) {
			loadImage(media);
		}

	}
}
