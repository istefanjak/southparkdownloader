package com.istef.southpark.util.converter;

import com.istef.southpark.requests.MgidParser.MgidParserModel;

import javafx.util.StringConverter;

public class MgidParserModelToStringConverter extends StringConverter<MgidParserModel> {

	@Override
	public String toString(MgidParserModel object) {
		return object == null ? "NULL" : object.getEpisode().getEpisodeNum() + ". " + object.getEpisode().getTitle();
	}

	@Override
	public MgidParserModel fromString(String string) {
		return null;
	}

}
