package com.istef.southpark.ui.model;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import com.istef.southpark.model.Episode;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.requests.M3U8Parser.Centile;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.util.Function;

import io.lindstrom.m3u8.model.Resolution;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class DownloadItem {
	public static final String STATE_CANCELED = "Canceled";
	public static final String STATE_DOWNLOADING = "Downloading";
	public static final String STATE_FINISHED_SUCCESSFULLY = "Downloaded successfully";
	public static final String STATE_SUBTITLES_PARSED = "Subtitles parsed";
	public static final String STATE_PLAYLIST_PARSED = "Playlist parsed";
	
	public static final Integer EPISODE_IMAGE_W = 64;
	public static final Integer EPISODE_IMAGE_H = Function.calculate169Height(EPISODE_IMAGE_W);
	
	private UUID taskId;
	private Mgid mgid;
	private MgidParserModel mgidParserModel;
	private List<Resolution> resolutions;
	private ObjectProperty<Resolution> resolutionProperty;
	private Image image;
	private Path path;
	private ObjectProperty<Centile> progressProperty;
	private StringProperty statusProperty;
	private ObjectProperty<Exception> exceptionProperty;
	
	public DownloadItem(Mgid mgid, MgidParserModel mgidParserModel, List<Resolution> resolutions, Resolution resolution, Path path) {
		this.mgid = mgid;
		this.mgidParserModel = mgidParserModel;
		this.resolutions = resolutions;
		this.resolutionProperty = new SimpleObjectProperty<>(resolution);
		this.path = path;
		loadImage();
		progressProperty = new SimpleObjectProperty<>(new Centile(0.));
		statusProperty = new SimpleStringProperty();
		exceptionProperty = new SimpleObjectProperty<>();
	}
	
	public void setTaskId(UUID taskId) {
		this.taskId = taskId;
	}

	public void loadImage() {
		image = new Image(mgidParserModel.getEpisode().getImageUri(EPISODE_IMAGE_W, EPISODE_IMAGE_H), true);
	}
	
	public UUID getTaskId() {
		return taskId;
	}

	public Mgid getMgid() {
		return mgid;
	}

	public MgidParserModel getMgidParserModel() {
		return mgidParserModel;
	}

	public List<Resolution> getResolutions() {
		return resolutions;
	}
	
	public ObjectProperty<Resolution> getResolutionProperty() {
		return resolutionProperty;
	}

	public void setResolution(Resolution resolution) {
		resolutionProperty.set(resolution);
	}

	public Episode getEpisode() {
		return mgidParserModel.getEpisode();
	}
	
	public Path getPath() {
		return path;
	}

	public ObjectProperty<Centile> getProgressProperty() {
		return progressProperty;
	}
	
	public StringProperty getStatusProperty() {
		return statusProperty;
	}
	
	public ObjectProperty<Exception> getExceptionProperty() {
		return exceptionProperty;
	}
	
	public void setException(Exception e) {
		this.exceptionProperty.set(e);
	}

	public Image getImage() {
		return image;
	}

	public void setProgress(Centile progress) {
		this.progressProperty.set(progress);
	}
	
	public void setStatus(String status) {
		statusProperty.set(status);
	}

	@Override
	public String toString() {
		return "DownloadItem [mgid=" + mgid + ", mgidParserModel=" + mgidParserModel + ", resolutions=" + resolutions
				+ ", image=" + image + ", path=" + path + ", progressProperty=" + progressProperty + ", statusProperty="
				+ statusProperty + ", exceptionProperty=" + exceptionProperty + "]";
	}
	
}
