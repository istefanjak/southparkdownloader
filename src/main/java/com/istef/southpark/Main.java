package com.istef.southpark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.ui.model.ServicesModel;
import com.istef.southpark.ui.view.shellscene.ShellSceneView;
import com.istef.southpark.ui.viewmodel.ShellSceneViewModel;
import com.istef.southpark.util.FX;
import com.istef.southpark.util.PropertiesHandler;
import com.istef.southpark.util.PropertiesHandler.MyProperties;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	private static final String TITLE = "South park downloader";

	@Override
	public void start(Stage stage) {
		try {
			loadProperties();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FX.showErrorDialog("Error creating/ reading properties file.\nExiting...");
			return;
		}

		ViewTuple<ShellSceneView, ShellSceneViewModel> viewTuple = FluentViewLoader.fxmlView(ShellSceneView.class)
				.load();

		Parent root = viewTuple.getView();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(640);
		stage.setMinHeight(480);
		stage.setWidth(800);
		stage.setHeight(520);
		stage.setTitle(TITLE);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		ServicesModel.getInstance().close();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void loadProperties() throws Exception {
		MyProperties props = PropertiesHandler.getProperties();
		Const.DL_DIR = props.getDownloadDir();
		Const.MKVMERGE = props.getMkvMergeExecutable();
	}
}
