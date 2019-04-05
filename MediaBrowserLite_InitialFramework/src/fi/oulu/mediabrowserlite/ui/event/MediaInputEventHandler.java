package fi.oulu.mediabrowserlite.ui.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaManager;
import fi.oulu.mediabrowserlite.ui.MediaCanvas;
import fi.oulu.mediabrowserlite.ui.MediaNode;

public class MediaInputEventHandler extends PBasicInputEventHandler {

	public enum MediaInputEventType {
		SELECTION, ACTIVATION
	};

	private MediaCanvas canvas;
	private PNode previousNode = null;
	private List<MediaInputEventListener> listeners = new ArrayList<MediaInputEventListener>();
	private Map<MediaInputEventType, Boolean> eventTypes = new HashMap<MediaInputEventType, Boolean>();
	private boolean triggeringPopup = false;

	public MediaInputEventHandler(MediaCanvas canvas) {
		this.canvas = canvas;
		eventTypes.put(MediaInputEventType.SELECTION, true);
		eventTypes.put(MediaInputEventType.ACTIVATION, true);
	}

	public void addListener(MediaInputEventListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener(MediaInputEventListener listener) {
		return listeners.remove(listener);
	}

	public boolean handlesEventType(MediaInputEventType type) {
		Boolean handles = eventTypes.get(type);
		if (handles != null) {
			return handles;
		}
		return false;
	}

	public void setHandlesEventType(MediaInputEventType type, boolean handles) {
		eventTypes.put(type, handles);
	}

	public void select(Media media) {
		media.setSelected(true);
		for (MediaInputEventListener l : listeners) {
			l.mediaSelected(this, media);
		}
	}

	public void unselect(Media media) {
		media.setSelected(false);
		for (MediaInputEventListener l : listeners) {
			l.mediaUnselected(this, media);
		}
	}

	public void clearSelection() {
		if (MediaManager.getInstance().clearSelection()) {
			for (MediaInputEventListener l : listeners) {
				l.mediasUnselected(this);
			}
		}
	}

	public void activate(MediaNode mediaNode) {
		for (MediaInputEventListener listener : listeners) {
			listener.mediaActivated(this, mediaNode.getMedia());
		}
	}

	public void deactivate(final MediaNode mediaNode) {
		for (MediaInputEventListener listener : listeners) {
			listener.mediaDeactivated(this, mediaNode.getMedia());
		}
	}

	@Override
	public void mousePressed(PInputEvent event) {
		triggeringPopup = false;
		if (event.isPopupTrigger()) {
			triggeringPopup = true;
		}
	}

	@Override
	public void mouseReleased(PInputEvent event) {
		if (event.isPopupTrigger() || triggeringPopup) {
			triggeringPopup = true;
		}
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		if (triggeringPopup) {
			triggeringPopup = false;
			return;
		}
		handleSelection(event);
		handleActivation(event);
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		PNode node = event.getPickedNode();
		Media media = null;
		if (node != previousNode) {
			previousNode = node;
			if (node != null && node instanceof MediaNode) {
				media = ((MediaNode) node).getMedia();
			}
			for (MediaInputEventListener listener : listeners) {
				listener.mediaHovered(this, media);
			}
		}
	}

	private void handleSelection(PInputEvent event) {
		if (handlesEventType(MediaInputEventType.SELECTION)
				&& !event.isHandled()) {

			if (event.getPickedNode() instanceof MediaNode) {
				if (!event.isShiftDown()) {
					clearSelection();
				}
				Media media = ((MediaNode) event.getPickedNode()).getMedia();
				if (media.isSelected()) {
					unselect(media);
				} else {
					select(media);
				}
			} else {
				clearSelection();
			}
		}
	}

	private void handleActivation(PInputEvent event) {
		if (handlesEventType(MediaInputEventType.ACTIVATION)
				&& !event.isHandled()) {
			if (event.getButton() == java.awt.event.MouseEvent.BUTTON1
					&& event.getClickCount() >= 1 && !event.isShiftDown()
					&& !event.isControlDown() && !event.isAltDown()
					&& !event.isMetaDown()
					&& event.getPickedNode() instanceof MediaNode) {

				MediaNode mediaNode = (MediaNode) event.getPickedNode();
				if (mediaNode.getMode() == MediaNode.Mode.THUMBNAIL) {
					activate(mediaNode);
					System.out.println(mediaNode.getMedia());
				} else {
					deactivate(mediaNode);
				}
			}
		}
	}
}
