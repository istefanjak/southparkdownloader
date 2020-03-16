package com.istef.southpark.ui.view.bottombar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class BottomBarView {
	public interface BottomBarViewClickListener {
		void onBrowseAction(ActionEvent event);
		void onDownloadsAction(ActionEvent event);
		void onSettingsAction(ActionEvent event);
	}
	
    @FXML private Button browseButton;
    @FXML private Button downloadsButton;
    @FXML private Button settingsButton;
    
    private BottomBarViewClickListener listener;
    
    public void setListener(BottomBarViewClickListener listener) {
		this.listener = listener;
	}

	@FXML
    void onBrowseAction(ActionEvent event) {
		if (listener != null)
			listener.onBrowseAction(event);
    }

    @FXML
    void onDownloadsAction(ActionEvent event) {
    	if (listener != null)
    		listener.onDownloadsAction(event);
    }
    
    @FXML
    void onSettingsAction(ActionEvent event) {
    	if (listener != null)
    		listener.onSettingsAction(event);
    }
}