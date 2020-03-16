package com.istef.southpark.model.thread;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import com.istef.southpark.Const;
import com.istef.southpark.model.listener.CentileListener;
import com.istef.southpark.model.listener.GetInfoListener;
import com.istef.southpark.model.listener.GetVideoListener;
import com.istef.southpark.model.listener.TaskCleanUpListener;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.util.FilesHandler;

import io.lindstrom.m3u8.model.Resolution;

public class DownloadMonitor {
	private ExecutorService exService;
	private List<TaskStruct> runningTasks;

	public DownloadMonitor(List<TaskStruct> runningTasks, ExecutorService exService) {
		this.runningTasks = runningTasks;
		this.exService = exService;
	}

	public UUID submitTaskGetEpisodeInfo(CloseableHttpClient client, String mgid, GetInfoListener listener) {

		// Create task and execute
		TaskStruct taskStruct = new TaskStruct();
		synchronized (runningTasks) {
			runningTasks.add(taskStruct);
		}
		DownloadTaskGetInfo task = new DownloadTaskGetInfo(listener, client, mgid, taskStruct, new TaskCleanUpListener() {
			
			@Override
			public void taskCleanup(TaskStruct taskStruct) {
				synchronized (runningTasks) {
					runningTasks.remove(taskStruct);
				}
			}
		});
		exService.execute(task);
		return taskStruct.id;
	}

	public UUID submitTaskGetVideo(CloseableHttpClient client, String mgid, MgidParserModel mgidParserModel,
			Resolution resolution, Path downloadDir, GetVideoListener listener, CentileListener centileListener) {
		
		// Create task and execute
		TaskStruct taskStruct = new TaskStruct();
		synchronized (runningTasks) {
			runningTasks.add(taskStruct);
		}
		DownloadTaskGetVideo task = new DownloadTaskGetVideo(new GetVideoListener() {
			
			@Override
			public void onStart() {
				listener.onStart();
			}
			
			@Override
			public void onVideoParserEnd(Throwable t) {
				FilesHandler.deleteUuidFromTmpDir(Paths.get(Const.TMP_DIR), taskStruct.id);
				listener.onVideoParserEnd(t);
			}

			@Override
			public void onSubtitleParserEnd(Throwable t) {
				if (t != null) {
					FilesHandler.deleteUuidFromTmpDir(Paths.get(Const.TMP_DIR), taskStruct.id);
				}
				listener.onSubtitleParserEnd(t);
			}

			@Override
			public void onM3U8ParserEnd_2(Throwable t) {
				if (t != null) {
					FilesHandler.deleteUuidFromTmpDir(Paths.get(Const.TMP_DIR), taskStruct.id);
				}
				listener.onM3U8ParserEnd_2(t);
			}

			
		}, centileListener, client, mgid, taskStruct, new TaskCleanUpListener() {
			
			@Override
			public void taskCleanup(TaskStruct taskStruct) {
				synchronized (runningTasks) {
					runningTasks.remove(taskStruct);
				}
			}
		}, resolution, downloadDir, mgidParserModel);
		exService.execute(task);
		return taskStruct.id;
	}
}
