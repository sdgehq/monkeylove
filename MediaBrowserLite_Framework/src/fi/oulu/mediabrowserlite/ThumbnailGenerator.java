package fi.oulu.mediabrowserlite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaHelper;

public class ThumbnailGenerator {

	public static String generateThumbnail(Media media, boolean overwrite) {
		if (MediaHelper.isImage(media)) {

			String path = getThumbnailPath(media, MediaBrowserSettings
					.getInstance().getThumbnailFileNamePrefix());

			File file = new File(path);
			if (file.exists() && !overwrite) {
				return path;
			} else {
				return createThumbnailImage(media, MediaBrowserSettings
						.getInstance().getThumbnailSize(), path);
			}

		}
		return null;
	}

	public static String generateContentImage(Media media, boolean overwrite) {
		if (MediaHelper.isImage(media)) {

			String path = getThumbnailPath(media, MediaBrowserSettings
					.getInstance().getContentImageFileNamePrefix());

			File file = new File(path);
			if (file.exists() && !overwrite) {
				return path;
			} else {
				return createThumbnailImage(media, MediaBrowserSettings
						.getInstance().getContentImageSize(), path);
			}
		}
		return null;
	}

	private static String createThumbnailImage(Media media, int size,
			String path) {
		try {
			BufferedImage src = ImageIO.read(new File(media.getPath()));
			if (src != null) {
				BufferedImage dst = MediaHelper.scale(src, size,
						BufferedImage.TYPE_INT_RGB);

				String ext = path.substring(path.lastIndexOf('.') + 1);

				Iterator writers = null;
				if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
					writers = ImageIO.getImageWritersBySuffix("jpeg");
				} else if (ext.equalsIgnoreCase("png")) {
					writers = ImageIO.getImageWritersBySuffix("png");
				}
				if (writers == null || !writers.hasNext()) {
					throw new IllegalStateException("No writers found");
				}
				ImageWriter writer = (ImageWriter) writers.next();

				File thumbFile = new File(path);
				FileOutputStream fout = new FileOutputStream(thumbFile);
				ImageOutputStream ios = ImageIO.createImageOutputStream(fout);
				writer.setOutput(ios);
				ImageWriteParam param = writer.getDefaultWriteParam();
				if (!ext.equalsIgnoreCase("png")) {
					param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					param.setCompressionQuality(0.9f);
				}
				writer.write(null, new IIOImage(dst, null, null), param);
				return thumbFile.getAbsolutePath();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getThumbnailPath(Media media, String prefix) {
		String path = media.getPath();
		String ext = path.substring(path.lastIndexOf('.'));
		String filename = path.substring(path.lastIndexOf(System
				.getProperty("file.separator")) + 1, path.lastIndexOf('.'));

		StringBuilder sb = new StringBuilder();
		sb.append(MediaBrowserSettings.getInstance().getThumbnailDirectory());
		sb.append(prefix + "_");
		sb.append(filename);
		sb.append(ext);
		return sb.toString();
	}
}
