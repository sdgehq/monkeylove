package fi.oulu.mediabrowserlite.media;

import java.awt.image.BufferedImage;

public interface RotateListener {

	public void rotateStarting(int cout);

	public void rotateStarting(Media media);

	public void thumbnailRotatingCompleted(Media media, BufferedImage image);

	public void contentImageRotatingCompleted(Media media, BufferedImage image);

	public void imageRotatingCompleted(Media media, BufferedImage image);

	public void rotateFinished(Media media);

	public void rotateFinished();
}
