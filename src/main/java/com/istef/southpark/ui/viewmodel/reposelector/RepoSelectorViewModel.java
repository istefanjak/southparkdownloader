package com.istef.southpark.ui.viewmodel.reposelector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel.Season;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.repo.EmptyRepo;
import com.istef.southpark.repo.Repo;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.ui.model.RepoModel;
import com.istef.southpark.ui.model.ServicesModel;

import de.saxsys.mvvmfx.ViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class RepoSelectorViewModel implements ViewModel {
	private static final Logger logger = LoggerFactory.getLogger(RepoSelectorViewModel.class);
	
	private ObservableList<TreeValueRepo> repos = FXCollections.observableArrayList();
	private ObjectProperty<Exception> loadExceptionProperty = new SimpleObjectProperty<Exception>();
	private ListChangeListener<TreeValueRepo> reposListChangeListener = new ListChangeListener<TreeValueRepo>() {

		@Override
		public void onChanged(Change<? extends TreeValueRepo> c) {
			while (c.next()) {
				if (c.wasAdded()) {
					c.getAddedSubList().stream().forEach(val -> {
						try {
							val.getRepoProperty().get().build();

						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					});
				}

				if (c.wasRemoved()) {
					c.getRemoved().stream().forEach(val -> {
						try {
							RepoModel.deleteRepo(val.getRepoProperty().get());

						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					});
				}
			}
		}
	};

	public RepoSelectorViewModel() {
		repos.addListener(reposListChangeListener);
	}

	public ObservableList<TreeValueRepo> getRepos() {
		return repos;
	}

	public void loadRepos() {
		List<Repo> repos = RepoModel.loadRepos(loadExceptionProperty);
		this.repos.removeListener(reposListChangeListener);
		this.repos.setAll(repos.stream().map(val -> new TreeValueRepo(val)).collect(Collectors.toList()));
		this.repos.addListener(reposListChangeListener);
	}

	public void addToRepo(TreeValueRepo addTo, Season season) {
		addTo.getSeasonsObservable().add(new TreeValueSeason(addTo.getRepoProperty(), addTo, season));
	}

	public void addToRepo(TreeValueSeason addTo, Mgid mgid) {
		addTo.getEpisodesObservable().add(new TreeValueEpisode(addTo.getRepoProperty(), addTo, mgid));
	}

	public ObjectProperty<Exception> getLoadExceptionProperty() {
		return loadExceptionProperty;
	}

	public void getEpisodeInfoAsync(Mgid mgid, BiConsumer<MgidParserModel, List<Resolution>> onComplete,
			Consumer<Exception> onException) {
		ServicesModel.getInstance().getEpisodeInfoAsync(mgid, onComplete, onException);
	}
	
	public void createRepo(Path path, String name, String description) throws RepoLoadException {
		Repo repo = new EmptyRepo(path, name, description);
		repos.add(new TreeValueRepo(repo));
	}

}
