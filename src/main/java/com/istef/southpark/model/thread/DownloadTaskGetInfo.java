package com.istef.southpark.model.thread;

import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.istef.southpark.exception.MgidParserException;
import com.istef.southpark.model.listener.GetInfoListener;
import com.istef.southpark.model.listener.TaskCleanUpListener;
import com.istef.southpark.requests.Cancelable;
import com.istef.southpark.requests.M3U8Parser;
import com.istef.southpark.requests.MgidParser;
import com.istef.southpark.requests.MgidParser.MgidParserModel;

import io.lindstrom.m3u8.model.Resolution;
import io.lindstrom.m3u8.parser.PlaylistParserException;

public class DownloadTaskGetInfo implements Runnable, Cancelable {
	private static final Logger logger = LoggerFactory.getLogger(DownloadTaskGetInfo.class);
	
	private GetInfoListener listener;
	private CloseableHttpClient client;
	private String mgid;
	private TaskStruct runningTask;
	private TaskCleanUpListener taskCleanUpListener;
	private boolean onGoingCancel;
	
	public DownloadTaskGetInfo(GetInfoListener listener, CloseableHttpClient client, String mgid,
			TaskStruct runningTask, TaskCleanUpListener taskCleanUpListener) {		
		this.listener = listener;
		this.client = client;
		this.mgid = mgid;
		this.runningTask = runningTask;
		this.taskCleanUpListener = taskCleanUpListener;
		this.runningTask.cancelable = this;
	}

	@Override
	public void run() {
		if (onGoingCancel) return;
		try {
			MgidParser mgidParser = new MgidParser(client, mgid);
			runningTask.mgid = mgid;
			runningTask.type = TaskStruct.TaskType.TASK1;
			if (onGoingCancel) return;
			runningTask.cancelable = mgidParser;
			MgidParserModel mgidParserModel = mgidParser.parse();
			if (listener != null)
				listener.onMgidParserEnd(null);

			M3U8Parser m3u8Parser = new M3U8Parser(client, mgidParserModel.getM3u8s_Bodies());
			runningTask.cancelable = m3u8Parser;
			List<Resolution> resolutions = m3u8Parser.getResolutions();
			if (listener != null)
				listener.onM3U8ParserEnd_1(resolutions, mgidParserModel, null);
			runningTask.cancelable = null;

		} catch (MgidParserException e) {
			logger.error(e.getMessage(), e);
			if (listener != null)
				listener.onMgidParserEnd(e);

		} catch (PlaylistParserException e) {
			logger.error(e.getMessage(), e);
			if (listener != null)
				listener.onM3U8ParserEnd_1(null, null, e);

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
