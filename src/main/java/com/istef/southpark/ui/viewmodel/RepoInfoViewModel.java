package com.istef.southpark.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepoInfoViewModel implements ViewModel {
	private StringProperty nameProperty = new SimpleStringProperty();
	private StringProperty descriptionProperty = new SimpleStringProperty();
	
	public StringProperty getNameProperty() {
		return nameProperty;
	}

	public StringProperty getDescriptionProperty() {
		return descriptionProperty;
	}

	public void setName(String value) {
		nameProperty.set(value);
	}
	
	public void setDescription(String value) {
		descriptionProperty.set(value);
	}
}
