package com.istef.southpark.ui.viewmodel;

import java.util.List;

import org.javatuples.Pair;

import com.istef.southpark.Const;
import com.istef.southpark.ui.SettingsCheck;
import com.istef.southpark.ui.model.DownloadItem;
import com.istef.southpark.ui.model.DownloadsModel;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueEpisode;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueSeason;

import de.saxsys.mvvmfx.ViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class SeasonInfoViewModel implements ViewModel {
	private TreeValueSeason treeValueSeason;
	private ObservableList<TreeValueEpisode> baseList;
	private ObservableList<TreeValueEpisode> loadedFiltered;
	private ObservableList<TreeValueEpisode> otherFiltered;

	public void init(TreeValueSeason treeValueSeason) {
		this.treeValueSeason = treeValueSeason;

		baseList = FXCollections.observableArrayList(attached -> new Observable[] { attached.getStatusProperty() });
		Bindings.bindContentBidirectional(baseList, treeValueSeason.getEpisodesObservable());

		otherFiltered = new FilteredList<>(baseList,
				t -> t.getStatusProperty().get() != TreeValueEpisode.STATUS_SUCCESS);

		loadedFiltered = new FilteredList<>(baseList,
				t -> t.getStatusProperty().get() == TreeValueEpisode.STATUS_SUCCESS);

	}

	public StringProperty getNameProperty() {
		return treeValueSeason.getNameProperty();
	}

	public ObservableList<TreeValueEpisode> getLoadedFiltered() {
		return loadedFiltered;
	}

	public ObservableList<TreeValueEpisode> getOtherFiltered() {
		return otherFiltered;
	}

	public void addToDownloads(List<Pair<TreeValueEpisode, Resolution>> list) {
		if(!SettingsCheck.areDirsValid()) return;
		
		for (Pair<TreeValueEpisode, Resolution> pairVal : list) {
			TreeValueEpisode val = pairVal.getValue0();
			Resolution res = pairVal.getValue1();
			DownloadItem dlItem = new DownloadItem(val.getMgidProperty().get(), val.getMgidParserModelProperty().get(),
					val.getResolutionsObservable(), res, Const.DL_DIR);
			DownloadsModel.getInstance().addDownloadItemAndStart(dlItem);
		}
	}

}
