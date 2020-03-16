package com.istef.southpark.ui.viewmodel.reposelector;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.repo.Repo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TreeValueRepo implements TreeValueBase {
	private static final Logger logger = LoggerFactory.getLogger(TreeValueRepo.class);
	
	private ObjectProperty<Repo> repoProperty;
	private StringProperty nameProperty;
	private StringProperty descriptionProperty;
	private ObservableList<TreeValueSeason> seasonsObservable;

	public TreeValueRepo(Repo repo) {
		repoProperty = new SimpleObjectProperty<>(repo);
		repoProperty.addListener(new ChangeListener<Repo>() {
			@Override
			public void changed(ObservableValue<? extends Repo> observable, Repo oldValue, Repo newValue) {
				nameProperty.set(newValue.getRepoModel().name);
				descriptionProperty.set(newValue.getRepoModel().description);
				seasonsObservable.setAll(repo.getRepoModel().seasons.stream()
						.map(season -> new TreeValueSeason(repoProperty, TreeValueRepo.this, season)).collect(Collectors.toList()));
			}
		});
		nameProperty = new SimpleStringProperty(repo.getRepoModel().name);
		nameProperty.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				repoProperty.get().getRepoModel().name = newValue;
				try {
					getRepoProperty().get().build();

				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
		descriptionProperty = new SimpleStringProperty(repo.getRepoModel().description);
		descriptionProperty.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				repoProperty.get().getRepoModel().description = newValue;
				try {
					getRepoProperty().get().build();

				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
		seasonsObservable = FXCollections.observableArrayList(repo.getRepoModel().seasons.stream()
				.map(season -> new TreeValueSeason(repoProperty, this, season)).collect(Collectors.toList()));
		seasonsObservable.addListener(new ListChangeListener<TreeValueSeason>() {

			@Override
			public void onChanged(Change<? extends TreeValueSeason> c) {
				while (c.next()) {
					if (c.wasRemoved()) {
						getRepoProperty().get().getRepoModel().seasons.removeAll(
								c.getRemoved().stream().map(val -> val.getSeason()).collect(Collectors.toList()));
					}
					if (c.wasAdded()) {
						getRepoProperty().get().getRepoModel().seasons.addAll(
								c.getAddedSubList().stream().map(val -> val.getSeason()).collect(Collectors.toList()));
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

	public void setRepo(Repo repo) {
		repoProperty.set(repo);
	}

	@Override
	public String toString() {
		return repoProperty.get().getRepoModel().name;
	}

	public ObjectProperty<Repo> getRepoProperty() {
		return repoProperty;
	}

	public StringProperty getNameProperty() {
		return nameProperty;
	}

	public void setNameProperty(StringProperty nameProperty) {
		this.nameProperty = nameProperty;
	}

	public StringProperty getDescriptionProperty() {
		return descriptionProperty;
	}

	public void setDescriptionProperty(StringProperty descriptionProperty) {
		this.descriptionProperty = descriptionProperty;
	}

	public ObservableList<TreeValueSeason> getSeasonsObservable() {
		return seasonsObservable;
	}

}