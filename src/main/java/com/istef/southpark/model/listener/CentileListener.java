package com.istef.southpark.model.listener;

import com.istef.southpark.requests.M3U8Parser.Centile;

public interface CentileListener {
	void onProgressChanged(Centile centile);
}
