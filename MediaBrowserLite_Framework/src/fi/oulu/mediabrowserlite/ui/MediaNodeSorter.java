package fi.oulu.mediabrowserlite.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaNodeSorter {

	public enum SortType {
		FILE_NAME_ASC
	}

	public static void sort(SortType type, List<MediaNode> mediaNodes) {
		switch (type) {
		case FILE_NAME_ASC:
			sortByFileNameAsc(mediaNodes);
			break;
		}
	}

	public static void sortByFileNameAsc(List<MediaNode> nodes) {
		Collections.sort(nodes, new Comparator<MediaNode>() {
			public int compare(MediaNode node1, MediaNode node2) {
				String p1 = node1.getMedia().getPath();
				String p2 = node2.getMedia().getPath();
				if (p1 != null) {
					if (p2 != null) {
						return p1.compareTo(p2);
					} else {
						return -1;
					}
				} else if (p2 != null) {
					return 1;
				}

				return 0;
			}
		});
	}

}
