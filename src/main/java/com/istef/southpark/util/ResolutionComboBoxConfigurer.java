package com.istef.southpark.util;

import java.util.Comparator;

import io.lindstrom.m3u8.model.Resolution;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ResolutionComboBoxConfigurer {
	public static final Comparator<Resolution> COMPARATOR = (Resolution res1, Resolution res2) -> -Integer.valueOf(res1.width() * res1.height())
			.compareTo(Integer.valueOf(res2.width() * res2.height()));
	
	public static final Callback<ListView<Resolution>, ListCell<Resolution>> CALLBACK = l -> new ListCell<Resolution>() {

		@Override
		protected void updateItem(Resolution res, boolean empty) {
			super.updateItem(res, empty);
			
			if (res == null || empty) {
				setGraphic(null);
			} else {
				setText(res.width() + "x" + res.height());
			}
		}
		
	};
	
	public static void configure(ComboBox<Resolution> comboBox, ObservableList<Resolution> resolutions) {
		comboBox.minWidthProperty().set(100.);
		comboBox.maxWidthProperty().set(100.);
		comboBox.setButtonCell(CALLBACK.call(null));
		comboBox.setCellFactory(CALLBACK);
		
		comboBox.setItems(resolutions);
		
		if (!resolutions.isEmpty()) {
			comboBox.setDisable(false);
			comboBox.valueProperty().set(resolutions.get(0));
		} else {
			comboBox.setDisable(true);
		}
		
		resolutions.addListener((Change<? extends Resolution> c) -> {
			if (!c.getList().isEmpty()) {
				comboBox.setDisable(false);
				comboBox.valueProperty().set(c.getList().get(0));
			} else {
				comboBox.setDisable(true);
			}
		});
	}
}
