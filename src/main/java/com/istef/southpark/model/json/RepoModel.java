package com.istef.southpark.model.json;

import java.util.List;

public class RepoModel {
	public String name;
	public String description;
	
	public List<Season> seasons;
	
	public static class Season {
		public String name;
		public List<Mgid> mgids;
		
		public Season() {
		}
		
		public Season(String name, List<Mgid> mgids) {
			this.name = name;
			this.mgids = mgids;
		}
		
		public static class Mgid {
			public String mgid;
			
			public Mgid() {
			}

			public Mgid(String mgid) {
				this.mgid = mgid;
			}

			@Override
			public String toString() {
				return mgid;
			}
		}
	}
}
