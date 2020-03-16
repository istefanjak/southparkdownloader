package com.istef.southpark.ui.viewmodel.reposelector;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.model.json.RepoModel.Season;
import com.istef.southpark.repo.Repo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TreeValueSeason implements TreeValueBase {
	private static final Logger logger = LoggerFactory.getLogger(TreeValueSeason.class);
	
	private TreeValueRepo parent;
	private Season season;
	private ObjectProperty<Repo> repoProperty;
	private StringProperty nameProperty;
	private ObservableList<TreeValueEpisode> episodesObservable;

	public TreeValueSeason(ObjectProperty<Repo> repoProperty, TreeValueRepo parent, Season season) {
		this.season = season;
		this.parent = parent;
		this.repoProperty = repoProperty;
		nameProperty = new SimpleStringProperty(season.name);
		nameProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				season.name = newValue;
				try {
					repoProperty.get().build();

				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
		episodesObservable = FXCollections.observableArrayList(
				season.mgids.stream().map(mgid -> new TreeValueEpisode(repoProperty, TreeValueSeason.this, mgid))
						.collect(Collectors.toList()));
		episodesObservable.addListener(new ListChangeListener<TreeValueEpisode>() {
			@Override
			public void onChanged(Change<? extends TreeValueEpisode> c) {
				while (c.next()) {
					if (c.wasRemoved()) {
						season.mgids.removeAll(c.getRemoved().stream().map(val -> val.getMgidProperty().get())
								.collect(Collectors.toList()));
					}
					if (c.wasAdded()) {
						season.mgids.addAll(c.getAddedSubList().stream().map(val -> val.getMgidProperty().get())
								.collect(Collectors.toList()));
					}
					try {
						repoProperty.get().build();

					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		});
	}

	public TreeValueRepo getParent() {
		return parent;
	}

	public Season getSeason() {
		return season;
	}

	@Override
	public String toString() {
		return nameProperty.get();
	}

	public ObjectProperty<Repo> getRepoProperty() {
		return repoProperty;
	}

	public void setRepoProperty(ObjectProperty<Repo> repoProperty) {
		this.repoProperty = repoProperty;
	}

	public StringProperty getNameProperty() {
		return nameProperty;
	}

	public ObservableList<TreeValueEpisode> getEpisodesObservable() {
		return episodesObservable;
	}

}