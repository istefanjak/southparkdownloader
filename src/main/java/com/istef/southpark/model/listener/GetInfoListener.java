package com.istef.southpark.model.listener;

import java.util.List;

import com.istef.southpark.requests.MgidParser.MgidParserModel;

import io.lindstrom.m3u8.model.Resolution;

public interface GetInfoListener {
	void onMgidParserEnd(Throwable t);
	void onM3U8ParserEnd_1(List<Resolution> resolutions, MgidParserModel model, Throwable t);
}
