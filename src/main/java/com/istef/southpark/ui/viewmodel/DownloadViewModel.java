package com.istef.southpark.ui.viewmodel;

import com.istef.southpark.ui.model.DownloadItem;
import com.istef.southpark.ui.model.DownloadsModel;
import com.istef.southpark.ui.view.download.DownloadView.DownloadItemCellItem;
import com.istef.southpark.util.BindingUtil;

import de.saxsys.mvvmfx.ViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DownloadViewModel implements ViewModel {
	private ObservableList<DownloadItemCellItem> downloadItems = FXCollections.observableArrayList();

	public DownloadViewModel() {
		ObservableList<DownloadItem> source = DownloadsModel.getInstance().getDownloadItems();
		BindingUtil.mapContent(downloadItems, source, m -> new DownloadItemCellItem(m));
	}
	
	public ObservableList<DownloadItemCellItem> getDownloadItems() {
		return downloadItems;
	}

	public void addDownloadItem(DownloadItem item) {
		DownloadsModel.getInstance().addDownloadItemAndStart(item);
	}
	
	public void removeDownloadItem(DownloadItem item) {
		DownloadsModel.getInstance().removeDownloadItem(item);
	}
	
	public void restartDownloadItem(DownloadItem item, Resolution resolution) {
		DownloadsModel.getInstance().restartDownloadItem(item, resolution);
	}
	
	public void cancelDownloadItem(DownloadItem item) {
		DownloadsModel.getInstance().cancelDownloadItem(item);
	}
}
