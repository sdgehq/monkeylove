package fi.oulu.mediabrowserlite.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolox.swing.PScrollPane;
import fi.oulu.mediabrowserlite.MediaBrowser;
import fi.oulu.mediabrowserlite.MediaBrowserSettings;
import fi.oulu.mediabrowserlite.MediaImportListener;
import fi.oulu.mediabrowserlite.MediaImporter;
import fi.oulu.mediabrowserlite.map.CrosshairMarker;
import fi.oulu.mediabrowserlite.map.MapPanel;
import fi.oulu.mediabrowserlite.map.Marker;
import fi.oulu.mediabrowserlite.map.MarkerController;
import fi.oulu.mediabrowserlite.map.ZoomSlider;
import fi.oulu.mediabrowserlite.media.ImageTransferable;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaHelper;
import fi.oulu.mediabrowserlite.media.MediaManager;
import fi.oulu.mediabrowserlite.media.MediaManagerObserver;
import fi.oulu.mediabrowserlite.media.MediaObject;
import fi.oulu.mediabrowserlite.media.RotateListener;
import fi.oulu.mediabrowserlite.media.UndoableMediaEdit;
import fi.oulu.mediabrowserlite.media.Media.Type;
import fi.oulu.mediabrowserlite.media.mediaresources.MediaResourceManager;
import fi.oulu.mediabrowserlite.ui.event.MediaInputEventHandler;
import fi.oulu.mediabrowserlite.ui.event.MediaInputEventListener;

