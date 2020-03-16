package com.istef.southpark.ui.model;

import java.util.UUID;

import com.istef.southpark.requests.M3U8Parser.Centile;

import io.lindstrom.m3u8.model.Resolution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//Singleton
public class DownloadsModel {
	
	private static DownloadsModel instance;

	private ObservableList<DownloadItem> downloadItems;

	private DownloadsModel() {
		downloadItems = FXCollections.observableArrayList();
	}

	synchronized public static DownloadsModel getInstance() {
		if (instance == null) {
			instance = new DownloadsModel();
		}
		return instance;
	}

	public ObservableList<DownloadItem> getDownloadItems() {
		return downloadItems;
	}

	public UUID addDownloadItemAndStart(DownloadItem item) {
		downloadItems.add(item);
		UUID uuid = ServicesModel.getInstance().downloadEpisode(item.getMgid(), item.getMgidParserModel(), item.getPath(),
				item.getResolutionProperty().get(), item.getStatusProperty(), item.getExceptionProperty(), item.getProgressProperty());
		item.setTaskId(uuid);
		return uuid;
	}
	
	public void removeDownloadItem(DownloadItem item) {
		ServicesModel.getInstance().cancelTask(item.getTaskId());
		downloadItems.remove(item);
	}
	
	public void cancelDownloadItem(DownloadItem item) {
		ServicesModel.getInstance().cancelTask(item.getTaskId());
		item.setProgress(new Centile(0.));
		item.getStatusProperty().set(DownloadItem.STATE_CANCELED);
	}
	
	public UUID restartDownloadItem(DownloadItem item, Resolution resolution) {
		ServicesModel.getInstance().cancelTask(item.getTaskId());
		item.setResolution(resolution);
		item.setProgress(new Centile(0.));
		UUID uuid = ServicesModel.getInstance().downloadEpisode(item.getMgid(), item.getMgidParserModel(), item.getPath(),
				item.getResolutionProperty().get(), item.getStatusProperty(), item.getExceptionProperty(), item.getProgressProperty());
		item.setTaskId(uuid);
		item.getStatusProperty().set(DownloadItem.STATE_DOWNLOADING);
		return uuid;
	}
}
