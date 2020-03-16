package com.istef.southpark.ui.view.download.windows;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.istef.southpark.ui.viewmodel.EpisodeInfoViewModel.ResolutionWrapper;
import com.istef.southpark.ui.viewmodel.RedownloadViewModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class RedownloadView implements FxmlView<RedownloadViewModel>, Initializable {
	
    @FXML
    private ComboBox<ResolutionWrapper> resolutionComboBox;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
	
	@InjectViewModel
	private RedownloadViewModel viewModel;
	
	private Consumer<ActionEvent> onCancelConsumer;
	private Consumer<ActionEvent> onOkConsumer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resolutionComboBox.setItems(viewModel.getResolutionsObservable());
		
		viewModel.getResolutionsObservable().addListener(new ListChangeListener<ResolutionWrapper>() {
			@Override
			public void onChanged(Change<? extends ResolutionWrapper> c) {
				if (!c.getList().isEmpty()) {
					resolutionComboBox.setValue(c.getList().get(0));
				}
			}
		});
	}
	
	public void init(Consumer<ActionEvent> onCancelConsumer, Consumer<ActionEvent> onOkConsumer) {
		this.onCancelConsumer = onCancelConsumer;
		this.onOkConsumer = onOkConsumer;
	}

    public ComboBox<ResolutionWrapper> getResolutionComboBox() {
		return resolutionComboBox;
	}

	@FXML
    void onCancelAction(ActionEvent event) {
    	onCancelConsumer.accept(event);
    	((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    void onOkAction(ActionEvent event) {
    	onOkConsumer.accept(event);
    	((Stage) cancelButton.getScene().getWindow()).close();
    }
}
