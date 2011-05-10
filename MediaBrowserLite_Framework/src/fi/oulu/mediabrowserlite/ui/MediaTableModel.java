package fi.oulu.mediabrowserlite.ui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import fi.oulu.mediabrowserlite.MediaBrowser;
import fi.oulu.mediabrowserlite.media.Media;

public class MediaTableModel extends AbstractTableModel
{
	public static final String ATTRIBUTE_LABEL = MediaBrowser.getString( "ATTRIBUTE_LABEL" );
	public static final String VALUE_LABEL = MediaBrowser.getString( "VALUE_LABEL" );
	public static final String ID_LABEL = MediaBrowser.getString( "ID_LABEL" );
	public static final String MEDIA_TYPE_LABEL = MediaBrowser.getString( "MEDIA_TYPE_LABEL" );
	public static final String AUTHOR_LABEL = MediaBrowser.getString( "AUTHOR_LABEL" );
	public static final String TITLE_LABEL = MediaBrowser.getString( "TITLE_LABEL" );
	public static final String DESCRIPTION_LABEL = MediaBrowser.getString( "DESCRIPTION_LABEL" );
	public static final String CREATION_DATE_LABEL = MediaBrowser.getString( "CREATION_DATE_LABEL" );
	public static final String MIME_TYPE_LABEL = MediaBrowser.getString( "MIME_TYPE_LABEL" );
	public static final String FILE_NAME_LABEL = MediaBrowser.getString( "FILE_NAME_LABEL" );
	public static final String LATITUDE_LABEL = MediaBrowser.getString( "LATITUDE_LABEL" );
	public static final String LONGITUDE_LABEL = MediaBrowser.getString( "LONGITUDE_LABEL" );
	public static final String DIRECTION_LABEL = MediaBrowser.getString( "DIRECTION_LABEL" );
	public static final String TYPE_LABEL = MediaBrowser.getString( "TYPE_LABEL" );
	public static final String POSITIVE_LABEL = MediaBrowser.getString( "POSITIVE_LABEL" );
	public static final String NEGATIVE_LABEL = MediaBrowser.getString( "NEGATIVE_LABEL" );
	public static final String NEUTRAL_LABEL = MediaBrowser.getString( "NEUTRAL_LABEL" );
	public static final String YES_LABEL = MediaBrowser.getString( "YES_LABEL" );
	public static final String NO_LABEL = MediaBrowser.getString( "NO_LABEL" );
	
	private String [] keys = { 
			ID_LABEL, MEDIA_TYPE_LABEL, AUTHOR_LABEL, TITLE_LABEL, DESCRIPTION_LABEL, CREATION_DATE_LABEL,
			MIME_TYPE_LABEL, FILE_NAME_LABEL, LATITUDE_LABEL, LONGITUDE_LABEL, DIRECTION_LABEL, TYPE_LABEL
	};
	
	private Media media;
	private ArrayList<String> metas = null;
	
	public MediaTableModel()
	{
	}
	
	public void setMedia( Media media )
	{
		this.media = media;
		if( media != null )
		{
			Collection<String> metaKeys = media.getMetaKeys();
			metas = new ArrayList<String>( metaKeys );
		}
		else
		{
			metas = null;
		}
		fireTableDataChanged();
	}
	
	public int getRowCount()
	{
		int size = keys.length;
		if( metas != null )
		{
			size+=metas.size();
		}
		return size;
	}
	
	public int getColumnCount()
	{
		return 2;
	}
	
	public Object getValueAt( int row, int column )
	{
		String key;
		if( row < 0 )
		{
			return null;
		}
		if( row < keys.length )
		{
			key = keys[ row ];
		
			// Labels
			if( column == 0 )
			{
				return key;
			}
			// Values
			if( column == 1 && media != null )
			{
				if( key == ID_LABEL )
				{
					return media.getId();
				}
				if( key == MEDIA_TYPE_LABEL )
				{
					return media.getMediaType();
				}
				if( key == AUTHOR_LABEL )
				{
					return media.getAuthor();
				}
				if( key == TITLE_LABEL )
				{
					return media.getTitle();
				}
				if( key == DESCRIPTION_LABEL )
				{
					return media.getDescription();
				}
				if( key == CREATION_DATE_LABEL )
				{
					return media.getDateCreated();
				}
				if( key == MIME_TYPE_LABEL )
				{
					return media.getMimeType();
				}
				if( key == FILE_NAME_LABEL )
				{
					return media.getFileName();
				}
				if( key == LATITUDE_LABEL )
				{
					return media.getLatitude();
				}
				if( key == LONGITUDE_LABEL )
				{
					return media.getLongitude();
				}
				if( key == DIRECTION_LABEL )
				{
					return media.getDirection();
				}
				if( key == TYPE_LABEL )
				{
					Media.Type type = media.getType();
					if( type == Media.Type.POSITIVE )
					{
						return POSITIVE_LABEL;
					}
					if( type == Media.Type.NEGATIVE )
					{
						return NEGATIVE_LABEL;
					}
					return NEUTRAL_LABEL;
				}
			}
		}
		if( row >= keys.length )
		{
			// Process the meta data
	
			int index = row-keys.length;
			if( column == 0 )
			{
				String metaString = metas.get( index );
				metaString = MediaBrowser.getString( metaString+"_LABEL" );
				return metaString;
			}
			if( column == 1 )
			{
				String metaString = media.getMeta( metas.get( index ) );
				return metaString;
			}
		}
		return null;
	}
	
	public boolean isCellEditable( int row, int column )
	{
		String key;

		if( row < keys.length && row >= 0 )
		{
			key = keys[ row ];
			if( column == 1 )
			{
				if( key == AUTHOR_LABEL )
				{
					return true;
				}
				if( key == TITLE_LABEL )
				{
					return true;
				}
				if( key == DESCRIPTION_LABEL )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public String getColumnName( int column )
	{
		if( column == 0 )
		{
			return ATTRIBUTE_LABEL;
		}
		return VALUE_LABEL;
	}
}
