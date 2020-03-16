package com.istef.southpark.ui.viewmodel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.istef.southpark.ui.viewmodel.EpisodeInfoViewModel.ResolutionWrapper;

import de.saxsys.mvvmfx.ViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RedownloadViewModel implements ViewModel {
	private ObservableList<ResolutionWrapper> resolutionsObservable = FXCollections.observableArrayList();

	public ObservableList<ResolutionWrapper> getResolutionsObservable() {
		return resolutionsObservable;
	}
	
	public void init(List<Resolution> resolutions) {
		resolutionsObservable.setAll(resolutions.stream().sorted(new Comparator<Resolution>() {

			@Override
			public int compare(Resolution o1, Resolution o2) {
				return -Integer.valueOf(o1.width() * o1.height()).compareTo(o2.width() * o2.height());
			}
			
		}).map(r -> new ResolutionWrapper(r)).collect(Collectors.toList()));
	}
}
