package com.istef.southpark.ui.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.Const;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;
import com.istef.southpark.model.listener.CentileListener;
import com.istef.southpark.model.listener.GetInfoListener;
import com.istef.southpark.model.listener.GetVideoListener;
import com.istef.southpark.model.thread.DownloadMonitor;
import com.istef.southpark.model.thread.TaskStruct;
import com.istef.southpark.requests.M3U8Parser.Centile;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.util.Builder;
import com.istef.southpark.util.FilesHandler;

import io.lindstrom.m3u8.model.Resolution;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

// Singleton
public class ServicesModel {
	private static final Logger logger = LoggerFactory.getLogger(ServicesModel.class);
	
	private static ServicesModel instance;

	private List<TaskStruct> runningTasks;
	private ExecutorService executorService;
	private DownloadMonitor downloadMonitor;
	private CloseableHttpClient client;

	private ServicesModel() {
		runningTasks = new ArrayList<>();
		executorService = Executors.newFixedThreadPool(4);
		downloadMonitor = new DownloadMonitor(runningTasks, executorService);
		client = Builder.getCloseableHttpClient();
	}

	synchronized public static ServicesModel getInstance() {
		if (instance == null) {
			instance = new ServicesModel();
		}
		return instance;
	}

	public UUID getEpisodeInfoAsync(Mgid mgid, BiConsumer<MgidParserModel, List<Resolution>> onComplete, Consumer<Exception> onException) {
		return downloadMonitor.submitTaskGetEpisodeInfo(client, mgid.mgid, new GetInfoListener() {

			@Override
			public void onMgidParserEnd(Throwable t) {
				if (t != null) {
					//Platform.runLater(() -> onComplete.accept(null, null));
					if (t instanceof Exception)
						Platform.runLater(() -> onException.accept((Exception)t));
				}
			}

			@Override
			public void onM3U8ParserEnd_1(List<Resolution> resolutions, MgidParserModel model, Throwable t) {
				Platform.runLater(() -> onComplete.accept(model, resolutions));
				if (t != null && t instanceof Exception)
					Platform.runLater(() -> onException.accept((Exception)t));
			}
		});
	}

	public UUID downloadEpisode(Mgid mgid, MgidParserModel mgidParserModel, Path downloadDir, Resolution resolution,
			StringProperty statusProperty, ObjectProperty<Exception> exceptionProperty,
			ObjectProperty<Centile> progressProperty) {

		return downloadMonitor.submitTaskGetVideo(client, mgid.mgid, mgidParserModel, resolution, downloadDir,
				new GetVideoListener() {

					@Override
					public void onStart() {
						Platform.runLater(() -> {
							statusProperty.set(DownloadItem.STATE_DOWNLOADING);
							exceptionProperty.set(null);
						});
					}

					@Override
					public void onVideoParserEnd(Throwable t) {
						Platform.runLater(() -> {
							if (t == null) {
								statusProperty.set(DownloadItem.STATE_FINISHED_SUCCESSFULLY);
							} else {
								statusProperty.set("Error: video conversion error");
								if (t instanceof Exception) {
									exceptionProperty.set((Exception) t);
								}
							}
						});
					}

					@Override
					public void onSubtitleParserEnd(Throwable t) {
						Platform.runLater(() -> {
							if (t == null) {
								statusProperty.set(DownloadItem.STATE_SUBTITLES_PARSED);
							} else {
								statusProperty.set("Error: subtitle parse error");
								if (t instanceof Exception) {
									exceptionProperty.set((Exception) t);
								}
							}
						});
					}

					@Override
					public void onM3U8ParserEnd_2(Throwable t) {
						Platform.runLater(() -> {
							if (t == null) {
								statusProperty.set(DownloadItem.STATE_PLAYLIST_PARSED);
							} else {
								statusProperty.set("Error: playlist parse error");
								if (t instanceof Exception) {
									exceptionProperty.set((Exception) t);
								}
							}
						});
					}
				}, new CentileListener() {

					@Override
					public void onProgressChanged(Centile centile) {
						Platform.runLater(() -> progressProperty.set(centile));
					}
				});
	}

	public boolean cancelTask(UUID taskId) {
		synchronized (runningTasks) {
			TaskStruct toRemove = null;
			for (TaskStruct task : runningTasks) {
				if (task.id.equals(taskId)) {
					toRemove = task;
					if (task.cancelable != null) {
						task.cancelable.cancel();
					}
					break;
				}
			}
			if (toRemove != null) {
				runningTasks.remove(toRemove);
				FilesHandler.deleteUuidFromTmpDir(Paths.get(Const.TMP_DIR), taskId);
				return true;
			}
		}
		return false;
	}

	public void cancelAllTasks() {
		synchronized (runningTasks) {
			for (TaskStruct task : runningTasks) {
				if (task.cancelable != null) {
					task.cancelable.cancel();
					FilesHandler.deleteUuidFromTmpDir(Paths.get(Const.TMP_DIR), task.id);
				}
			}
			runningTasks.clear();
		}
	}

	public void close() {
		cancelAllTasks();
		executorService.shutdown();
		try {
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
		        executorService.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executorService.shutdownNow();
		}
		try {
			client.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
