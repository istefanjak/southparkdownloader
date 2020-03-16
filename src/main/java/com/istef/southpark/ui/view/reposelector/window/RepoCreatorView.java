package com.istef.southpark.ui.view.reposelector.window;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import com.istef.southpark.Const;
import com.istef.southpark.util.FilesHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RepoCreatorView implements Initializable {
	public interface OnOkConsumer {
		void accept(Path path, String name, String description);
	}

	@FXML
	private TextField nameInputField;

	@FXML
	private TextField descriptionInputField;

	@FXML
	private Button okButton;

	@FXML
	private Button cancelButton;

	private OnOkConsumer onOkConsumer;

	@FXML
	private Label errorLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorLabel.setVisible(false);
		nameInputField.textProperty().addListener((observable, oldVal, newVal) -> {
			errorLabel.setVisible(false);
			
		});
	}
	
	public void init(OnOkConsumer onOkConsumer) {
		this.onOkConsumer = onOkConsumer;
	}

	@FXML
	void onCancelAction(ActionEvent event) {
		close(event);
	}

	private void close(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	@FXML
	void onOkAction(ActionEvent event) {
		String repoFileName = FilesHandler.findAppropriateFileName(Paths.get(Const.REPO_DIR), "repo", ".json");
		Path repoPath = Paths.get(Const.REPO_DIR, repoFileName);
		String repoName = nameInputField.getText().trim();
		String repoDescription = descriptionInputField.getText().trim();
		if (repoName.equals("")) {
			errorLabel.setVisible(true);
			return;
		}
		if (onOkConsumer != null) {
			onOkConsumer.accept(repoPath, repoName, repoDescription);
			close(event);
		}
	}

}
