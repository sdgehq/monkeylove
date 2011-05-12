package fi.oulu.mediabrowserlite.media;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import fi.oulu.mediabrowserlite.MediaBrowserSettings;
import fi.oulu.mediabrowserlite.media.Media.Type;

public class MediaHelper {

	private static final String TYPE_POSITIVE = "pos";
	private static final String TYPE_NEUTRAL = "neu";
	private static final String TYPE_NEGATIVE = "neg";

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy:MM:dd HH:mm:ss");

	public static Date parseDate(String dateString) {
		try {
			if (dateString != null && !dateString.equals("")) {
				return DATE_FORMAT.parse(dateString);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toString(Date dateCreated) {
		String dateString = "";
		if (dateCreated != null) {
			dateString = DATE_FORMAT.format(dateCreated);
		}
		return dateString;
	}

	public static Date toGmt(Date date) {
		try {
			String dateString = getGmtDateFormat().format(date);
			date = getDateFormat().parse(dateString);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static SimpleDateFormat getGmtDateFormat() {
		SimpleDateFormat dateFormat = getDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT:00"));
		return dateFormat;
	}

	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static Type parseType(String string) {
		if (string.equals(TYPE_POSITIVE)) {
			return Type.POSITIVE;
		}
		if (string.equals(TYPE_NEUTRAL)) {
			return Type.NEUTRAL;
		}
		if (string.equals(TYPE_NEGATIVE)) {
			return Type.NEGATIVE;
		}
		return null;
	}

	public static String toString(Type type) {
		switch (type) {
		case POSITIVE:
			return TYPE_POSITIVE;
		case NEUTRAL:
			return TYPE_NEUTRAL;
		case NEGATIVE:
			return TYPE_NEGATIVE;
		}
		return null;
	}

	public static String getMimeType(File file) {
		String mime = null;
		String path = file.getAbsolutePath().toLowerCase();
		if (path.endsWith("jpg")) {
			return "image/jpg";
		}
		return mime;
	}

	public static boolean isImage(Media media) {
		return media.getMimeType().toLowerCase().startsWith("image");
	}

	public static boolean isAudio(Media media) {
		return media.getMimeType().toLowerCase().startsWith("audio");
	}

	public static boolean isVideo(Media media) {
		return media.getMimeType().toLowerCase().startsWith("video");
	}

	public static String getUniqueFilePath(String path) {
		File file = new File(path);
		if (file.exists()) {
			File parentDir = file.getParentFile();
			File[] files = parentDir.listFiles();

			int i = 0;
			String filename = path.substring(path.lastIndexOf(System
					.getProperty("file.separator")) + 1, path.lastIndexOf('.'));
			String ext = path.substring(path.lastIndexOf('.') + 1);

			for (File f : files) {
				if (f.getAbsolutePath().startsWith(
						MediaBrowserSettings.getInstance().getMediaDirectory()
								+ filename)) {
					i++;
				}
			}
			path = parentDir.getAbsolutePath()
					+ System.getProperty("file.separator") + filename + i + "."
					+ ext;
		}
		return path;
	}

	public static String getMediaFilePath(String path) {
		String newPath = MediaBrowserSettings.getInstance().getMediaDirectory()
				+ path.substring(path.lastIndexOf(System
						.getProperty("file.separator")) + 1);
		return getUniqueFilePath(newPath);
	}

	public static void copyFile(File src, File dst) {
		try {
			if (src.exists()) {
				boolean ok = false;
				if (!dst.exists()) {
					ok = dst.createNewFile();
				}
				if (ok) {
					InputStream in = new FileInputStream(src);
					OutputStream out = new FileOutputStream(dst);

					System.out.println("MediaImporter, copying file "
							+ src.getAbsolutePath() + " to media dir: "
							+ dst.getAbsolutePath());

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static final int ROTATE_90 = 90;
	public static final int ROTATE_180 = 180;
	public static final int ROTATE_270 = 270;

	public static BufferedImage rotate(BufferedImage in, double angle) {
		int w = in.getWidth();
		int h = in.getHeight();
		BufferedImage out = new BufferedImage(h, w, in.getType());
		Graphics2D outG2 = (Graphics2D) out.getGraphics();

		AffineTransform tr = (AffineTransform) ((Graphics2D) in.getGraphics())
				.getTransform().clone();
		tr.rotate(Math.toRadians(angle), out.getWidth() / 2,
				out.getHeight() / 2);
		outG2.setTransform(tr);

		int x = out.getWidth() / 2 - in.getWidth() / 2;
		int y = out.getHeight() / 2 - in.getHeight() / 2;

		outG2.drawImage(in, x, y, null);
		outG2.dispose();
		return out;
	}

	public static BufferedImage flipVertically(BufferedImage image) {
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(image, null);
	}

	public static BufferedImage flipHorizontally(BufferedImage image) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(image, null);
	}

	public static BufferedImage scale(BufferedImage src, int maxSize, int type) {
		if (src != null) {
			double scale = (double) src.getWidth() / (double) src.getHeight();
			int width, height;
			if (scale > 0) {
				width = maxSize;
				height = (int) (width / scale);
			} else if (scale < 0) {
				height = maxSize;
				width = (int) (height * scale);
			} else {
				width = maxSize;
				height = maxSize;
			}
			return scale(src, width, height, type);
		}
		return null;
	}

	public static BufferedImage scale(BufferedImage src, int width, int height,
			int type) {
		BufferedImage dst = new BufferedImage(width, height, type);
		Graphics2D g2 = (Graphics2D) dst.getGraphics();
		AffineTransform tr = AffineTransform.getScaleInstance((double) width
				/ src.getWidth(), (double) height / src.getHeight());
		g2.drawRenderedImage(src, tr);
		return dst;
	}

	public static byte[] getBytes(String path) {
		try {
			File file = new File(path);
			InputStream is = new FileInputStream(file);
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				// TODO do something?
			}
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}
			is.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
