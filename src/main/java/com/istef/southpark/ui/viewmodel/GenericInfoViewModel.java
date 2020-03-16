package com.istef.southpark.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GenericInfoViewModel implements ViewModel {
	private StringProperty titleProperty = new SimpleStringProperty();
	private StringProperty descriptionProperty = new SimpleStringProperty();
	
	public StringProperty getTitleProperty() {
		return titleProperty;
	}
	public StringProperty getDescriptionProperty() {
		return descriptionProperty;
	}
	
	public void setTitle(String title) {
		titleProperty.set(title);
	}
	
	public void setDescription(String description) {
		descriptionProperty.set(description);
	}
}
