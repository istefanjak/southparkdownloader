package com.istef.southpark.util.converter;

import com.istef.southpark.model.json.RepoModel.Season.Mgid;

import javafx.util.StringConverter;

public class MgidToStringConverter extends StringConverter<Mgid> {

	@Override
	public String toString(Mgid mgid) {
		return mgid == null ? "NULL" : mgid.mgid;
	}

	@Override
	public Mgid fromString(String string) {
		return string.equals("NULL") ? null : new Mgid(string);
	}

}
