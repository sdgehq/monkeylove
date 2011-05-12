package fi.oulu.mediabrowserlite.media.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fi.oulu.mediabrowserlite.ThumbnailGenerator;
import fi.oulu.mediabrowserlite.media.Media;
import fi.oulu.mediabrowserlite.media.provider.storage.DefaultMediaStorage;

public class DefaultMediaProvider extends AbstractMediaProvider {

	public DefaultMediaProvider() {
		super(new DefaultMediaStorage());
	}

	public boolean deleteMedia(Media media, boolean internalAction) {
		media.setDeleted(true);
		return updateMedia(media, internalAction);
	}

	public boolean undeleteMedia(Media media, boolean internalAction) {
		media.setDeleted(false);
		return updateMedia(media, internalAction);
	}

	public void prepareForShutdown() {
		List<Media> medias = getStorage().getMedias();
		for (Media media : medias) {
			if (media.isDeleted()) {

				System.out
						.println("DefaultMediaProvider.prepareForShutdown: deleting media "
								+ media.getId());
				deleteFiles(media);
				getStorage().deleteMedia(media);
			}
		}
	}

	public List<Media> load() {
		List<Media> medias = new ArrayList<Media>();

		for (Media media : getStorage().getMedias()) {

			if (media.isDeleted()) {
				System.out
						.println("DefaultMediaProvider.load, media "
								+ media.getId()
								+ " is marked as deleted, but hasnt been deleted from the database for some reason, deleting it now");
				getStorage().deleteMedia(media);
				deleteFiles(media);
			} else {

				String path = media.getPath();
				if (path == null || path.equals("")) {
					System.out
							.println("DefaultMediaProvider.load, no path --> deleting media from database: "
									+ media);
					deleteFiles(media);
					getStorage().deleteMedia(media);
				} else {
					File file = new File(path);
					if (!file.exists()) {
						System.out
								.println("DefaultMediaProvider.load, no file with the specified path exists --> deleting media from database: "
										+ media);
						deleteFiles(media);
						getStorage().deleteMedia(media);
					} else {
						media.setThumbnailImagePath(ThumbnailGenerator
								.generateThumbnail(media, false));
						media.setContentImagePath(ThumbnailGenerator
								.generateContentImage(media, false));
						medias.add(media);
					}
				}
			}
		}
		return medias;
	}

}
