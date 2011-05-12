package fi.oulu.mediabrowserlite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;

import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.MediaHelper;
import fi.oulu.mediabrowserlite.media.MediaManager;
import fi.oulu.mediabrowserlite.media.MediaObject;

public class MediaImporter {

	public static boolean isImportableFile(File file) {
		return file != null && file.exists()
				&& isImportableFile(file.getAbsolutePath());
	}

	public static boolean isImportableFile(String path) {
		return path.toLowerCase().endsWith("jpg");
	}

	public static void importFile(String path, MediaImportListener listener) {
		importFiles(new File[] { new File(path) }, listener);
	}

	public static void importFiles(String directory,
			MediaImportListener listener) {
		importFiles(new File(directory), listener);
	}

	public static void importFiles(File directory, MediaImportListener listener) {
		if (directory.exists()) {
			File[] files = directory.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return isImportableFile(pathname);
				}
			});
			importFiles(files, listener);
		} else {
			throw new IllegalArgumentException("Directory "
					+ directory.getAbsolutePath() + " does not exist+");
		}
	}

	public static void importFiles(final File[] files,
			final MediaImportListener listener) {
		if (files.length > 0) {
			new Thread(new Runnable() {
				public void run() {
					if (listener != null) {
						listener.mediaImportStarted(files.length);
					}
					for (File file : files) {
						if (listener != null) {
							listener.mediaImportStarted(file.getAbsolutePath());
						}

						File fileInMediaDir = new File(MediaHelper
								.getMediaFilePath(file.getAbsolutePath()));

						if (!fileInMediaDir.exists()) {
							MediaHelper.copyFile(file, fileInMediaDir);
						}

						Media media = MediaImporter.createMedia(fileInMediaDir);
						//System.out.println("XXXMedias size: "+MediaManager.getInstance().getMedias().size());
						if (media != null) {
							MediaManager.getInstance().newMedia(media, true);
						} else {
							System.out
									.println("MediaHelper.createMedia returned null");
						}
						System.out.println("YYYMedias size: "+MediaManager.getInstance().getMedias().size());
						if (listener != null) {
							listener
									.mediaImportFinished(file.getAbsolutePath());
						}
					}
					if (listener != null) {
						listener.mediaImportFinished();
					}
				}
			}).start();
		}
	}

	public static Media createMedia(File file) {
		if (file.exists()) {
			String mime = MediaHelper.getMimeType(file);
			if (mime != null) {
				Media media = new MediaObject();
				media.setMimeType(mime);
				media.setPath(file.getAbsolutePath());
				if (MediaHelper.isImage(media)) {
					handleExif(media);

				}
				media.setThumbnailImagePath(ThumbnailGenerator
						.generateThumbnail(media, true));
				media.setContentImagePath(ThumbnailGenerator
						.generateContentImage(media, true));
				return media;
			}
		}
		return null;
	}

	private static void handleExif(Media media) {
		try {
			File jpegFile = new File(media.getPath());
			Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);

			setDates(media, metadata);
			setCameraInfo(media, metadata);
			setGPSInfo(media, metadata);

			handleImageOrientation(metadata, media);

		} catch (JpegProcessingException e) {
			e.printStackTrace();
		}
	}

	private static final int NONE = 0;
	private static final int HORIZONTAL = 1;
	private static final int VERTICAL = 2;
	private static final int[][] OPERATIONS = new int[][] {
			new int[] { 0, NONE }, new int[] { 0, HORIZONTAL },
			new int[] { 180, NONE }, new int[] { 0, VERTICAL },
			new int[] { 90, HORIZONTAL }, new int[] { 90, NONE },
			new int[] { -90, HORIZONTAL }, new int[] { -90, NONE }, };

	private static void handleImageOrientation(Metadata metadata, Media media) {
		ExifDirectory exifDir = (ExifDirectory) metadata
				.getDirectory(ExifDirectory.class);

		if (exifDir.containsTag(ExifDirectory.TAG_ORIENTATION)) {
			try {
				int orientation = exifDir.getInt(ExifDirectory.TAG_ORIENTATION);
				System.out
						.println("MediaImporter.handleImageOrientation, orientation value in exif data: "
								+ orientation);

				int index = orientation - 1;
				if (index > 0) {
					File imageFile = new File(media.getPath());
					BufferedImage image = ImageIO.read(imageFile);
					int degrees = OPERATIONS[index][0];
					if (degrees != 0) {
						System.out
								.println("MediaImporter.handleImageOrientation, rotating image by: "
										+ degrees);
						image = MediaHelper.rotate(image, degrees);
					}
					switch (OPERATIONS[index][1]) {
					case HORIZONTAL:
						System.out
								.println("MediaImporter.handleImageOrientation, HORIZONTAL");
						image = MediaHelper.flipVertically(image);
						break;
					case VERTICAL:
						System.out
								.println("MediaImporter.handleImageOrientation, VERTICAL");
						image = MediaHelper.flipHorizontally(image);
					}

					Iterator<ImageWriter> writers = ImageIO
							.getImageWritersByMIMEType(getMimeTypeForImageIO(media));

					if (writers != null && writers.hasNext()) {
						ImageWriter writer = writers.next();
						FileOutputStream fout = new FileOutputStream(imageFile);
						ImageOutputStream ios = ImageIO
								.createImageOutputStream(fout);
						writer.setOutput(ios);
						ImageWriteParam param = writer.getDefaultWriteParam();
						if (!imageFile.getPath().toLowerCase().endsWith("png")) {
							param
									.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
							param.setCompressionQuality(0.9f);
						}
						writer.write(null, new IIOImage(image, null, null),
								param);
					} else {
						System.out
								.println("MediaImporter.handleImageOrientation, no image writers");
					}
				} else {
					System.out
							.println("MediaImporter.handleImageOrientation, orientation 1, nothing to do");
				}
			} catch (MetadataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out
					.println("MediaImporter.handleImageOrientation, no orientation info in exif data");
		}
	}

	private static String getMimeTypeForImageIO(Media media) {
		if (media.getMimeType().equalsIgnoreCase("image/jpg")) {
			return "image/jpeg";
		}
		return media.getMimeType();
	}

	private static void setDates(Media media, Metadata metadata) {
		ExifDirectory exifDir = (ExifDirectory) metadata
				.getDirectory(ExifDirectory.class);
		if (exifDir != null
				&& exifDir.containsTag(ExifDirectory.TAG_DATETIME_ORIGINAL)) {
			String dateString = exifDir
					.getString(ExifDirectory.TAG_DATETIME_ORIGINAL);

			Date date = MediaHelper.parseDate(dateString);
			Date dateGmt = MediaHelper.toGmt(date);

			media.setDateCreated(date);
			media.setDateCreatedGmt(dateGmt);

			media.setDateModified(date);
			media.setDateModifiedGmt(dateGmt);
		}
	}

	private static void setCameraInfo(Media media, Metadata metadata) {
		ExifDirectory exifDir = (ExifDirectory) metadata
				.getDirectory(ExifDirectory.class);
		if (exifDir != null) {
			if (exifDir.containsTag(ExifDirectory.TAG_MAKE)) {
				media.setMeta(Media.META_KEY_MAKE, exifDir
						.getString(ExifDirectory.TAG_MAKE));
			}
			if (exifDir.containsTag(ExifDirectory.TAG_MODEL)) {
				media.setMeta(Media.META_KEY_MODEL, exifDir
						.getString(ExifDirectory.TAG_MODEL));
			}
		}
	}

	private static void setGPSInfo(Media media, Metadata metadata) {
		media.setLatitude(getLatitude(metadata));
		media.setLongitude(getLongitude(metadata));
		media.setAltitude(getAltitude(metadata));
		media.setDirection(getDirection(metadata));
	}

	private static double getLatitude(Metadata metadata) {
		GpsDirectory gpsDir = (GpsDirectory) metadata
				.getDirectory(GpsDirectory.class);

		if (gpsDir != null && gpsDir.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
			String latitudeString = gpsDir
					.getString(GpsDirectory.TAG_GPS_LATITUDE);
			double latitude = getCoordinateInDecimals(latitudeString);
			if (latitude != Double.NaN) {
				return latitude;
			}
		}
		return Media.NOT_SPECIFIED_LATITUDE;
	}

	private static double getLongitude(Metadata metadata) {
		GpsDirectory gpsDir = (GpsDirectory) metadata
				.getDirectory(GpsDirectory.class);
		if (gpsDir != null
				&& gpsDir.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)) {
			String longitudeString = gpsDir
					.getString(GpsDirectory.TAG_GPS_LONGITUDE);

			double longitude = getCoordinateInDecimals(longitudeString);
			if (longitude != Double.NaN) {
				return longitude;
			}
		}
		return Media.NOT_SPECIFIED_LONGITUDE;
	}

	private static double getAltitude(Metadata metadata) {
		try {
			GpsDirectory gpsDir = (GpsDirectory) metadata
					.getDirectory(GpsDirectory.class);
			if (gpsDir != null
					&& gpsDir.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) {
				return gpsDir.getDouble(GpsDirectory.TAG_GPS_ALTITUDE);
			}
		} catch (MetadataException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static double getDirection(Metadata metadata) {
		GpsDirectory gpsDir = (GpsDirectory) metadata
				.getDirectory(GpsDirectory.class);
		if (gpsDir != null
				&& gpsDir.containsTag(GpsDirectory.TAG_GPS_IMG_DIRECTION)) {
			String directionString = gpsDir
					.getString(GpsDirectory.TAG_GPS_IMG_DIRECTION);
			return getDirection(directionString);
		}
		return -1;
	}

	private static double getCoordinateInDecimals(
			String degreesMinutesSecondsExifString) {

		if (degreesMinutesSecondsExifString != null) {
			String[] parts = degreesMinutesSecondsExifString.split(" ");
			if (parts.length == 3) {
				String[] degreesParts = parts[0].split("/");
				String[] minutesParts = parts[1].split("/");
				String[] secondsParts = parts[2].split("/");
				if (degreesParts.length == 2 && minutesParts.length == 2
						&& secondsParts.length == 2) {
					try {
						double degrees = Double.parseDouble(degreesParts[0])
								/ Double.parseDouble(degreesParts[1]);
						double minutes = Double.parseDouble(minutesParts[0])
								/ Double.parseDouble(minutesParts[1]);
						double seconds = Double.parseDouble(secondsParts[0])
								/ Double.parseDouble(secondsParts[1]);
						return convert(degrees, minutes, seconds);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return Double.NaN;
	}

	private static double convert(double degrees, double minutes, double seconds) {
		return degrees + (minutes * 1 / 60) + (seconds / 3600);
	}

	private static double getDirection(String exifDirectionString) {
		if (exifDirectionString != null) {
			try {
				String[] parts = exifDirectionString.split("/");
				if (parts.length == 2) {
					return Double.parseDouble(parts[0])
							/ Double.parseDouble(parts[1]);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static void printExif(String file) {
		printExif(new File(file));
	}

	public static void printExif(File file) {
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(file);
			Iterator directories = metadata.getDirectoryIterator();
			while (directories.hasNext()) {
				Directory directory = (Directory) directories.next();
				Iterator tags = directory.getTagIterator();
				while (tags.hasNext()) {
					Tag tag = (Tag) tags.next();
					System.out.println(tag);
				}
			}
		} catch (JpegProcessingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}
