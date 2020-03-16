package com.istef.southpark.util.converter;

import io.lindstrom.m3u8.model.Resolution;
import javafx.util.StringConverter;

public class ResolutionToStringConverter extends StringConverter<Resolution> {

	@Override
	public String toString(Resolution resolution) {
		return resolution == null ? "NULL" : resolution.width() + "x" + resolution.height();
	}

	@Override
	public Resolution fromString(String string) {
		return string.equals("NULL") ? null : new Resolution() {
			
			@Override
			public int width() {
				int xInd = string.indexOf("x");
				return Integer.valueOf(string.substring(0, xInd));
			}

			@Override
			public int height() {
				int xInd = string.indexOf("x");
				return Integer.valueOf(string.substring(xInd+1));
			}
			
		};
	}

}
