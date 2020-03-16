package com.istef.southpark.model.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SegmentInfo {
	@SerializedName("package") 
	public Package _package;
	
	public class Package {
		public String version;
		public Video video;
		
		public class Video {
			public List<Item> item;
			
			public class Item {
				public String origination_date;
				public List<Rendition> rendition;
				public List<Transcript> transcript;
				
				public class Rendition {
					public String cdn;
					public String method;
					public String duration;
					public String type;
					public String src;
					public String rdcount;
				}
				
				public class Transcript {
					public String kind;
					public String srclang;
					public String label;
					public List<Typographic> typographic;
					
					public class Typographic {
						public String format;
						public String src;
					}
				}
			}
		}
	}
	
}
