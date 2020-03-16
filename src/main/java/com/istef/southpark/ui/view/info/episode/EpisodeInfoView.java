package com.istef.southpark.ui.view.info.episode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.istef.southpark.Const;
import com.istef.southpark.ui.SettingsCheck;
import com.istef.southpark.ui.model.DownloadItem;
import com.istef.southpark.ui.viewmodel.EpisodeInfoViewModel;
import com.istef.southpark.ui.viewmodel.EpisodeInfoViewModel.ResolutionWrapper;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class EpisodeInfoView implements FxmlView<EpisodeInfoViewModel>, Initializable {

	@FXML
	private ImageView episodeImageView;
	@FXML
	private Label titleLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Label seasonLabel;
	@FXML
	private Label episodeLabel;
	@FXML
	private Label pubDateLabel;
	@FXML
	private Label durationLabel;
	@FXML
	private Button downloadButton;
	@FXML
	private ComboBox<ResolutionWrapper> resolutionComboBox;

	@InjectViewModel
	private EpisodeInfoViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleLabel.textProperty().bind(viewModel.getTitleProperty());
		descriptionLabel.textProperty().bind(viewModel.getDescriptionProperty());
		seasonLabel.textProperty().bind(viewModel.getSeasonProperty());
		episodeLabel.textProperty().bind(viewModel.getEpisodeProperty());
		pubDateLabel.textProperty().bind(viewModel.getPubDateProperty());
		durationLabel.textProperty().bind(viewModel.getDurationProperty());

		episodeImageView.setFitWidth(EpisodeInfoViewModel.EPISODE_IMAGE_W);
		episodeImageView.setFitHeight(EpisodeInfoViewModel.EPISODE_IMAGE_H);
		episodeImageView.imageProperty().bind(viewModel.getImageProperty());

		resolutionComboBox.setItems(viewModel.getResolutionsObservable());

		viewModel.getResolutionsObservable().addListener(new ListChangeListener<ResolutionWrapper>() {
			@Override
			public void onChanged(Change<? extends ResolutionWrapper> c) {
				if (c.getList().size() > 0) {
					resolutionComboBox.setValue(c.getList().get(0));
				}
			}
		});
	}

	@FXML
	void onDownloadAction(ActionEvent event) {
		if(!SettingsCheck.areDirsValid()) return;
		
		viewModel.addToDownloads(new DownloadItem(
				viewModel.getMgid(), viewModel.getMgidParserModel(), viewModel.getResolutionsObservable().stream()
						.map(val -> val.getResolution()).collect(Collectors.toList()),
				resolutionComboBox.getValue().getResolution(), Const.DL_DIR));
	}
}
