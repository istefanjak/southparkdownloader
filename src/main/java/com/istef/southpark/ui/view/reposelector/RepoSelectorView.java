package com.istef.southpark.ui.view.reposelector;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel;
import com.istef.southpark.model.json.RepoModel.Season;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.ui.view.info.episode.EpisodeInfoView;
import com.istef.southpark.ui.view.info.generic.GenericInfoView;
import com.istef.southpark.ui.view.info.repo.RepoInfoView;
import com.istef.southpark.ui.view.info.season.SeasonInfoView;
import com.istef.southpark.ui.view.reposelector.window.RepoCreatorView;
import com.istef.southpark.ui.viewmodel.EpisodeInfoViewModel;
import com.istef.southpark.ui.viewmodel.GenericInfoViewModel;
import com.istef.southpark.ui.viewmodel.RepoInfoViewModel;
import com.istef.southpark.ui.viewmodel.SeasonInfoViewModel;
import com.istef.southpark.ui.viewmodel.reposelector.RepoSelectorViewModel;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueBase;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueEpisode;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueRepo;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueSeason;
import com.istef.southpark.util.FX;
import com.istef.southpark.util.FX.TextInputDialogType;
import com.istef.southpark.util.Function;
import com.istef.southpark.util.ResolutionComboBoxConfigurer;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RepoSelectorView implements FxmlView<RepoSelectorViewModel>, Initializable {
	private static final Logger logger = LoggerFactory.getLogger(RepoSelectorView.class);
	
	private static class ContextMenuBuilder {
		@SafeVarargs
		public static ContextMenu build(TreeItem<TreeValueBase> treeItem,
				Pair<String, EventHandler<ActionEvent>>... menuItemPairs) {
			ContextMenu contextMenu = new ContextMenu();
			for (Pair<String, EventHandler<ActionEvent>> pair : menuItemPairs) {
				MenuItem mi = new MenuItem(pair.getValue0());
				mi.setOnAction(pair.getValue1());
				contextMenu.getItems().add(mi);
			}
			return contextMenu;
		}
	}

	private class RepoTreeCell extends TreeCell<TreeValueBase> {
		private static final double graphicsize = 10.;

		@Override
		protected void updateItem(TreeValueBase item, boolean empty) {
			super.updateItem(item, empty);

			graphicTextGapProperty().set(5.);
			if (empty) {
				setText(null);
				setGraphic(null);

			} else {
				setText(getItem() == null ? "" : getItem().toString());

				TreeValueBase val = getTreeItem().getValue();
				if (val instanceof TreeValueRepo) {
					setGraphic(null);
					setContextMenu(buildRepoContextMenu(getTreeItem()));

				} else if (val instanceof TreeValueSeason) {
					setGraphic(null);
					setContextMenu(buildSeasonContextMenu(getTreeItem()));

				} else if (val instanceof TreeValueEpisode) {
					setAccordingToStatus(((TreeValueEpisode) val).getStatusProperty().get());
					setContextMenu(buildEpisodeContextMenu(getTreeItem()));
				}
			}
		}

		@Override
		public void updateSelected(boolean selected) {
			super.updateSelected(selected);
		}

		private void setAccordingToStatus(int status) {
			switch (status) {
			case TreeValueEpisode.STATUS_LOADING:
				setGraphicLoading();
				break;
			case TreeValueEpisode.STATUS_ERROR:
				setGraphicError();
				break;
			case TreeValueEpisode.STATUS_SUCCESS:
				setGraphicSuccess();
				break;
			}
		}

		private void setGraphicLoading() {
			ProgressIndicator pindicator = new ProgressIndicator();
			pindicator.prefWidthProperty().set(graphicsize);
			pindicator.prefHeightProperty().set(graphicsize);
			setGraphic(pindicator);
		}

		private void setGraphicError() {
			Image errorImg = new Image(getClass().getResource("/image/error_icon.png").toExternalForm());
			ImageView errorImageView = new ImageView(errorImg);
			errorImageView.preserveRatioProperty().set(true);
			errorImageView.fitWidthProperty().set(graphicsize);
			errorImageView.fitHeightProperty().set(graphicsize);
			setGraphic(errorImageView);
		}

		private void setGraphicSuccess() {
			setGraphic(null);
		}

		private ContextMenu buildRepoContextMenu(TreeItem<TreeValueBase> treeItem) {
			TreeValueRepo val = (TreeValueRepo) treeItem.getValue();
			return ContextMenuBuilder.build(treeItem, new Pair<>("Remove", event -> viewModel.getRepos().remove(val)),
					new Pair<>("Add season", event -> {
						FX.showInputDialog(TextInputDialogType.SEASON).showAndWait().ifPresent(name -> {
							if (name.trim().equals("")) {
								FX.showErrorDialog("Season name must not be empty");
								return;
							}
							Season season = new Season(name, new ArrayList<>());
							viewModel.addToRepo(val, season);
						});
					}));
		}

		private ContextMenu buildSeasonContextMenu(TreeItem<TreeValueBase> treeItem) {
			TreeValueSeason val = (TreeValueSeason) treeItem.getValue();
			return ContextMenuBuilder.build(treeItem,
					new Pair<>("Remove", event -> val.getParent().getSeasonsObservable().remove(val)),
					new Pair<>("Add episode", event -> {
						FX.showInputDialog(TextInputDialogType.EPISODE).showAndWait().ifPresent(mgid -> {
							if (mgid.trim().equals("")) {
								FX.showErrorDialog("Season name must not be empty");
								return;
							}
							viewModel.addToRepo(val, new Mgid(mgid));
						});
					}));
		}

		private ContextMenu buildEpisodeContextMenu(TreeItem<TreeValueBase> treeItem) {
			TreeValueEpisode val = (TreeValueEpisode) treeItem.getValue();
			return ContextMenuBuilder.build(treeItem,
					new Pair<>("Remove", event -> val.getParent().getEpisodesObservable().remove(val)),
					new Pair<>("Refresh", event -> refreshTreeItemEpisode(treeItem)));
		}
	}

	private class RepoChangeListener implements ListChangeListener<TreeValueRepo> {
		private TreeItem<TreeValueBase> rootTreeItem;

		public RepoChangeListener(TreeItem<TreeValueBase> rootTreeItem) {
			super();
			this.rootTreeItem = rootTreeItem;
		}

		@Override
		public void onChanged(Change<? extends TreeValueRepo> c) {
			while (c.next()) {
				if (c.wasAdded()) {
					onAdd(c.getAddedSubList());
				}
				if (c.wasRemoved()) {
					c.getRemoved().forEach(treeValRepo -> {
						rootTreeItem.getChildren().stream().filter(treeItem -> treeItem.getValue() == treeValRepo)
								.findFirst().ifPresent(treeItem -> treeItem.getParent().getChildren().remove(treeItem));
					});
				}
			}
		}

		public void onAdd(List<? extends TreeValueRepo> list) {
			list.forEach(treeValRepo -> {
				TreeItem<TreeValueBase> treeItemRepo = new TreeItem<>(treeValRepo);
				rootTreeItem.getChildren().add(treeItemRepo);
				SeasonChangeListener listener = new SeasonChangeListener(treeItemRepo);
				treeValRepo.getSeasonsObservable().addListener(listener);
				listener.onAdd(treeValRepo.getSeasonsObservable());
			});
		}
	}

	private class SeasonChangeListener implements ListChangeListener<TreeValueSeason> {
		private TreeItem<TreeValueBase> repoTreeItem;

		public SeasonChangeListener(TreeItem<TreeValueBase> repoTreeItem) {
			super();
			this.repoTreeItem = repoTreeItem;
		}

		@Override
		public void onChanged(Change<? extends TreeValueSeason> c) {
			while (c.next()) {
				if (c.wasAdded()) {
					onAdd(c.getAddedSubList());
				}
				if (c.wasRemoved()) {
					c.getRemoved().forEach(treeValSeason -> {
						repoTreeItem.getChildren().stream().filter(treeItem -> treeItem.getValue() == treeValSeason)
								.findFirst().ifPresent(treeItem -> treeItem.getParent().getChildren().remove(treeItem));
					});
				}
			}
		}

		public void onAdd(List<? extends TreeValueSeason> list) {
			list.forEach(treeValSeason -> {
				TreeItem<TreeValueBase> treeItemSeason = new TreeItem<>(treeValSeason);
				treeItemSeason.expandedProperty().addListener((observable, oldVal, newVal) -> {
					if (newVal) {
						treeItemSeason.getChildren().forEach(treeItem -> refreshTreeItemEpisode(treeItem, false));	
					}
				});
				repoTreeItem.getChildren().add(treeItemSeason);
				EpisodeChangeListener listener = new EpisodeChangeListener(treeItemSeason);
				treeValSeason.getEpisodesObservable().addListener(listener);

				listener.onAdd(treeValSeason.getEpisodesObservable());
			});
		}
	}

	private class EpisodeChangeListener implements ListChangeListener<TreeValueEpisode> {
		private TreeItem<TreeValueBase> seasonTreeItem;

		public EpisodeChangeListener(TreeItem<TreeValueBase> seasonTreeItem) {
			super();
			this.seasonTreeItem = seasonTreeItem;
		}

		@Override
		public void onChanged(Change<? extends TreeValueEpisode> c) {
			while (c.next()) {
				if (c.wasAdded()) {
					onAdd(c.getAddedSubList());
				}
				if (c.wasRemoved()) {
					c.getRemoved().forEach(treeValEpisode -> {
						seasonTreeItem.getChildren().stream().filter(treeItem -> treeItem.getValue() == treeValEpisode)
								.findFirst().ifPresent(treeItem -> treeItem.getParent().getChildren().remove(treeItem));
					});
				}
			}
		}

		public void onAdd(List<? extends TreeValueEpisode> list) {
			list.forEach(treeValEpisode -> {
				TreeItem<TreeValueBase> treeItemEpisode = new TreeItem<>(treeValEpisode);
				seasonTreeItem.getChildren().add(treeItemEpisode);
			});
		}
	}

	@FXML
	private Button refreshButton;
	@FXML
	private Button addButton;
	@FXML
	private TreeView<TreeValueBase> repoTreeView;
	@FXML
	private VBox leftPane;
	@FXML
	private AnchorPane rightPane;

	@InjectViewModel
	private RepoSelectorViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTreeView();

		viewModel.getRepos().addListener(new RepoChangeListener(repoTreeView.getRoot()));

		viewModel.getLoadExceptionProperty().addListener(new ChangeListener<Exception>() {
			@Override
			public void changed(ObservableValue<? extends Exception> observable, Exception oldValue,
					Exception newValue) {
				if (newValue instanceof RepoLoadException) {
					FX.showErrorDialog("Error loading repo located at "
							+ ((RepoLoadException) newValue).getPath().toAbsolutePath());
				} else {
					FX.showErrorDialog("Error loading repos while traversing repo dir.");
				}
			}
		});

		loadRepos();
	}

	private void initTreeView() {
		repoTreeView.setRoot(new TreeItem<TreeValueBase>());
		repoTreeView.setShowRoot(false);

		repoTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Optional.ofNullable(repoTreeView.getSelectionModel().getSelectedItem()).ifPresent(treeItem -> {

					TreeValueBase val = treeItem.getValue();

					if (val instanceof TreeValueRepo) {
						RepoModel repoModel = ((TreeValueRepo) val).getRepoProperty().get().getRepoModel();
						setRightPaneRepoInfo(repoModel.name, repoModel.description);

					} else if (val instanceof TreeValueSeason) {
						TreeValueSeason valSeas = (TreeValueSeason) val;
						treeItem.getChildren().forEach(it -> refreshTreeItemEpisode(it, false));
						setRightPaneSeasonInfo(valSeas);

					} else if (val instanceof TreeValueEpisode) {
						TreeValueEpisode valEp = (TreeValueEpisode) val;
						int status = valEp.getStatusProperty().get();

						switch (status) {

						case TreeValueEpisode.STATUS_ERROR:
							setRightPaneGenericInfo("Error",
									"We encountered an error while requesting:\n" + valEp.getMgidProperty().get()
											+ "\n\nError message:\n" + valEp.getExceptionProperty().get().getMessage());
							break;

						case TreeValueEpisode.STATUS_LOADING:
							setRightPaneGenericInfo("Still loading...",
									"Repo item is still loading. Please wait.\n\nTaking too long? Check your internet connection");
							break;

						case TreeValueEpisode.STATUS_SUCCESS:
							setRightPaneEpisodeInfo(valEp.getMgidProperty().get(), valEp.getMgidParserModelProperty().get(),
									valEp.getResolutionsObservable());
							break;

						}
					}

				});
			}
		});
		repoTreeView.setCellFactory(value -> new RepoTreeCell());
	}

	private void loadRepos() {
		viewModel.loadRepos();
	}

	@FXML
	void onRefreshAction(ActionEvent event) {
		loadRepos();
	}

	@FXML
	void onAddAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("window/RepoCreatorView.fxml"));
		Parent root = loader.load();
		RepoCreatorView controller = loader.getController();
		controller.init((path, name, desc) -> {
			try {
				viewModel.createRepo(path, name, desc);
				FX.showInformationDialog("Successfully created repo in\n" + path);

			} catch (RepoLoadException e) {
				logger.error(e.getMessage(), e);
				FX.showErrorDialog("Error creating repo");
			}
		});
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Repo creator");
		stage.show();
	}

	private void setRightPaneRepoInfo(String name, String description) {
		ViewTuple<RepoInfoView, RepoInfoViewModel> viewTuple = FluentViewLoader.fxmlView(RepoInfoView.class).load();
		viewTuple.getViewModel().setName(name);
		viewTuple.getViewModel().setDescription(description);
		rightPane.getChildren().setAll(viewTuple.getView());
		Function.setZeroAnchor(viewTuple.getView());
	}

	private void setRightPaneSeasonInfo(TreeValueSeason treeValSeason) {
		ViewTuple<SeasonInfoView, SeasonInfoViewModel> viewTuple = FluentViewLoader.fxmlView(SeasonInfoView.class)
				.load();
		viewTuple.getViewModel().init(treeValSeason);
		viewTuple.getCodeBehind().init();
		rightPane.getChildren().setAll(viewTuple.getView());
		Function.setZeroAnchor(viewTuple.getView());
	}

	private void setRightPaneEpisodeInfo(Mgid mgid, MgidParserModel mgidParserModel, List<Resolution> resolutions) {
		ViewTuple<EpisodeInfoView, EpisodeInfoViewModel> viewTuple = FluentViewLoader.fxmlView(EpisodeInfoView.class)
				.load();
		viewTuple.getViewModel().init(mgid, mgidParserModel);
		viewTuple.getViewModel().setResolutions(resolutions);
		rightPane.getChildren().setAll(viewTuple.getView());
		Function.setZeroAnchor(viewTuple.getView());
	}

	private void setRightPaneGenericInfo(String title, String desc) {
		ViewTuple<GenericInfoView, GenericInfoViewModel> viewTuple = FluentViewLoader.fxmlView(GenericInfoView.class)
				.load();
		viewTuple.getViewModel().setTitle(title);
		viewTuple.getViewModel().setDescription(desc);
		rightPane.getChildren().setAll(viewTuple.getView());
		Function.setZeroAnchor(viewTuple.getView());
	}

	private void refreshTreeItemEpisode(final TreeItem<TreeValueBase> treeItem, boolean refreshLoaded) {
		if (treeItem.getValue() instanceof TreeValueEpisode) {
			TreeValueEpisode val = (TreeValueEpisode) treeItem.getValue();
			if (!refreshLoaded && val.getStatusProperty().get() == TreeValueEpisode.STATUS_SUCCESS) {
				return;
			}
			
			viewModel.getEpisodeInfoAsync(val.getMgidProperty().get(), (model, resolutions) -> {
				resolutions.sort(ResolutionComboBoxConfigurer.COMPARATOR);
				val.setParams(model, resolutions);
				repoTreeView.refresh();
			}, (exception) -> {
				val.setParams(null, new ArrayList<>());
				((TreeValueEpisode) treeItem.getValue()).setException(exception);
				repoTreeView.refresh();
			});
		}
	}
	
	private void refreshTreeItemEpisode(final TreeItem<TreeValueBase> treeItem) {
		refreshTreeItemEpisode(treeItem, true);
	}
}