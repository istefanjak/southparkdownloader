package com.istef.southpark.ui.view.info.repo;

import java.net.URL;
import java.util.ResourceBundle;

import com.istef.southpark.ui.viewmodel.RepoInfoViewModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class RepoInfoView implements FxmlView<RepoInfoViewModel>, Initializable {
	
    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;
	
    @InjectViewModel
    private RepoInfoViewModel viewModel;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameLabel.textProperty().bind(viewModel.getNameProperty());
		descriptionLabel.textProperty().bind(viewModel.getDescriptionProperty());
	}

}
