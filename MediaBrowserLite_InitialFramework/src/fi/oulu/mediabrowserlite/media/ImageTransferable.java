package fi.oulu.mediabrowserlite.media;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ImageTransferable implements Transferable 
{
	private static DataFlavor [] flavors = { DataFlavor.imageFlavor };
	
	private Image image;
	
	public ImageTransferable( Image image )
	{
		this.image = image;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if( flavor == DataFlavor.imageFlavor )
		{
			return true;
		}
		return false;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
	{
		if( flavor == DataFlavor.imageFlavor )
		{
			return image;
		}
		throw new UnsupportedFlavorException( flavor );
	}
	
	
}
