package com.istef.southpark.ui.view.info.season;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.istef.southpark.ui.viewmodel.SeasonInfoViewModel;
import com.istef.southpark.ui.viewmodel.reposelector.TreeValueEpisode;
import com.istef.southpark.util.BindingUtil;
import com.istef.southpark.util.ResolutionComboBoxConfigurer;
import com.istef.southpark.util.converter.MgidParserModelToStringConverter;
import com.istef.southpark.util.converter.MgidToStringConverter;
import com.istef.southpark.util.converter.StatusPropertyToStringConverter;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SeasonInfoView implements FxmlView<SeasonInfoViewModel>, Initializable {
	public static class TreeValueEpisodeWrapper {
		private TreeValueEpisode treeValueEpisode;
		private ComboBox<Resolution> comboBox;

		public TreeValueEpisodeWrapper(TreeValueEpisode treeValueEpisode) {
			this.treeValueEpisode = treeValueEpisode;
			comboBox = new ComboBox<>();
			ResolutionComboBoxConfigurer.configure(comboBox, treeValueEpisode.getResolutionsObservable());
		}

		public TreeValueEpisode getTreeValueEpisode() {
			return treeValueEpisode;
		}

		public ComboBox<Resolution> getComboBox() {
			return comboBox;
		}
	}

	@FXML
	private Label seasonNameLabel;

	@FXML
	private TableView<TreeValueEpisodeWrapper> loadedTableView;

	@FXML
	private TableColumn<TreeValueEpisodeWrapper, String> episodeCol;

	@FXML
	private TableColumn<TreeValueEpisodeWrapper, ComboBox<Resolution>> resolutionCol;

	@FXML
	private TableView<TreeValueEpisode> nonLoadedTableView;

	@FXML
	private TableColumn<TreeValueEpisode, String> itemCol;

	@FXML
	private TableColumn<TreeValueEpisode, String> statusCol;

	@FXML
	private Button downloadButton;

	@InjectViewModel
	private SeasonInfoViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void init() {
		seasonNameLabel.textProperty().bind(viewModel.getNameProperty());
		episodeCol.setCellValueFactory(data -> {
			StringProperty sprop = new SimpleStringProperty();
			sprop.bindBidirectional(data.getValue().getTreeValueEpisode().getMgidParserModelProperty(),
					new MgidParserModelToStringConverter());
			return sprop;
		});

		resolutionCol.setCellValueFactory(new PropertyValueFactory<>("comboBox"));

		itemCol.setCellValueFactory(data -> {
			StringProperty sprop = new SimpleStringProperty();
			sprop.bindBidirectional(data.getValue().getMgidProperty(), new MgidToStringConverter());
			return sprop;
		});
		statusCol.setCellValueFactory(data -> {
			StringProperty sprop = new SimpleStringProperty();
			sprop.bindBidirectional(data.getValue().getStatusProperty(), new StatusPropertyToStringConverter());
			return sprop;
		});

		ObservableList<TreeValueEpisodeWrapper> loadedFiltered = FXCollections.observableArrayList();
		BindingUtil.mapContent(loadedFiltered, viewModel.getLoadedFiltered(), m -> new TreeValueEpisodeWrapper(m));

		loadedTableView.setItems(loadedFiltered);
		nonLoadedTableView.setItems(viewModel.getOtherFiltered());
	}

	@FXML
	void onDownloadAction(ActionEvent event) {
		viewModel.addToDownloads(loadedTableView.getItems().stream().map(
				row -> new Pair<TreeValueEpisode, Resolution>(row.getTreeValueEpisode(), row.getComboBox().getValue()))
				.collect(Collectors.toList()));
	}

}
