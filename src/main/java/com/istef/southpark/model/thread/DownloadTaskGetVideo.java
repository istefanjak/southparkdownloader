package com.istef.southpark.model.thread;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.Const;
import com.istef.southpark.exception.M3U8ParserException;
import com.istef.southpark.exception.SubtitleParserException;
import com.istef.southpark.exception.VideoParserException;
import com.istef.southpark.model.Episode;
import com.istef.southpark.model.listener.CentileListener;
import com.istef.southpark.model.listener.GetVideoListener;
import com.istef.southpark.model.listener.TaskCleanUpListener;
import com.istef.southpark.requests.Cancelable;
import com.istef.southpark.requests.M3U8Parser;
import com.istef.southpark.requests.SubtitleParser;
import com.istef.southpark.requests.MgidParser.MgidParserModel;
import com.istef.southpark.util.Function;
import com.istef.southpark.video.VideoParser;

import io.lindstrom.m3u8.model.Resolution;

public class DownloadTaskGetVideo implements Runnable, Cancelable {
	private static final Logger logger = LoggerFactory.getLogger(DownloadTaskGetVideo.class);
	
	private GetVideoListener listener;
	private CentileListener centileListener;
	private CloseableHttpClient client;
	private String mgid;
	private TaskStruct runningTask;
	private Resolution resolution;
	private Path downloadDir;
	private MgidParserModel mgidParserModel;
	private TaskCleanUpListener taskCleanUpListener;
	private boolean onGoingCancel;

	public DownloadTaskGetVideo(GetVideoListener listener, CentileListener centileListener, CloseableHttpClient client,
			String mgid, TaskStruct runningTask, TaskCleanUpListener taskCleanUpListener, Resolution resolution, Path downloadDir,
			MgidParserModel mgidParserModel) {
		super();
		this.listener = listener;
		this.centileListener = centileListener;
		this.client = client;
		this.mgid = mgid;
		this.runningTask = runningTask;
		this.taskCleanUpListener = taskCleanUpListener;
		this.resolution = resolution;
		this.downloadDir = downloadDir;
		this.mgidParserModel = mgidParserModel;
		this.runningTask.cancelable = this;
	}

	@Override
	public void run() {
		if (onGoingCancel) return;
		if (listener != null)
			listener.onStart();
		try {
			M3U8Parser m3u8Parser = new M3U8Parser(client, mgidParserModel.getM3u8s_Bodies());
			Episode e = mgidParserModel.getEpisode();
			
			final String fileNameBase = e.getEpisodeFileName();
			final String fileNameBaseWithId = String.valueOf(runningTask.id) + "_" + fileNameBase + " - act ";
			
			Path vidPath = Paths.get(Const.TMP_DIR, fileNameBaseWithId + ".ts");
			if (onGoingCancel) return;
			runningTask.mgid = mgid;
			runningTask.type = TaskStruct.TaskType.TASK2;
			runningTask.cancelable = m3u8Parser;
			m3u8Parser.parse(vidPath, resolution, centileListener);
			if (m3u8Parser.isOngoingCancel()) return;
			if (listener != null)
				listener.onM3U8ParserEnd_2(null);

			List<String> subtitleUris = e.getSegments().stream().filter(s -> s.getSubtitle() != null).map(s -> s.getSubtitle().getUri())
					.collect(Collectors.toList());
			Path subPath = Paths.get(Const.TMP_DIR, fileNameBaseWithId + ".srt");
			SubtitleParser subPars = new SubtitleParser(client, subtitleUris);
			runningTask.cancelable = subPars;
			subPars.parse(subPath);
			if (subPars.isOnGoingCancel()) return;
			if (listener != null)
				listener.onSubtitleParserEnd(null);

			// TO-DO Cancelable video parser
			runningTask.cancelable = null;
			List<Path> videos = Function.getAllFilesMatcher(vidPath);
			List<Path> subtitles = Function.getAllFilesMatcher(subPath);
			VideoParser videoParser = new VideoParser(videos, subtitles);
			videoParser.merge(Paths.get(downloadDir.toAbsolutePath().toString(), fileNameBase + ".mkv"));
			if (listener != null)
				listener.onVideoParserEnd(null);

		} catch (M3U8ParserException e) {
			logger.error(e.getMessage(), e);
			if (listener != null)
				listener.onM3U8ParserEnd_2(e);

		} catch (SubtitleParserException e) {
			logger.error(e.getMessage(), e);
			if (listener != null)
				listener.onSubtitleParserEnd(e);

		} catch (VideoParserException e) {
			logger.error(e.getMessage(), e);
			if (listener != null)
				listener.onVideoParserEnd(e);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (taskCleanUpListener != null)
				taskCleanUpListener.taskCleanup(runningTask);
		}

	}

	@Override
	public void cancel() {
		onGoingCancel = true;
	}
}
