package com.istef.southpark.ui.view.shellscene;

import java.net.URL;
import java.util.ResourceBundle;

import com.istef.southpark.ui.view.bottombar.BottomBarView;
import com.istef.southpark.ui.view.bottombar.BottomBarView.BottomBarViewClickListener;
import com.istef.southpark.ui.view.download.DownloadView;
import com.istef.southpark.ui.view.menubar.MenuBarView;
import com.istef.southpark.ui.view.reposelector.RepoSelectorView;
import com.istef.southpark.ui.view.settings.SettingsView;
import com.istef.southpark.ui.viewmodel.DownloadViewModel;
import com.istef.southpark.ui.viewmodel.SettingsViewModel;
import com.istef.southpark.ui.viewmodel.ShellSceneViewModel;
import com.istef.southpark.ui.viewmodel.reposelector.RepoSelectorViewModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ShellSceneView implements FxmlView<ShellSceneViewModel>, Initializable, BottomBarViewClickListener {
    @FXML private MenuBar menu;
    @FXML private HBox bottomBar;
    @FXML private MenuBarView menuController;
    @FXML private BottomBarView bottomBarController;
    @FXML private BorderPane root;
    
    @InjectViewModel
    private ShellSceneViewModel viewModel;
    
    private ViewTuple<RepoSelectorView, RepoSelectorViewModel> viewTupleBrowse;
    private ViewTuple<DownloadView, DownloadViewModel> viewTupleDownload;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bottomBarController.setListener(this);
		viewTupleBrowse = FluentViewLoader.fxmlView(RepoSelectorView.class).load();
		viewTupleDownload = FluentViewLoader.fxmlView(DownloadView.class).load();
		loadBrowseView();
	}

	@Override
	public void onBrowseAction(ActionEvent event) {
		loadBrowseView();
	}

	@Override
	public void onDownloadsAction(ActionEvent event) {
		loadDownloadView();
	}

	@Override
	public void onSettingsAction(ActionEvent event) {
		loadSettingsView();
	}
	
	private void loadBrowseView() {
		root.setCenter(viewTupleBrowse.getView());
	}
	
	private void loadDownloadView() {
		root.setCenter(viewTupleDownload.getView());
	}

	private void loadSettingsView() {
		ViewTuple<SettingsView, SettingsViewModel> viewTupleSettings = FluentViewLoader.fxmlView(SettingsView.class).load();
		root.setCenter(viewTupleSettings.getView());
	}
	
}
