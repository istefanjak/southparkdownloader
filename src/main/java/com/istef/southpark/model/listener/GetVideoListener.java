package com.istef.southpark.model.listener;

public interface GetVideoListener {
	void onStart();
	void onM3U8ParserEnd_2(Throwable t);
	void onSubtitleParserEnd(Throwable t);
	void onVideoParserEnd(Throwable t);
}
