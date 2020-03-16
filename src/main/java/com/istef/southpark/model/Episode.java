package com.istef.southpark.model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.istef.southpark.Const;
import com.istef.southpark.model.json.FeedInfo;
import com.istef.southpark.util.Function;

public class Episode {
	public class Subtitle {
		private String title;
		private String lang;
		private String uri;
		
		public Subtitle(String title, String lang, String uri) {
			this.title = title;
			this.lang = lang;
			this.uri = uri;
		}
		public String getTitle() {
			return title;
		}
		public String getLang() {
			return lang;
		}
		public String getUri() {
			return uri;
		}
		@Override
		public String toString() {
			return "Subtitle [title=" + title + ", lang=" + lang + ", uri=" + uri + "]";
		}
	}
	
	public class SegmentRef {
		private String guid;
		private String title;
		private Subtitle subtitle;
		
		public SegmentRef(String guid, String title, Subtitle subtitle) {
			this.guid = guid;
			this.title = title;
			this.subtitle = subtitle;
		}
		public String getTitle() {
			return title;
		}
		public String getUri() {
			return String.format(Const.SEGMENT_URI_FORMAT, guid);
		}
		public Subtitle getSubtitle() {
			return subtitle;
		}
		@Override
		public String toString() {
			return "Segment [uri=" + getUri() + ", title=" + title + ", subtitle=" + subtitle + "]";
		}
	}
	
	private String title;
	private String imageURI;
	private Double duration;
	private String description;
	private String pubDate;
	private String seasonNum;
	private String episodeNum;
	private ArrayList<SegmentRef> segments = new ArrayList<>();
	
	public Episode(FeedInfo feedInfo) {
		title = feedInfo.feed.title;
		
		String img = feedInfo.feed.image.url;
		int indexWhenParams = img.indexOf("?");
		if(indexWhenParams != -1) {
			imageURI = img.substring(0, indexWhenParams);
		} else {
			imageURI = img;
		}
		
		duration = 0.;
		feedInfo.feed.items.forEach(item -> duration += item.duration);
		
		description = feedInfo.feed.description;
		
		pubDate = feedInfo.feed.items.get(0).group.category.date;
		seasonNum = feedInfo.feed.items.get(0).group.category.seasonN;
		
		Pattern pattern = Pattern.compile("s(?<season>[0-9]{2})e(?<episode>[0-9]{2})");
		Matcher matcher = pattern.matcher(feedInfo.feed.link);
		
		while(matcher.find()) {
			episodeNum = matcher.group("episode");
		}
		
		feedInfo.feed.items.forEach(i -> segments.add(
				new SegmentRef(i.guid,
							i.title,
							i.group.text == null? null : new Subtitle(i.group.text.label, i.group.text.lang, i.group.text.src)
							)
				));
	}

	public String getTitle() {
		return title;
	}

	public String getImageUri() {
		return imageURI;
	}
	
	public String getImageUri(int width, int height) {
		return imageURI.concat(String.format("?width=%d&height=%d", width, height));
	}
	
	public Double getDuration() {
		return duration;
	}
	
	public String getDurationStr() {
		long dur = (long) Math.floor(duration);
		long min = TimeUnit.SECONDS.toMinutes(dur);
		dur -= TimeUnit.MINUTES.toSeconds(min);
		return String.format("%d min %d sec", min, dur);
	}
	
	public String getDescription() {
		return description;
	}

	public String getPubDate() {
		return pubDate;
	}

	public String getSeasonNum() {
		return (seasonNum.length() == 1)? "0" + seasonNum : seasonNum;
	}

	public String getEpisodeNum() {
		return (episodeNum.length() == 1)? "0" + episodeNum : episodeNum;
	}
	
	public String getEpisodeFileName() {
		return String.format("s%se%s - %s", getSeasonNum(), getEpisodeNum(),
				Function.strForFileName(getTitle()));
	}

	public ArrayList<SegmentRef> getSegments() {
		return segments;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(getTitle()).append("\n")
			.append(getDescription()).append("\n")
			.append(getDuration()).append("\n")
			.append(getPubDate()).append("\n")
			.append(String.format("s%se%s", getSeasonNum(), getEpisodeNum())).append("\n")
			.append(getImageUri(300,200)).append("\n");
		segments.forEach(s -> ret.append(s).append("\n"));
		return ret.toString();
	}
}
