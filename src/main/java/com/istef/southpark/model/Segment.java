package com.istef.southpark.model;

import java.util.HashMap;
import java.util.Map;

import com.istef.southpark.model.json.SegmentInfo;
import com.istef.southpark.model.json.SegmentInfo.Package.Video.Item.Transcript.Typographic;

public class Segment {
	private String uri;
	private Map<String, String> subtitles = new HashMap<>();
	
	public Segment(SegmentInfo segmentInfo) {
		uri = segmentInfo._package.video.item.get(0).rendition.get(0).src;
		for(Typographic t : segmentInfo._package.video.item.get(0).transcript.get(0).typographic) {
			subtitles.put(t.format, t.src);
		}
	}
	public String getUri() {
		return uri;
	}
	public Map<String, String> getSubtitles() {
		return subtitles;
	}
	
}