public class MediaBrowserFrame extends JFrame implements ActionListener,
		ChangeListener, ClipboardOwner, MediaInputEventListener, KeyListener,
		MouseListener, FocusListener {

	public enum ApplicationMode {
		BROWSE, IMPORT
	};

	public enum Views {
		GRID_VIEW, MAP_VIEW, GROUPS_VIEW
	};

	public static final String GRID_KEY = "Grid";
	public static final String MAP_KEY = "Map";
	public static final String GROUPS_KEY = "Groups";
	public static final String BROWSE_KEY = "Browse";
	public static final String IMPORT_KEY = "Import";

	public static final String WINDOW_LABEL = MediaBrowser
			.getString("WINDOW_LABEL");
	public static final String FILE_MENU_LABEL = MediaBrowser
			.getString("FILE_MENU_LABEL");
	public static final String IMPORT_ITEM_LABEL = MediaBrowser
			.getString("IMPORT_ITEM_LABEL");
	public static final String EDIT_MENU_LABEL = MediaBrowser
			.getString("EDIT_MENU_LABEL");
	public static final String COPY_ITEM_LABEL = MediaBrowser
			.getString("COPY_ITEM_LABEL");
	public static final String TOOLS_MENU_LABEL = MediaBrowser
			.getString("TOOLS_MENU_LABEL");
	public static final String ROTATE_LEFT_ITEM_LABEL = MediaBrowser
			.getString("ROTATE_LEFT_ITEM_LABEL");
	public static final String ROTATE_RIGHT_ITEM_LABEL = MediaBrowser
			.getString("ROTATE_RIGHT_ITEM_LABEL");
	public static final String SEARCH_LABEL = MediaBrowser
			.getString("SEARCH_LABEL");
	public static final String VIEW_LABEL = MediaBrowser
			.getString("VIEW_LABEL");
	public static final String GRID_LABEL = MediaBrowser
			.getString("GRID_LABEL");
	public static final String MAP_LABEL = MediaBrowser.getString("MAP_LABEL");
	public static final String PREVIEW_LABEL = MediaBrowser
			.getString("PREVIEW_LABEL");
	public static final String LOCATION_LABEL = MediaBrowser
			.getString("LOCATION_LABEL");
	public static final String DESCRIPTION_LABEL = MediaBrowser
			.getString("DESCRIPTION_LABEL");
	public static final String METADATA_LABEL = MediaBrowser
			.getString("METADATA_LABEL");
	public static final String AUTHOR_LABEL = MediaBrowser
			.getString("AUTHOR_LABEL");
	public static final String TITLE_LABEL = MediaBrowser
			.getString("TITLE_LABEL");
	public static final String DATE_LABEL = MediaBrowser
			.getString("DATE_LABEL");
	public static final String STORY_LABEL = MediaBrowser
			.getString("STORY_LABEL");
	public static final String CENTER_MAP_LABEL = MediaBrowser
			.getString("CENTER_MAP_LABEL");
	public static final String COPY_COORDINATE_LABEL = MediaBrowser
			.getString("COPY_COORDINATE_LABEL");
	public static final String PASTE_COORDINATE_LABEL = MediaBrowser
			.getString("PASTE_COORDINATE_LABEL");
	public static final String LOADING_MEDIA_LABEL = MediaBrowser
			.getString("LOADING_MEDIA_LABEL");
	public static final String MULTIPLE_SELECTION_LABEL = MediaBrowser
			.getString("MULTIPLE_SELECTION_LABEL");
	public static final String DELETE_LABEL = MediaBrowser
			.getString("DELETE_LABEL");
	public static final String SELECT_ALL_LABEL = MediaBrowser
			.getString("SELECT_ALL_LABEL");
	public static final String SHOW_ALL_MEDIA_ITEM_LABEL = MediaBrowser
			.getString("SHOW_ALL_MEDIA_ITEM_LABEL");

	private ApplicationMode mode = ApplicationMode.BROWSE;
	private Views view = Views.GRID_VIEW;
	private boolean switchingView = false;
	private boolean usesMap = true;

	private MediaGrid thumbnailGrid;
	private PScrollPane thumbScrollPane;
	private MediaGroupCanvas mediaGroupCanvas;
	private PScrollPane groupScrollPane;
	private MapPanel mapPanel;
	private JTextField searchField;
	private JComboBox viewSelectorBox;
	private JTabbedPane previewPanel;
	private PCanvas previewImagePanel;
	private MediaNode previewNode;
	private MapPanel previewMapPanel;
	private CrosshairMarker crossHair;
	private JLabel warningLabel;
	private JTabbedPane informationPanel;
	private MarkerController markerController;
	private JTextField authorField;
	private JTextField titleField;
	private JTextArea descriptionArea;
	private JScrollPane descriptionSP;
	private JTable metaDataTable;
	private MediaTableModel metaDataTableModel;
	private JButton rotateLeftButton;
	private JButton rotateRightButton;
	private JToggleButton negativeButton;
	private JToggleButton neutralButton;
	private JToggleButton positiveButton;
	private JSlider thumbnailSizeSlider;
	private JLabel numberLabel;
	private JPopupMenu mapContextMenu;
	private JPopupMenu gridContextMenu;
	private JMenuItem centerMapItem;
	private JMenuItem copyCoordinateFromMapItem;
	private JMenuItem copyCoordinateFromMediaItem;
	private JMenuItem copyImageFromMediaItem;
	private JMenuItem pasteCoordinateItem;
	private JMenuItem undoItem;
	private JMenuItem redoItem;
	private JMenuItem copyItem;
	private JMenuItem selectAllItem;
	private JMenuItem deleteItem;
	private JMenuItem contextualDeleteItem;
	private JMenuItem rotateLeftItem;
	private JMenuItem rotateRightItem;
	private JMenuItem showAllItemsItem;
	private Point2D clickPoint = null; // Used to store coordinates on
	// contextual menu selections
	private MediaNode pickedNode = null; // Used to store the media node on
	// contextual menu selections
	private String authorString = ""; // Used to track the changes in the author
	// field
	private String titleString = "";
	private String descriptionString = "";

	private CardLayout cardLayout;
	private JPanel cardPanel;
	private UndoManager undoManager;
	private MediaInputEventHandler mediaGridInputEventHandler;
	private MediaInputEventHandler mediaGroupCanvasInputEventHandler;

	private RotateHandler rotateHandler;
	private Media fullScreenMedia = null;
	private int operationCount = 0;

	private JMenuItem importMenuItem;


	public MediaBrowserFrame() {
		super(WINDOW_LABEL);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		undoManager = new UndoManager();
		createMenu();

		Box mainBox = Box.createHorizontalBox();
		Box leftBox = Box.createVerticalBox();

		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);

		this.thumbnailGrid = new MediaGrid(MediaGrid.Mode.VERTICAL_GRID);
		this.mediaGridInputEventHandler = new MediaInputEventHandler(
				thumbnailGrid);
		mediaGridInputEventHandler.addListener(this);
		thumbnailGrid.addInputEventListener(mediaGridInputEventHandler);
		thumbnailGrid.addMouseListener(this);
		thumbnailGrid.addKeyListener(this);
		addComponentListener(thumbnailGrid);

		this.mediaGroupCanvas = new MediaGroupCanvas();
		this.mediaGroupCanvasInputEventHandler = new MediaInputEventHandler(
				mediaGroupCanvas);
		mediaGroupCanvas
				.addInputEventListener(mediaGroupCanvasInputEventHandler);
		mediaGroupCanvasInputEventHandler.addListener(this);
		mediaGroupCanvas.addMouseListener(this);
		mediaGroupCanvas.addKeyListener(this);
		addComponentListener(mediaGroupCanvas);

		String mapPath = MediaBrowserSettings.getInstance().getMapDirectory();
		String mapName = MediaBrowserSettings.getInstance().getMapName();
		Properties mapProperties = null;
		if (mapPath != null && mapName != null) {
			try {
				File mapPropertiesFile = new File(mapPath + mapName
						+ ".properties");
				System.out.println("Found map properties for: " + mapName);
				mapProperties = new Properties();
				mapProperties.load(new FileInputStream(mapPropertiesFile));
			} catch (Exception e) {
				e.printStackTrace();
				usesMap = false;
			}
		}
		thumbScrollPane = new PScrollPane(thumbnailGrid,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cardPanel.add(thumbScrollPane, GRID_KEY);
		if (usesMap) {
			mapPanel = new MapPanel();
			mapPanel.initialize(mapPath, mapProperties);
			ZoomSlider zoomSlider = new ZoomSlider();
			zoomSlider.setOffset(5, 15);
			mapPanel.setZoomSlider(zoomSlider);
			markerController = mapPanel.getMarkerController();
			markerController.addMediaInputEventListener(this);
			mapPanel.addMouseListener(this);
			mapPanel.addKeyListener(this);
			crossHair = new CrosshairMarker();
			mapPanel.getCamera().addPropertyChangeListener(crossHair);
			mapPanel.getLayer().addChild(crossHair);
			cardPanel.add(mapPanel, MAP_KEY);
		}

		groupScrollPane = new PScrollPane(mediaGroupCanvas,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cardPanel.add(groupScrollPane, GROUPS_KEY);

		leftBox.add(cardPanel);

		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(Box.createRigidArea(new Dimension(3, 3)));
		rotateLeftButton = new JButton(new ImageIcon(getClass().getResource(
				"RotateLeftIcon.png")));
		rotateLeftButton.addActionListener(this);
		bottomBox.add(rotateLeftButton);
		bottomBox.add(Box.createRigidArea(new Dimension(3, 3)));
		rotateRightButton = new JButton(new ImageIcon(getClass().getResource(
				"RotateRightIcon.png")));
		rotateRightButton.addActionListener(this);
		bottomBox.add(rotateRightButton);
		bottomBox.add(Box.createHorizontalGlue());

		negativeButton = new JToggleButton(new ImageIcon(getClass()
				.getResource("Negative.png")));
		negativeButton.addActionListener(this);
		neutralButton = new JToggleButton(new ImageIcon(getClass().getResource(
				"Neutral.png")));
		neutralButton.addActionListener(this);
		positiveButton = new JToggleButton(new ImageIcon(getClass()
				.getResource("Positive.png")));
		positiveButton.addActionListener(this);


		ButtonGroup group = new ButtonGroup();
		group.add(negativeButton);
		group.add(neutralButton);
		group.add(positiveButton);
		bottomBox.add(negativeButton);
		bottomBox.add(Box.createRigidArea(new Dimension(3, 3)));
		bottomBox.add(neutralButton);
		bottomBox.add(Box.createRigidArea(new Dimension(3, 3)));
		bottomBox.add(positiveButton);
		bottomBox.add(Box.createRigidArea(new Dimension(3, 3)));
		bottomBox.add(Box.createHorizontalGlue());

		Box sliderBox = Box.createVerticalBox();
		thumbnailSizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 250, 100);
		thumbnailSizeSlider.setPaintTicks(true);
		thumbnailSizeSlider.setMajorTickSpacing(10);
		thumbnailSizeSlider.addChangeListener(this);
		sliderBox.add(thumbnailSizeSlider);

		Box countBox = Box.createHorizontalBox();
		countBox.add(Box.createHorizontalGlue());
		numberLabel = new JLabel("0");
		numberLabel.setFont(numberLabel.getFont().deriveFont(10f));
		countBox.add(numberLabel);
		sliderBox.add(countBox);

		bottomBox.add(sliderBox);

		leftBox.add(bottomBox);
		mainBox.add(leftBox);

		// Search field
		Box rightBox = Box.createVerticalBox();
		rightBox.add(Box.createRigidArea(new Dimension(3, 3)));
		Box searchBox = Box.createHorizontalBox();
		searchBox.add(Box.createRigidArea(new Dimension(3, 3)));
		searchBox.add(new JLabel(SEARCH_LABEL));
		searchBox.add(Box.createRigidArea(new Dimension(3, 3)));
		searchField = new JTextField("", 20);
		searchField.putClientProperty("JTextField.variant", "search");
		searchField.putClientProperty("JTextField.Search.CancelAction",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						filterMedia(searchField.getText());
					}
				});
		searchBox.add(searchField);
		rightBox.add(searchBox);
		rightBox.add(Box.createRigidArea(new Dimension(3, 3)));

		// View selector
		Box viewBox = Box.createHorizontalBox();
		viewBox.add(Box.createRigidArea(new Dimension(3, 3)));
		viewBox.add(new JLabel(VIEW_LABEL));
		viewBox.add(Box.createRigidArea(new Dimension(3, 3)));
		viewSelectorBox = new JComboBox();
		viewSelectorBox.addItem(GRID_LABEL);
		if (usesMap) {
			viewSelectorBox.addItem(MAP_LABEL);
		}
		viewSelectorBox.addItem(AUTHOR_LABEL);
		viewSelectorBox.addItem(DATE_LABEL);
		viewSelectorBox.addActionListener(this);
		viewBox.add(viewSelectorBox);
		rightBox.add(viewBox);

		// Preview panes
		previewPanel = new JTabbedPane();
		// previewImagePanel = new JPanel();
		previewImagePanel = new PCanvas();
		previewImagePanel.setPanEventHandler(null);
		previewImagePanel.setZoomEventHandler(null);
		previewNode = new MediaNode();
		previewImagePanel.getLayer().addChild(previewNode);
		previewNode.setMode(MediaNode.Mode.CONTENT);
		previewNode.setPickable(false);
		previewNode.setBorder(0, 0);
		previewPanel.add(PREVIEW_LABEL, previewImagePanel);

		if (usesMap) {
			previewMapPanel = new MapPanel();
			previewMapPanel.initialize(mapPath, mapProperties);
			previewMapPanel.setZoomEventHandler(null);
			previewMapPanel.setCameraController(null);
			previewMapPanel.setMarkerController(null);
			previewPanel.add(LOCATION_LABEL, previewMapPanel);
		}

		previewPanel.setPreferredSize(new Dimension(252, 289));
		rightBox.add(previewPanel);
		rightBox.add(Box.createRigidArea(new Dimension(3, 3)));

		// Warning label
		Box warningBox = Box.createHorizontalBox();
		warningLabel = new JLabel(" ");
		warningLabel.setForeground(Color.red);
		warningBox.add(warningLabel);
		rightBox.add(warningBox);

		// Information and metadata
		informationPanel = new JTabbedPane();
		Box informationBox = Box.createVerticalBox();
		Box authorBox = Box.createHorizontalBox();
		authorBox.add(Box.createRigidArea(new Dimension(3, 3)));
		final JLabel authorLabel = new JLabel(AUTHOR_LABEL);
		authorBox.add(authorLabel);
		authorBox.add(Box.createRigidArea(new Dimension(3, 3)));
		authorField = new JTextField("", 15);
		authorBox.add(authorField);
		informationBox.add(authorBox);
		informationBox.add(Box.createRigidArea(new Dimension(3, 3)));

		Box titleBox = Box.createHorizontalBox();
		titleBox.add(Box.createRigidArea(new Dimension(3, 3)));
		final JLabel titleLabel = new JLabel(TITLE_LABEL);
		titleBox.add(titleLabel);
		titleBox.add(Box.createRigidArea(new Dimension(3, 3)));
		titleField = new JTextField();
		titleBox.add(titleField);
		informationBox.add(titleBox);
		informationBox.add(Box.createRigidArea(new Dimension(3, 3)));

		Box labelBox = Box.createHorizontalBox();
		labelBox.add(Box.createHorizontalGlue());
		labelBox.add(new JLabel(DESCRIPTION_LABEL));
		labelBox.add(Box.createHorizontalGlue());
		informationBox.add(labelBox);

		descriptionArea = new JTextArea();
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionSP = new JScrollPane(descriptionArea);
		informationBox.add(Box.createRigidArea(new Dimension(3, 3)));
		informationBox.add(descriptionSP);

		informationPanel.add(DESCRIPTION_LABEL, informationBox);
		metaDataTableModel = new MediaTableModel();
		metaDataTable = new JTable(metaDataTableModel);

		JScrollPane scrollPane = new JScrollPane(metaDataTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		informationPanel.add(METADATA_LABEL, scrollPane);
		rightBox.add(informationPanel);

		rightBox.add(Box.createRigidArea(new Dimension(3, 3)));
		rightBox.add(Box.createVerticalGlue());

		mainBox.add(rightBox);

		// Prepare contextual menus
		mapContextMenu = new JPopupMenu();
		centerMapItem = new JMenuItem(CENTER_MAP_LABEL);
		centerMapItem.addActionListener(this);
		mapContextMenu.add(centerMapItem);
		copyCoordinateFromMapItem = new JMenuItem(COPY_COORDINATE_LABEL);
		copyCoordinateFromMapItem.addActionListener(this);
		mapContextMenu.add(copyCoordinateFromMapItem);

		gridContextMenu = new JPopupMenu();
		copyImageFromMediaItem = new JMenuItem(COPY_ITEM_LABEL);
		copyImageFromMediaItem.addActionListener(this);
		gridContextMenu.add(copyImageFromMediaItem);
		copyCoordinateFromMediaItem = new JMenuItem(COPY_COORDINATE_LABEL);
		copyCoordinateFromMediaItem.addActionListener(this);
		gridContextMenu.add(copyCoordinateFromMediaItem);
		pasteCoordinateItem = new JMenuItem(PASTE_COORDINATE_LABEL);
		pasteCoordinateItem.addActionListener(this);
		gridContextMenu.add(pasteCoordinateItem);
		gridContextMenu.add(new JSeparator());
		contextualDeleteItem = new JMenuItem(DELETE_LABEL);
		contextualDeleteItem.addActionListener(this);
		gridContextMenu.add(contextualDeleteItem);

		getContentPane().add(mainBox);
		setPreferredSize(new Dimension(1024, 768));
		pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				searchField.setPreferredSize(new Dimension(200, searchField
						.getPreferredSize().height));
				searchField.setMaximumSize(new Dimension(500, searchField
						.getPreferredSize().height));
				searchField.invalidate();
				authorField.setPreferredSize(new Dimension(200, authorField
						.getPreferredSize().height));
				authorField.setMaximumSize(new Dimension(500, authorField
						.getPreferredSize().height));
				authorField.invalidate();
				titleField.setPreferredSize(new Dimension(200, titleField
						.getPreferredSize().height));
				titleField.setMaximumSize(new Dimension(500, titleField
						.getPreferredSize().height));
				titleField.invalidate();

				descriptionSP.setPreferredSize(new Dimension(200, descriptionSP
						.getPreferredSize().height));
				descriptionArea.invalidate();

				if (usesMap) {
					previewMapPanel.zoomToLevel(2);

					// Needed to properly initialise the map camera for the view
					// bounds
					mapPanel.getCamera().setViewBounds(
							new PBounds(0, 0, 800, 800));
					mapPanel.getCameraController().validateZoomLevel();
					mapPanel.zoomToLevel(0);
					previewPanel.setSelectedComponent(previewMapPanel);
				}
				metaDataTable.setPreferredScrollableViewportSize(new Dimension(
						200, 200));

				previewNode.setBounds(previewImagePanel.getCamera()
						.getViewBounds());

				int labelWidth = Math.max(authorLabel.getPreferredSize().width,
						titleLabel.getPreferredSize().width);
				Dimension labelSize = new Dimension(labelWidth, authorLabel
						.getPreferredSize().height);
				authorLabel.setMinimumSize(labelSize);
				authorLabel.setPreferredSize(labelSize);
				titleLabel.setMinimumSize(labelSize);
				titleLabel.setPreferredSize(labelSize);

				mediaGroupCanvas.reGroupMediaNodes();
				validate();
				setUndo();
				checkUIState();
				setVisible(true);
			}

		});

		MediaManager.getInstance().addObserver(new MediaManagerObserver() {
			public void mediaAdded(Media media) {
				thumbnailGrid.addMedia(media);
				if (usesMap) {
					mapPanel.addMedia(media);
				}
				mediaGroupCanvas.addMedia(media);
				countVisibleMedia();
			}

			public void mediaRemoved(Media media) {
				thumbnailGrid.removeMedia(media);
				if (usesMap) {
					mapPanel.removeMedia(media);
				}
				mediaGroupCanvas.removeMedia(media);
				countVisibleMedia();
			}

			public void startedLoading() {
			}

			public void finishedLoading() {
			}

		});

		authorField.addFocusListener(this);
		titleField.addFocusListener(this);
		descriptionArea.addFocusListener(this);

		searchField.addKeyListener(new KeyAdapter() {
			boolean ok = false;

			public void keyReleased(KeyEvent e) {
				String s = searchField.getText();
				if (s.length() > 0) {
					ok = true;
					filterMedia(searchField.getText());
				} else if (s.length() == 0 && ok) {
					ok = false;
					filterMedia(searchField.getText());
				}
			}
		});

		this.rotateHandler = new RotateHandler();
		MediaResourceManager.getInstance().addRotateListener(rotateHandler);

		// TODO HACK
		if (MediaBrowser.hasMediasToUpload()) {
			setMode(ApplicationMode.IMPORT);
		}
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu(FILE_MENU_LABEL);
		menuBar.add(fileMenu);
		this.importMenuItem = new JMenuItem(IMPORT_ITEM_LABEL);
		fileMenu.add(importMenuItem);
		importMenuItem.addActionListener(this);
		JMenu editMenu = new JMenu(EDIT_MENU_LABEL);
		menuBar.add(editMenu);
		undoItem = new JMenuItem(undoManager.getUndoPresentationName());
		undoItem.addActionListener(this);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		editMenu.add(undoItem);
		redoItem = new JMenuItem(undoManager.getRedoPresentationName());
		redoItem.addActionListener(this);
		redoItem
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
						InputEvent.SHIFT_MASK
								| Toolkit.getDefaultToolkit()
										.getMenuShortcutKeyMask()));

		editMenu.add(redoItem);
		editMenu.add(new JSeparator());
		copyItem = new JMenuItem(COPY_ITEM_LABEL);
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		copyItem.addActionListener(this);
		editMenu.add(copyItem);
		editMenu.add(new JSeparator());
		deleteItem = new JMenuItem(DELETE_LABEL);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		deleteItem.addActionListener(this);
		editMenu.add(deleteItem);
		selectAllItem = new JMenuItem(SELECT_ALL_LABEL);
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		selectAllItem.addActionListener(this);
		editMenu.add(selectAllItem);

		JMenu toolsMenu = new JMenu(TOOLS_MENU_LABEL);
		menuBar.add(toolsMenu);
		rotateLeftItem = new JMenuItem(ROTATE_LEFT_ITEM_LABEL);
		rotateLeftItem.addActionListener(this);
		rotateLeftItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		toolsMenu.add(rotateLeftItem);
		rotateRightItem = new JMenuItem(ROTATE_RIGHT_ITEM_LABEL);
		rotateRightItem.addActionListener(this);
		rotateRightItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		toolsMenu.add(rotateRightItem);
		showAllItemsItem = new JMenuItem(SHOW_ALL_MEDIA_ITEM_LABEL);
		showAllItemsItem.addActionListener(this);
		toolsMenu.add(showAllItemsItem);

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == viewSelectorBox) {
			Object selected = viewSelectorBox.getSelectedItem();
			if (selected == GRID_LABEL) {
				setViewMode(Views.GRID_VIEW);
			}
			if (selected == MAP_LABEL) {
				setViewMode(Views.MAP_VIEW);
			}
			if (selected == AUTHOR_LABEL) {
				mediaGroupCanvas.setGrouping(MediaGroupCanvas.Grouping.AUTHOR);
				setViewMode(Views.GROUPS_VIEW);
			}
			if (selected == DATE_LABEL) {
				mediaGroupCanvas.setGrouping(MediaGroupCanvas.Grouping.DATE);
				setViewMode(Views.GROUPS_VIEW);
			}
		}
		if (e.getSource() == centerMapItem) {
			// System.out.println( "Center map" );
			double latitude = clickPoint.getY();
			double longitude = clickPoint.getX();
			System.out.println("Lat: " + latitude + " Long: " + longitude);
			mapPanel.centerMapOnCoordinates(latitude, longitude, 500l);
		}
		if (e.getSource() == copyCoordinateFromMapItem) {
			// System.out.println( "Copy coordinate" );
			Clipboard clipBoard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			// Format is: "Longitude:Latitude" (x,y)
			StringSelection contents = new StringSelection(clickPoint.getX()
					+ ":" + clickPoint.getY());
			clipBoard.setContents(contents, contents);

			System.out.println("copy coordinates from map: " + clickPoint);

		}
		if (e.getSource() == copyCoordinateFromMediaItem) {
			if (pickedNode != null) {
				Media media = pickedNode.getMedia();
				Clipboard clipBoard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				StringSelection contents = new StringSelection(media
						.getLongitude()
						+ ":" + media.getLatitude());
				clipBoard.setContents(contents, contents);

				System.out.println("copy coordinates from media: "
						+ media.getLongitude() + ", " + media.getLatitude());
			}
		}
		if (e.getSource() == pasteCoordinateItem) {
			Point2D coordinates = getCoordinateFromClipboard();

			System.out.println("pasting coordinates: " + coordinates);

			if (pickedNode != null && coordinates != null) {
				List<Media> selectedMedias = MediaManager.getInstance()
						.getSelectedMedias();
				Media media = pickedNode.getMedia();
				UndoableEdit edit = null;
				if (selectedMedias.contains(media)) {
					// Set coordinates for all selected media
					CompoundEdit compound = new CompoundEdit();
					for (Media aMedia : selectedMedias) {
						// aMedia.setLatitude(coordinates.getY());
						// aMedia.setLongitude(coordinates.getX());

						edit = UndoableMediaEdit.setPosition(aMedia,
								coordinates.getY(), coordinates.getX());
						compound.addEdit(edit);
					}
					compound.end();
					undoManager.addEdit(compound);
				} else {
					// Only set for the picked media
					// media.setLatitude(coordinates.getY());
					// media.setLongitude(coordinates.getX());
					edit = UndoableMediaEdit.setPosition(media, coordinates
							.getY(), coordinates.getX());
					undoManager.addEdit(edit);
				}
				setUndo();
				mapPanel.updateMarkerPositions();
				previewMapPanel.updateMarkerPositions();
			}
		}
		if (e.getSource() == selectAllItem) {
			MediaManager.getInstance().selectAll();
			checkUIState();
		}
		if (e.getSource() == showAllItemsItem) {
			showAllMedia();
		}
		if (e.getSource() == undoItem) {
			try {
				undoManager.undo();
			} catch (CannotUndoException cue) {
				cue.printStackTrace();

			}
			setUndo();
			// If we are viewing a media object that has been edited, re-set it
			if (previewNode.getMedia() != null) {
				System.out.println("Should update view on undo");
				setMedia(previewNode.getMedia());
			}
			thumbnailGrid.organizeMediaNodes();
			mediaGroupCanvas.organizeMediaNodes();
			mapPanel.updateMarkerPositions();
			countVisibleMedia();
		}
		if (e.getSource() == redoItem) {
			try {
				undoManager.redo();
			} catch (CannotRedoException cre) {
				cre.printStackTrace();
			}
			setUndo();
			// If we are viewing a media object that has been edited, re-set it
			if (previewNode.getMedia() != null) {
				System.out.println("Should update view on redo");
				setMedia(previewNode.getMedia());

			}
		}
		if (e.getSource() == copyItem) {
			List<Media> selected = MediaManager.getInstance()
					.getSelectedMedias();
			if (selected.size() == 1) {
				Clipboard clipBoard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Media media = selected.get(0);
				Image image = MediaResourceManager.getInstance()
						.getContentImage(media);
				ImageTransferable trans = new ImageTransferable(image);
				clipBoard.setContents(trans, this);
			}
		}
		if (e.getSource() == copyImageFromMediaItem) {
			if (pickedNode != null) {
				Media media = pickedNode.getMedia();
				Image image = MediaResourceManager.getInstance()
						.getContentImage(media);
				ImageTransferable trans = new ImageTransferable(image);
				Clipboard clipBoard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipBoard.setContents(trans, this);
			}
		}
		if (e.getSource() == rotateLeftItem
				|| e.getSource() == rotateLeftButton) {
			rotateHandler.rotate(
					MediaManager.getInstance().getSelectedMedias(),
					MediaHelper.ROTATE_270);
		}
		if (e.getSource() == rotateRightItem
				|| e.getSource() == rotateRightButton) {
			rotateHandler.rotate(
					MediaManager.getInstance().getSelectedMedias(),
					MediaHelper.ROTATE_90);
		}
		if (e.getSource() == deleteItem) {
			removeSelectedMedias();
		}
		if (e.getSource() == contextualDeleteItem) {
			if (pickedNode != null) {
				List<Media> selectedMedias = MediaManager.getInstance()
						.getSelectedMedias();
				Media media = pickedNode.getMedia();
				if (selectedMedias.contains(media)) {
					removeSelectedMedias();
				} else {
					MediaManager.getInstance().clearSelection();
					media.setSelected(true);
					removeSelectedMedias();
				}
			}
		}

		if (e.getSource() == positiveButton) {
			setTypeForSelectedMedias(Type.POSITIVE);
		}
		if (e.getSource() == neutralButton) {
			setTypeForSelectedMedias(Type.NEUTRAL);
		}
		if (e.getSource() == negativeButton) {
			setTypeForSelectedMedias(Type.NEGATIVE);
		}

		if (e.getSource() == importMenuItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					return MediaImporter.isImportableFile(f);
				}

				@Override
				public String getDescription() {
					return "jpeg";
				}
			});

			int returnValue = fileChooser
					.showOpenDialog(MediaBrowserFrame.this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File[] files = fileChooser.getSelectedFiles();
				System.out.println(files.length + " files selected for import");
				MediaImporter.importFiles(files, null );
				setMode(ApplicationMode.IMPORT);
			}
		}

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_HOME) {
			mapPanel.centerMap();
		}
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
				|| e.getKeyCode() == KeyEvent.VK_DELETE) {
			removeSelectedMedias();
		}
		if (e.getKeyCode() == KeyEvent.VK_M
				&& (e.isMetaDown() || e.isControlDown())) {
			resetMap();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getSource() == mapPanel && e.isPopupTrigger()) {
			mapContextMenu.show(mapPanel, e.getX(), e.getY());
			clickPoint = mapPanel.screenPointToGeoPoint(e.getX(), e.getY());
			crossHair.setPosition(mapPanel.geoPointToScreenPoint(clickPoint));
		}
		if (e.getSource() == thumbnailGrid && e.isPopupTrigger()) {
			showGridContextMenu(thumbnailGrid, e.getPoint());
		}
		if (e.getSource() == mediaGroupCanvas && e.isPopupTrigger()) {
			showGridContextMenu(mediaGroupCanvas, e.getPoint());
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == mapPanel && e.isPopupTrigger()) {
			mapContextMenu.show(mapPanel, e.getX(), e.getY());
			clickPoint = mapPanel.screenPointToGeoPoint(e.getX(), e.getY());
			crossHair.setPosition(mapPanel.geoPointToScreenPoint(clickPoint));
		}
		if (e.getSource() == thumbnailGrid && e.isPopupTrigger()) {
			showGridContextMenu(thumbnailGrid, e.getPoint());
		}
		if (e.getSource() == mediaGroupCanvas && e.isPopupTrigger()) {
			showGridContextMenu(mediaGroupCanvas, e.getPoint());
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void setViewMode(Views mode) {
		switchingView = true;
		if (mode == Views.GRID_VIEW) {
			cardLayout.show(cardPanel, GRID_KEY);
			if (usesMap) {
				previewPanel.setSelectedComponent(previewMapPanel);
			}
			thumbnailSizeSlider.setValue((int) thumbnailGrid
					.getThumbnailWidth());
			view = Views.GRID_VIEW;
		}
		if (mode == Views.MAP_VIEW && usesMap) {
			cardLayout.show(cardPanel, MAP_KEY);
			previewPanel.setSelectedComponent(previewImagePanel);
			thumbnailSizeSlider.setValue((int) mapPanel.getThumbWidth());
			view = Views.MAP_VIEW;
		}
		if (mode == Views.GROUPS_VIEW) {
			cardLayout.show(cardPanel, GROUPS_KEY);
			if (usesMap) {
				previewPanel.setSelectedComponent(previewMapPanel);
			}
			thumbnailSizeSlider.setValue((int) mediaGroupCanvas
					.getThumbnailWidth());
			view = Views.GROUPS_VIEW;
		}
		switchingView = false;
	}

	public void showGridContextMenu(Component parent, Point point) {
		pickedNode = null;
		if (parent == thumbnailGrid || parent == mediaGroupCanvas) {
			PCamera camera = ((MediaCanvas) parent).getCamera();
			PPickPath pick = camera.pick(point.getX(), point.getY(), 1.0);
			PNode node = pick.getPickedNode();
			if (node instanceof MediaNode) {
				pickedNode = (MediaNode) node;
			}
		}
		copyCoordinateFromMediaItem.setEnabled(false);
		pasteCoordinateItem.setEnabled(false);
		copyImageFromMediaItem.setEnabled(false);
		contextualDeleteItem.setEnabled(false);
		if (pickedNode != null) {
			copyImageFromMediaItem.setEnabled(true);
			copyCoordinateFromMediaItem.setEnabled(true);
			contextualDeleteItem.setEnabled(true);
			if (getCoordinateFromClipboard() != null) {
				pasteCoordinateItem.setEnabled(true);
			}
		}
		gridContextMenu.show(parent, (int) point.getX(), (int) point.getY());
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == thumbnailSizeSlider && !switchingView) {
			int size = thumbnailSizeSlider.getValue();
			if (view == Views.GRID_VIEW) {
				thumbnailGrid.setThumbnailSize(size, size);
				thumbnailGrid.organizeMediaNodes();
			}
			if (view == Views.MAP_VIEW) {
				mapPanel.setThumbSize(size, size);
			}
			if (view == Views.GROUPS_VIEW) {
				mediaGroupCanvas.setThumbnailSize(size, size);
				mediaGroupCanvas.organizeMediaNodes();
			}
		}
	}

	private synchronized void countVisibleMedia() {
		int visible = 0;
		int total = 0;
		List<Media> medias = MediaManager.getInstance().getMedias();
		total = medias.size();
		Media firstVisible = null;
		for (Media media : medias) {
			if (media.isVisible()) {
				visible++;
				if (firstVisible == null) {
					firstVisible = media;
				}
			}
		}
		String visibleString = visible + "/" + total;
		numberLabel.setText(visibleString);

		if (firstVisible != null) {
			// Ugly hacks to try to ensure visibility of filtered results
			PBounds bounds = thumbnailGrid.getMediaNode(firstVisible)
					.getBounds();
			PBounds viewBounds = thumbnailGrid.getCamera().getViewBounds();
			if (!viewBounds.contains(bounds)) {
				thumbScrollPane.getVerticalScrollBar().setValue(0);
			}
			bounds = mediaGroupCanvas.getMediaNode(firstVisible).getBounds();
			viewBounds = mediaGroupCanvas.getCamera().getViewBounds();
			if (!viewBounds.contains(bounds)) {
				groupScrollPane.getVerticalScrollBar().setValue(0);
			}
		}
	}

	public void focusGained(FocusEvent e) {
		if (e.getSource() == authorField) {
			authorString = authorField.getText().trim();
		} else if (e.getSource() == titleField) {
			titleString = titleField.getText().trim();
		} else if (e.getSource() == descriptionArea) {
			descriptionString = descriptionArea.getText().trim();
		}
	}

	public void focusLost(FocusEvent e) {
		boolean needsGrouping = false;
		UndoableEdit edit = null;
		CompoundEdit compound = null;
		if (MediaManager.getInstance().getSelectedMedias().size() > 1) {
			// Prepare for a multiple edit
			compound = new CompoundEdit();
		}

		for (Media media : MediaManager.getInstance().getSelectedMedias()) {

			if (e.getSource() == authorField) {
				String authorText = authorField.getText().trim();

				if (!authorString.equals(authorText)
						&& !authorText.equals(media.getAuthor())) {

					edit = UndoableMediaEdit.setAuthor(media, authorText);
					if (compound != null) {
						compound.addEdit(edit);
					}
					needsGrouping = true;
				}

			} else if (e.getSource() == titleField) {
				String titleText = titleField.getText().trim();

				if (!titleString.equals(titleText)
						&& !titleText.equals(media.getTitle())) {

					edit = UndoableMediaEdit.setTitle(media, titleText);
					if (compound != null) {
						compound.addEdit(edit);
					}
				}
			} else if (e.getSource() == descriptionArea) {
				String descriptionText = descriptionArea.getText().trim();

				if (!descriptionString.equals(descriptionText)
						&& !descriptionText.equals(media.getDescription())) {

					edit = UndoableMediaEdit.setDescription(media,
							descriptionText);
					if (compound != null) {
						compound.addEdit(edit);
					}
				}
			}
		}
		if (compound != null && edit != null) {
			// Needs to have at least one actual edit
			compound.end();
			edit = compound;
		}
		if (edit != null) {
			undoManager.addEdit(edit);
			setUndo();
		}

		if (needsGrouping) {
			// mediaGroupCanvas.reGroupMediaNodes();
		}
	}

	public void filterMedia(String text) {
		text = text.toLowerCase();

		// Try to see if the search term can be parsed as date
		DateFormat format = DateFormat.getDateInstance();
		Date date = null;
		try {
			date = format.parse(text);
		} catch (ParseException pe) {
			// System.out.println( "Can't parse date: " + text );
		}
		if (date == null) {
			// Try again, with a placeholder year
			try {
				date = format.parse(text + "1");
			} catch (ParseException pe) {
				// System.out.println( "Can't parse date: " + text );
			}
		}
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			System.out.println("Searching by date: " + format.format(date));
		}
		Media.Type searchType = null;
		if (text.equals(":)") || text.equals(":-)")) {
			// Look for positive
			searchType = Media.Type.POSITIVE;
		}
		if (text.equalsIgnoreCase(":I") || text.equalsIgnoreCase(":-I")
				|| text.equals(":|") || text.equals(":-|")) {
			// Look for neutral
			searchType = Media.Type.NEUTRAL;
		}
		if (text.equals(":(") || text.equals(":-(")) {
			// Look for negative
			searchType = Media.Type.NEGATIVE;
		}

		List<Media> medias = MediaManager.getInstance().getMedias();
		for (Media media : medias) {
			boolean visible = false;
			if (text.length() == 0) {
				visible = true;
			} else {
				if (media.getAuthor().toLowerCase().contains(text)
						|| media.getTitle().toLowerCase().contains(text)
						|| media.getDescription().toLowerCase().contains(text)) {
					visible = true;
					System.out.println("Matched by text; " + media.getId());
				}
				if (MediaGroupCanvas.UNKNOWN_AUTHOR.toLowerCase().startsWith(
						text.toLowerCase())) {
					if (media.getAuthor() == null
							|| media.getAuthor().length() < 1) {
						visible = true;
						System.out.println("Matched by unknown author");
					}
				}
				if (!visible) {
					if (date != null) {
						Calendar mediaCalendar = Calendar.getInstance();
						mediaCalendar.setTime(media.getDateCreated());
						if (datesMatch(calendar, mediaCalendar)) {
							visible = true;
							System.out.println("Matched by date; "
									+ media.getId());
						}
					}
				}
				if (!visible) {
					if (media.getId().toLowerCase().startsWith(text)) {
						visible = true;
						System.out.println("Matched by media id: "
								+ media.getId());
					}
				}
				if (!visible) {
					if (media.getMediaType().toLowerCase().contains(text)) {
						visible = true;
						System.out.println("Matched by media type: "
								+ media.getId());
					}
				}
				if (!visible) {
					if (text.equals("!")
							&& media.getLatitude() == Media.NOT_SPECIFIED_LATITUDE) {
						visible = true;
					}
				}
				if (!visible) {
					if (text.equals("*")
							&& media.getMediaType().equals(
									MediaObject.MEDIA_TYPE_DEFAULT)) {
						visible = true;
					}
				}
				if (!visible) {
					for (String key : media.getMetaKeys()) {
						Object metaValue = media.getMeta(key);
						if (metaValue instanceof String
								&& ((String) metaValue).toLowerCase().contains(
										text)) {
							visible = true;
							System.out.println("Matched by meta: " + key + ": "
									+ media.getId());
							break;
						}
					}
				}
				if (!visible && searchType != null) {
					if (media.getType() == searchType) {
						visible = true;
					}
				}
			}
			media.setVisible(visible);
		}
		thumbnailGrid.organizeMediaNodes();
		mediaGroupCanvas.organizeMediaNodes();
		MediaNode viewedNode = thumbnailGrid.getViewedMediaNode();

		if (viewedNode != null && !viewedNode.getVisible()) {
			mediaDeactivated(this, viewedNode.getMedia());
			/*
			 * activationHandler.unZoomMediaNode( viewedNode );
			 * thumbnailGrid.setViewedMediaNode( null );
			 * markerController.returnFromFullScreen();
			 * previewMapPanel.removeMedia( viewedNode.getMedia() );
			 */
		}
		countVisibleMedia();
	}

	private void checkUIState() {
		List<Media> selected = MediaManager.getInstance().getSelectedMedias();
		boolean hasSelection = (selected.size() > 0);
		if (selected.size() == 1) {
			copyItem.setEnabled(true);
		} else {
			copyItem.setEnabled(false);
		}
		boolean canRotate = false;
		if (selected.size() > 0) {
			canRotate = true;
		}
		for (Media media : selected) {
			if (!media.isMediaFileAvailable()) {
				canRotate = false;
			}
		}
		rotateLeftButton.setEnabled(canRotate);
		rotateLeftItem.setEnabled(canRotate);
		rotateRightButton.setEnabled(canRotate);
		rotateRightItem.setEnabled(canRotate);
		deleteItem.setEnabled(hasSelection);
	}

	private boolean datesMatch(Calendar searchCal, Calendar mediaCal) {
		boolean match = false;

		if (searchCal.get(Calendar.YEAR) < 1900) {
			match = (searchCal.get(Calendar.DAY_OF_MONTH) == mediaCal
					.get(Calendar.DAY_OF_MONTH) && searchCal
					.get(Calendar.MONTH) == mediaCal.get(Calendar.MONTH));
		} else {
			match = (searchCal.get(Calendar.DAY_OF_MONTH) == mediaCal
					.get(Calendar.DAY_OF_MONTH)
					&& searchCal.get(Calendar.MONTH) == mediaCal
							.get(Calendar.MONTH) && searchCal
					.get(Calendar.YEAR) == mediaCal.get(Calendar.YEAR));
		}
		return match;
	}

	public void hideExistingMedia() {
		List<Media> medias = MediaManager.getInstance().getMedias();
		for (Media media : medias) {
			media.setVisible(false);
		}
	}

	public void showAllMedia() {
		List<Media> medias = MediaManager.getInstance().getMedias();
		for (Media media : medias) {
			media.setVisible(true);
		}
		searchField.setText("");
		thumbnailGrid.organizeMediaNodes();
		mediaGroupCanvas.organizeMediaNodes();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}

	public void removeSelectedMedias() {
		System.out.println("Removing media");
		List<Media> selectedMedias = MediaManager.getInstance()
				.getSelectedMedias();
		UndoableEdit edit = null;
		CompoundEdit compound = new CompoundEdit();
		for (Media aMedia : selectedMedias) {
			if (thumbnailGrid.getViewedMediaNode() != null) {
				if (thumbnailGrid.getViewedMediaNode().getMedia() == aMedia) {
					mediaDeactivated(this, aMedia);
				}
			}
			edit = UndoableMediaEdit.delete(aMedia);
			compound.addEdit(edit);

		}
		compound.end();
		undoManager.addEdit(compound);
		setUndo();
		thumbnailGrid.organizeMediaNodes();
		mediaGroupCanvas.organizeMediaNodes();
		countVisibleMedia();
	}

	private void resetMap() {
		mapPanel.resetMap();
		List<Media> medias = MediaManager.getInstance().getMedias();
		for (Media media : medias) {
			mapPanel.addMedia(media);
		}
	}

	private Point2D getCoordinateFromClipboard() {
		Point2D point = null;
		Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipBoard.getContents(this);
		System.out.println("Getting coordinates from clipboard: "
				+ contents.toString());
		if (contents != null) {
			try {
				String string = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
				if (string != null) {
					StringTokenizer tokenizer = new StringTokenizer(string, ":");
					if (tokenizer.countTokens() == 2) {
						double longitude = Double.parseDouble(tokenizer
								.nextToken());
						double latitude = Double.parseDouble(tokenizer
								.nextToken());
						point = new Point2D.Double(longitude, latitude);
						System.out.println("Parsed: " + point.toString());
					}
				}
			} catch (NumberFormatException nfe) {
				// Can't parse coordinates, fail silently
			} catch (UnsupportedFlavorException ufe) {
				// Wrong dataflavor, also fail silently
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return point;
	}

	private void setTypeForSelectedMedias(Type type) {
		CompoundEdit compound = new CompoundEdit();
		UndoableEdit edit;
		for (Media media : MediaManager.getInstance().getSelectedMedias()) {
			// media.setType(type);
			edit = UndoableMediaEdit.setType(media, type);
			compound.addEdit(edit);
		}
		compound.end();
		undoManager.addEdit(compound);
		setUndo();
	}

	private void setMedia(Media media) {
		if (media != null) {
			authorField.setText(media.getAuthor());
			titleField.setText(media.getTitle());
			descriptionArea.setText(media.getDescription());
	
			if (media.getType() == Type.POSITIVE) {
				positiveButton.setSelected(true);
			} else if (media.getType() == Type.NEUTRAL) {
				neutralButton.setSelected(true);
			} else if (media.getType() == Type.NEGATIVE) {
				negativeButton.setSelected(true);
			}
			if (usesMap) {
				previewMapPanel.removeAllMedia();
				previewMapPanel.addMedia(media);
				previewMapPanel.centerMapOnCoordinates(media.getLatitude(),
						media.getLongitude());
				previewMapPanel.getMarker(media).setVisible(true);
			}
			previewNode
					.setBounds(previewImagePanel.getCamera().getViewBounds());
			previewNode.setMedia(media);
			previewNode.setVisible(true);
			metaDataTableModel.setMedia(media);
		} else {
			authorField.setText("");
			titleField.setText("");
			descriptionArea.setText("");
			positiveButton.setSelected(false);
			neutralButton.setSelected(false);
			negativeButton.setSelected(false);
			if (usesMap) {
				previewMapPanel.removeAllMedia();
				previewMapPanel.repaint();
			}
			previewNode.setMedia(null);
			previewNode.repaint();
			metaDataTableModel.setMedia(null);
		}
		authorString = authorField.getText().trim();
		titleString = titleField.getText().trim();
		descriptionString = descriptionArea.getText().trim();
		List<Media> selectedMedias = MediaManager.getInstance()
				.getSelectedMedias();
		if (selectedMedias.size() > 1) {
			warningLabel.setText(MULTIPLE_SELECTION_LABEL);
		} else {
			warningLabel.setText(" ");
		}
	}

	public void setMode(ApplicationMode mode) {
		this.mode = mode;
		if (mode == ApplicationMode.IMPORT ) {
			hideExistingMedia();
		} else {
			showAllMedia();
		}
	}

	private void setUndo() {
		undoItem.setText(undoManager.getUndoPresentationName());
		redoItem.setText(undoManager.getRedoPresentationName());
		undoItem.setEnabled(undoManager.canUndo());
		redoItem.setEnabled(undoManager.canRedo());
	}

	public void mediaSelected(Object source, Media media) {
		if (source == mediaGridInputEventHandler) {
			setMedia(media);
		}
		checkUIState();
	}

	public void mediaUnselected(Object source, Media media) {
		if (source == mediaGridInputEventHandler) {
			if (usesMap) {
				previewMapPanel.removeMedia(media);
			}
			List<Media> selectedMedias = MediaManager.getInstance()
					.getSelectedMedias();
			if (selectedMedias.size() == 1) {
				setMedia(selectedMedias.get(0));
			} else {
				setMedia(null);
			}
			// if (selectedNodes.isEmpty()) {
			// setMedia(null);
			// } else {
			// setMedia(selectedNodes.get(selectedNodes.size() - 1).getMedia());
			// }
		}
		if (media == fullScreenMedia) {
			// Can't deselect full screen media
			media.setSelected(true);
		}
		checkUIState();
	}

	public void mediasUnselected(Object source) {
		setMedia(null);
		checkUIState();
	}

	public void mediaActivated(Object source, Media media) {
		if (fullScreenMedia != media && fullScreenMedia != null) {
			mediaDeactivated(this, fullScreenMedia);
		}
		thumbnailGrid.showFullScreen(media);
		mediaGroupCanvas.showFullScreen(media);
		if (usesMap) {
			Marker marker = mapPanel.getMarker(media);
			markerController.showFullScreen(marker);
		}
		if (view == Views.MAP_VIEW) {
			MediaManager.getInstance().clearSelection();
			media.setSelected(true);
		}
		fullScreenMedia = media;
		setMedia(media);
		checkUIState();
	}

	public void mediaDeactivated(Object source, Media media) {
		thumbnailGrid.returnFromFullScreen(media);
		mediaGroupCanvas.returnFromFullScreen(media);
		if (usesMap) {
			markerController.returnFromFullScreen();
		}
		if (view == Views.MAP_VIEW) {
			media.setSelected(false);
		}
		setMedia(null);
		checkUIState();
		fullScreenMedia = null;
	}

	public void mediaHovered(Object source, Media media) {
		if (thumbnailGrid.getViewedMediaNode() == null) {
			List<Media> selectedMedias = MediaManager.getInstance()
					.getSelectedMedias();
			if (media == null && selectedMedias.size() == 1) {
				media = selectedMedias.get(0);
			}
			setMedia(media);
		}
	}

	private boolean okToUpload(Media media) {

		if (media.getMediaType().equals(MediaObject.MEDIA_TYPE_DEFAULT)
				&& !media.isDeleted()
				&& media.getLatitude() != Media.NOT_SPECIFIED_LATITUDE
				&& media.getLongitude() != Media.NOT_SPECIFIED_LONGITUDE) {
			return true;
		}

		return false;
	}

	private List<Media> getEditedMedias() {
		List<Media> medias = MediaManager.getInstance().getSelectedMedias();
		if (medias.isEmpty()) {
			medias = MediaManager.getInstance().getMedias();
		}
		return medias;
	}

	private List<Media> getUploadableMedias() {
		List<Media> medias = MediaManager.getInstance().getSelectedMedias();
		if (medias.isEmpty()) {
			medias = MediaManager.getInstance().getMedias();
		}
		return medias;
	}

	class RotateHandler implements RotateListener {

		private static final String ROTATING_IMAGES_DIALOG_MESSAGE = "ROTATING_IMAGES_DIALOG_MESSAGE";

		private List<Media> medias;
		private JDialog rotateDialog;
		private int rotated = 0;

		public RotateHandler() {
			init();
		}

		public RotateHandler(Media media) {
			this.medias = new ArrayList<Media>();
			medias.add(media);
			init();
		}

		public RotateHandler(List<Media> medias) {
			this.medias = medias;
			init();
		}

		private void init() {
			rotateDialog = new JDialog(MediaBrowserFrame.this, MediaBrowser
					.getString(ROTATING_IMAGES_DIALOG_MESSAGE), false);
			JProgressBar dpb = new JProgressBar();
			dpb.setIndeterminate(true);
			rotateDialog.add(BorderLayout.CENTER, dpb);
			rotateDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			rotateDialog.setBounds(getX() + getWidth() / 2, getY()
					+ getHeight() / 2, 300, 75);
		}

		public void rotate(List<Media> medias, int angle) {
			this.medias = new ArrayList<Media>();
			this.medias.addAll(medias);
			rotate(angle);
		}

		public void rotate(int angle) {

			CompoundEdit compound = new CompoundEdit();
			for (Media aMedia : medias) {

				if (aMedia.isMediaFileAvailable()) {
					UndoableEdit edit = UndoableMediaEdit.rotate(aMedia, angle);
					compound.addEdit(edit);
				}
			}
			compound.end();
			undoManager.addEdit(compound);
			setUndo();
		}

		public void rotateStarting(int count) {
	
			rotateLeftButton.setEnabled(false);
			rotateRightButton.setEnabled(false);
			rotateLeftItem.setEnabled(false);
			rotateRightItem.setEnabled(false);
		}

		public void rotateFinished() {
			rotateLeftButton.setEnabled(true);
			rotateRightButton.setEnabled(true);
			rotateLeftItem.setEnabled(true);
			rotateRightItem.setEnabled(true);
		}

		public void rotateStarting(Media media) {

		}

		public void rotateFinished(Media media) {
		}

		public void thumbnailRotatingCompleted(Media media, BufferedImage image) {
		}

		public void contentImageRotatingCompleted(Media media,
				BufferedImage image) {
		}

		public void imageRotatingCompleted(Media media, BufferedImage image) {
		}
	}

	public void resetUndoManager() {
		if (undoManager != null) {
			undoManager.discardAllEdits();
			setUndo();
		}
	}

}
