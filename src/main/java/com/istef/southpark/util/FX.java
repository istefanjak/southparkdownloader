package com.istef.southpark.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;

public class FX {
	public enum TextInputDialogType {
		REPO, SEASON, EPISODE
	}
	
	public static void showErrorDialog(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	public static void showInformationDialog(String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	public static TextInputDialog showInputDialog(TextInputDialogType type) {
		TextInputDialog dialog = null;
		switch (type) {
		
		case REPO:
			break;
			
		case SEASON:
			dialog = new TextInputDialog("Season");
			dialog.setTitle("Season name input");
			dialog.setHeaderText(null);
			dialog.setContentText("Enter season name:");
			return dialog;
			
		case EPISODE:
			dialog = new TextInputDialog("mgid");
			dialog.setTitle("Episode mgid input");
			dialog.setHeaderText(null);
			dialog.setContentText("Enter episode mgid:");
			return dialog;
			
		}
		return null;
	}
}
