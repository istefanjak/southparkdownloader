package com.istef.southpark.ui.view.info.generic;

import java.net.URL;
import java.util.ResourceBundle;

import com.istef.southpark.ui.viewmodel.GenericInfoViewModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class GenericInfoView implements FxmlView<GenericInfoViewModel>, Initializable {
	
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    
	@InjectViewModel
	private GenericInfoViewModel viewModel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleLabel.textProperty().bind(viewModel.getTitleProperty());
		descriptionLabel.textProperty().bind(viewModel.getDescriptionProperty());
	}

}
