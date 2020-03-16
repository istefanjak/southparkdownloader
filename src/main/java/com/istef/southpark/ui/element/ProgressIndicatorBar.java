package com.istef.southpark.ui.element;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ProgressIndicatorBar extends StackPane {
	final private ProgressBar bar = new ProgressBar();
	final private Text text = new Text();
	final private String labelFormatSpecifier;

	final private static int DEFAULT_LABEL_PADDING = 5;

	public ProgressIndicatorBar(final String labelFormatSpecifier) {
		this.labelFormatSpecifier = labelFormatSpecifier;

		setProgress(null);
		bar.setMaxWidth(Double.MAX_VALUE);
		getChildren().setAll(bar, text);
	}

	public void setProgress(Double centile) {
		if (centile == null) {
			text.setText("");
			bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		} else {
			text.setText(String.format(labelFormatSpecifier, centile*100));
			bar.setProgress(centile);
		}

		bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
		bar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
	}
}