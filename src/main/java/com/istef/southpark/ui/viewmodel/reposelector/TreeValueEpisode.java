package com.istef.southpark.ui.viewmodel.reposelector;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.repo.Repo;
import com.istef.southpark.requests.MgidParser.MgidParserModel;

import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TreeValueEpisode implements TreeValueBase {
	private static final Logger logger = LoggerFactory.getLogger(TreeValueEpisode.class);
	
	public static final int STATUS_LOADING = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_ERROR = 2;

	private TreeValueSeason parent;
	private ObjectProperty<Repo> repoProperty;
	private ObjectProperty<Mgid> mgidProperty;
	private ObjectProperty<MgidParserModel> mgidParserModelProperty;
	private ObservableList<Resolution> resolutionsObservable;
	private IntegerProperty statusProperty;
	private ObjectProperty<Exception> exceptionProperty;

	public TreeValueEpisode(ObjectProperty<Repo> repoProperty, TreeValueSeason parent, Mgid mgid,
			MgidParserModel model, List<Resolution> resolutions) {
		
		this.parent = parent;
		this.repoProperty = repoProperty;
		this.mgidProperty = new SimpleObjectProperty<>(mgid);
		this.mgidProperty.addListener(new ChangeListener<Mgid>() {

			@Override
			public void changed(ObservableValue<? extends Mgid> observable, Mgid oldValue, Mgid newValue) {
				parent.getSeason().mgids.set(parent.getSeason().mgids.indexOf(oldValue), newValue);
				try {
					repoProperty.get().build();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});

		mgidParserModelProperty = new SimpleObjectProperty<>(model);
		mgidParserModelProperty.addListener(new ChangeListener<MgidParserModel>() {

			@Override
			public void changed(ObservableValue<? extends MgidParserModel> observable, MgidParserModel oldValue,
					MgidParserModel newValue) {
				if (newValue == null) {
					if (exceptionProperty.get() == null) {
						statusProperty.set(STATUS_LOADING);
					} else {
						statusProperty.set(STATUS_ERROR);
					}
				} else {
					statusProperty.set(STATUS_SUCCESS);	
				}
			}
		});
		
		resolutionsObservable = FXCollections.observableArrayList();
		if (resolutions != null) {
			resolutionsObservable.addAll(resolutions);
		}
		exceptionProperty = new SimpleObjectProperty<>();
		if (model == null) {
			statusProperty = new SimpleIntegerProperty(STATUS_LOADING);
		} else {
			statusProperty = new SimpleIntegerProperty(STATUS_SUCCESS);
		}
	}

	public TreeValueEpisode(ObjectProperty<Repo> repoProperty, TreeValueSeason parent, Mgid mgid) {
		this(repoProperty, parent, mgid, null, null);
	}

	public void setParams(MgidParserModel model, List<Resolution> resolutions) {
		mgidParserModelProperty.set(model);
		resolutionsObservable.setAll(resolutions);
	}

	public TreeValueSeason getParent() {
		return parent;
	}


	public ObjectProperty<Repo> getRepoProperty() {
		return repoProperty;
	}

	public ObjectProperty<Mgid> getMgidProperty() {
		return mgidProperty;
	}

	public ObjectProperty<MgidParserModel> getMgidParserModelProperty() {
		return mgidParserModelProperty;
	}

	public ObservableList<Resolution> getResolutionsObservable() {
		return resolutionsObservable;
	}

	public IntegerProperty getStatusProperty() {
		return statusProperty;
	}

	public ObjectProperty<Exception> getExceptionProperty() {
		return exceptionProperty;
	}

	public void setException(Exception e) {
		if (e != null) {
			statusProperty.set(STATUS_ERROR);
		}
		exceptionProperty.set(e);
	}

	@Override
	public String toString() {
		if (statusProperty.get() == STATUS_SUCCESS)
			return mgidParserModelProperty.get().getEpisode().getEpisodeNum() + ". "
					+ mgidParserModelProperty.get().getEpisode().getTitle();
		return mgidProperty.get().mgid;
	}

}