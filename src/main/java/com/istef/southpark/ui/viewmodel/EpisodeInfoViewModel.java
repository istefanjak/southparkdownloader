package com.istef.southpark.ui.viewmodel;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.istef.southpark.model.Episode;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.ui.model.DownloadItem;
import com.istef.southpark.ui.model.DownloadsModel;
import com.istef.southpark.util.Function;

import de.saxsys.mvvmfx.ViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class EpisodeInfoViewModel implements ViewModel {
	
	public static class ResolutionWrapper {
		private Resolution resolution;
		public ResolutionWrapper(Resolution resolution) {
			this.resolution = resolution;
		}
		public Resolution getResolution() {
			return resolution;
		}
		@Override
		public String toString() {
			return String.format("%dx%d", resolution.width(), resolution.height());
		}
	}
	
	public static final Integer EPISODE_IMAGE_W = 320;
	public static final Integer EPISODE_IMAGE_H = Function.calculate169Height(EPISODE_IMAGE_W);
	
	private StringProperty titleProperty = new SimpleStringProperty();
	private StringProperty descriptionProperty = new SimpleStringProperty();
	private StringProperty seasonProperty = new SimpleStringProperty();
	private StringProperty episodeProperty = new SimpleStringProperty();
	private StringProperty pubDateProperty = new SimpleStringProperty();
	private StringProperty durationProperty = new SimpleStringProperty();
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
	private ObservableList<ResolutionWrapper> resolutionsObservable = FXCollections.observableArrayList();
	
	private Mgid mgid;
	private MgidParserModel mgidParserModel;
	
	public void init(Mgid mgid, MgidParserModel mgidParserModel) {
		setMgidParserModel(mgid, mgidParserModel);
	}

	public void setResolutions(List<Resolution> resolutions) {
		resolutionsObservable.setAll(resolutions.stream().sorted(new Comparator<Resolution>() {

			@Override
			public int compare(Resolution o1, Resolution o2) {
				return -Integer.valueOf(o1.width() * o1.height()).compareTo(o2.width() * o2.height());
			}
			
		}).map(r -> new ResolutionWrapper(r)).collect(Collectors.toList()));
	}
	
	public ObservableList<ResolutionWrapper> getResolutionsObservable() {
		return resolutionsObservable;
	}

	public void setMgidParserModel(Mgid mgid, MgidParserModel mgidParserModel) {
		this.mgid = mgid;
		this.mgidParserModel = mgidParserModel;
		Episode episode = mgidParserModel.getEpisode();
		setTitle(episode.getTitle());
		setDescription(episode.getDescription());
		setSeason(episode.getSeasonNum());
		setEpisode(episode.getEpisodeNum());
		setPubDate(episode.getPubDate());
		setDuration(episode.getDurationStr());
		setImage(episode.getImageUri(EPISODE_IMAGE_W, EPISODE_IMAGE_H));
	}
	
	public MgidParserModel getMgidParserModel() {
		return mgidParserModel;
	}
	
	public Mgid getMgid() {
		return mgid;
	}

	public Episode getEpisode() {
		return mgidParserModel.getEpisode();
	}

	public void setTitle(String title) {
		titleProperty.set(title);
	}
	
	public void setDescription(String description) {
		descriptionProperty.set(description);
	}
	
	public void setSeason(String season) {
		seasonProperty.set(season);
	}
	
	public void setEpisode(String episode) {
		episodeProperty.set(episode);
	}
	
	public void setPubDate(String pubDate) {
		pubDateProperty.set(pubDate);
	}
	
	public void setDuration(String duration) {
		durationProperty.set(duration);
	}
	
	public void setImage(String imageUri) {
		Image image = new Image(imageUri, true);
		imageProperty.set(image);
	}
	
	public StringProperty getTitleProperty() {
		return titleProperty;
	}

	public StringProperty getDescriptionProperty() {
		return descriptionProperty;
	}

	public StringProperty getSeasonProperty() {
		return seasonProperty;
	}

	public StringProperty getEpisodeProperty() {
		return episodeProperty;
	}

	public StringProperty getPubDateProperty() {
		return pubDateProperty;
	}

	public StringProperty getDurationProperty() {
		return durationProperty;
	}

	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}
	
	public void addToDownloads(DownloadItem item) {
		DownloadsModel.getInstance().addDownloadItemAndStart(item);
	}
	
}
