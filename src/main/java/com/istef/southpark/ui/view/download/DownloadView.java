package com.istef.southpark.ui.view.download;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.requests.M3U8Parser.Centile;
import com.istef.southpark.ui.element.ProgressIndicatorBar;
import com.istef.southpark.ui.model.DownloadItem;
import com.istef.southpark.ui.view.download.windows.RedownloadView;
import com.istef.southpark.ui.viewmodel.DownloadViewModel;
import com.istef.southpark.ui.viewmodel.RedownloadViewModel;
import com.istef.southpark.util.FX;
import com.istef.southpark.util.converter.ResolutionToStringConverter;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DownloadView implements FxmlView<DownloadViewModel>, Initializable {
	private static final Logger logger = LoggerFactory.getLogger(DownloadView.class);
	
	public static class DownloadItemCellItem {
		private ImageView imageView;
		private ProgressIndicatorBar progressBar;
		private String name;
		private DownloadItem downloadItem;

		public DownloadItemCellItem(DownloadItem item) {
			downloadItem = item;
			imageView = new ImageView(item.getImage());
			progressBar = new ProgressIndicatorBar("%.2f%%");
			name = item.getEpisode().getEpisodeFileName();

			item.getProgressProperty().addListener(new ChangeListener<Centile>() {
				@Override
				public void changed(ObservableValue<? extends Centile> observable, Centile oldValue, Centile newValue) {
					progressBar.setProgress(newValue.get());
				}
			});
		}

		public String getName() {
			return name;
		}

		public ImageView getImageView() {
			return imageView;
		}

		public ProgressIndicatorBar getProgressBar() {
			return progressBar;
		}

		public DownloadItem getDownloadItem() {
			return downloadItem;
		}

	}

	@FXML
	private TableView<DownloadItemCellItem> downloadTableView;

	@FXML
	private TableColumn<DownloadItemCellItem, ImageView> imageTableCol;

	@FXML
	private TableColumn<DownloadItemCellItem, String> nameTableCol;

	@FXML
	private TableColumn<DownloadItemCellItem, String> resolutionTableCol;

	@FXML
	private TableColumn<DownloadItemCellItem, String> statusTableCol;

	@FXML
	private TableColumn<DownloadItemCellItem, ProgressIndicatorBar> progressTableCol;

	@InjectViewModel
	private DownloadViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		imageTableCol.setCellValueFactory(new PropertyValueFactory<>("imageView"));
		nameTableCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		resolutionTableCol.setCellValueFactory(data -> {
			StringProperty stringResolutionProperty = new SimpleStringProperty();
			stringResolutionProperty.bindBidirectional(data.getValue().getDownloadItem().getResolutionProperty(),
					new ResolutionToStringConverter());
			return stringResolutionProperty;
		});
		statusTableCol.setCellValueFactory(data -> data.getValue().getDownloadItem().getStatusProperty());
		progressTableCol.setCellValueFactory(new PropertyValueFactory<>("progressBar"));

		downloadTableView.setItems(viewModel.getDownloadItems());
		downloadTableView.setRowFactory(tv -> {
			TableRow<DownloadItemCellItem> row = new TableRow<>();
			ContextMenu menu = buildContextMenu();

			row.emptyProperty()
					.addListener((obs, wasEmpty, isNowEmpty) -> row.setContextMenu(isNowEmpty ? null : menu));

			return row;
		});
	}

	private ContextMenu buildContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem openFileExplorer = new MenuItem("Open in file explorer");
		openFileExplorer.setOnAction(event -> {
			DownloadItemCellItem item = downloadTableView.getSelectionModel().getSelectedItem();
			if (item != null) {
				try {
					Desktop.getDesktop().open(item.getDownloadItem().getPath().toFile());

				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					FX.showErrorDialog("Error opening file explorer");
				}
			}
		});
		contextMenu.getItems().add(openFileExplorer);

		MenuItem redownload = new MenuItem("Redownload");
		redownload.setOnAction(event -> {
			DownloadItemCellItem item = downloadTableView.getSelectionModel().getSelectedItem();
			if (item != null) {
				ViewTuple<RedownloadView, RedownloadViewModel> viewTuple = FluentViewLoader
						.fxmlView(RedownloadView.class).load();

				viewTuple.getViewModel().init(item.getDownloadItem().getResolutions());
				viewTuple.getCodeBehind().init(cancel -> {
				}, ok -> {
					Resolution pick = viewTuple.getCodeBehind().getResolutionComboBox().getValue().getResolution();

					try {
						viewModel.restartDownloadItem(item.getDownloadItem(), pick);

					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						FX.showErrorDialog("Error restarting download");
					}
				});
				Scene scene = new Scene(viewTuple.getView());
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setResizable(false);
				stage.setTitle("Resolution picker");
				stage.show();
			}
		});
		contextMenu.getItems().add(redownload);

		MenuItem cancel = new MenuItem("Cancel");
		cancel.setOnAction(event -> {
			DownloadItemCellItem item = downloadTableView.getSelectionModel().getSelectedItem();
			if (item != null) {
				viewModel.cancelDownloadItem(item.getDownloadItem());
			}
		});
		contextMenu.getItems().add(cancel);
		
		return contextMenu;
	}

}
