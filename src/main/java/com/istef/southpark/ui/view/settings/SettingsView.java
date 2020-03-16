package com.istef.southpark.ui.view.settings;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.Const;
import com.istef.southpark.ui.SettingsCheck;
import com.istef.southpark.ui.viewmodel.SettingsViewModel;
import com.istef.southpark.util.FX;
import com.istef.southpark.util.PropertiesHandler;
import com.istef.southpark.util.PropertiesHandler.PropertiesVals;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class SettingsView implements FxmlView<SettingsViewModel>, Initializable {
	private static final Logger logger = LoggerFactory.getLogger(SettingsView.class);
	
	@FXML
	private TextField dlTextField;

	@FXML
	private Button browseDlButton;

	@FXML
	private TextField mkvMergeTextField;

	@FXML
	private Button browseMkvMergeButton;

	@FXML
	private Button saveButton;

	@InjectViewModel
	private SettingsViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dlTextField.setText(Const.DL_DIR == null ? "" : Const.DL_DIR.toString());
		mkvMergeTextField.setText(Const.MKVMERGE == null ? "" : Const.MKVMERGE.toString());
	}

	@FXML
	void onBrowseDlAction(ActionEvent event) {
		Node source = (Node) event.getSource();
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDir = dirChooser.showDialog(source.getScene().getWindow());
		if (selectedDir != null) {
			dlTextField.setText(selectedDir.getAbsolutePath());
		}
	}

	@FXML
	void onBrowseMkvMergeAction(ActionEvent event) {
		Node source = (Node) event.getSource();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MKVMerge executable", "*"));
		File selectedDir = fileChooser.showOpenDialog(source.getScene().getWindow());
		if (selectedDir != null) {
			mkvMergeTextField.setText(selectedDir.getAbsolutePath());
		}
	}

	@FXML
	void onSaveAction(ActionEvent event) {
		boolean check = SettingsCheck.areDirsValid(dlTextField.getText(), mkvMergeTextField.getText());
		if (!check) {
			return;
		}
		String dlText = dlTextField.getText().trim();
		String mkvMergeText = mkvMergeTextField.getText().trim();
		Const.DL_DIR = dlText.equals("") ? null : Paths.get(dlText);
		Const.MKVMERGE = mkvMergeText.equals("") ? null : Paths.get(mkvMergeText);

		try {
			PropertiesHandler.setProperty(PropertiesVals.DL_DIR, Const.DL_DIR == null ? null : Const.DL_DIR.toString());
			PropertiesHandler.setProperty(PropertiesVals.MKV_MERGE_EXE,
					Const.MKVMERGE == null ? null : Const.MKVMERGE.toString());
			FX.showInformationDialog("Settings saved successfully!");
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FX.showErrorDialog("Error saving settings to property file");
		}
	}
}
