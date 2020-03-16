package com.istef.southpark.util.converter;

import com.istef.southpark.ui.viewmodel.reposelector.TreeValueEpisode;

import javafx.util.StringConverter;

public class StatusPropertyToStringConverter extends StringConverter<Number> {

	@Override
	public String toString(Number status) {
		if (status.equals(TreeValueEpisode.STATUS_ERROR)) {
			return "Error";
		} else if (status.equals(TreeValueEpisode.STATUS_LOADING)) {
			return "Loading";
		} else if (status.equals(TreeValueEpisode.STATUS_SUCCESS)) {
			return "Success";
		}
		return "NULL";
	}

	@Override
	public Number fromString(String string) {
		if (string.equals("Error")) {
			return TreeValueEpisode.STATUS_ERROR;
		}
		else if (string.equals("Loading")) {
			return TreeValueEpisode.STATUS_LOADING;
		}
		else if (string.equals("Success")) {
			return TreeValueEpisode.STATUS_SUCCESS;
		}
		return null;
	}

}
