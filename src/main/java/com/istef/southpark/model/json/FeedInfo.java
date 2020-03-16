package com.istef.southpark.model.json;

import java.util.List;

public class FeedInfo {
	public Long feedLoaderStartTime;
	public String originalFeed;
	public String feedWithQueryParams;
	public Feed feed;
	
	public class Feed {
		public String title;
		public String description;
		public String link;
		public Image image;
		public List<Item> items;
		
		public class Image {
			public String title;
			public String link;
			public String url;
		}
		
		public class Item {
			public String link;
			public String pubDate;
			public String description;
			public String guid;
			public Double duration;
			public Boolean isMature;
			public String title;
			public Group group;
			public Image image;
			
			public class Group {
				public String content;
				public Text text;
				public Category category;
				
				public class Category {
					public String franchise;
					public String artist;
					public String videoTitle;
					public String playlistRepTitle;
					public String playlistTitle;
					public String playlistURI;
					public String id;
					public String source;
					public String sourceLink;
					public String date;
					public String seoHTMLText;
					public String contentType;
					public String seasonN;
				}
				
				public class Text {
					public String role;
					public String lang;
					public String label;
					public String type;
					public String src;
				}
			}
			
			public class Image {
				public String url;
				public Integer height;
				public Integer width;
			}
		}
		
	}
		
}
