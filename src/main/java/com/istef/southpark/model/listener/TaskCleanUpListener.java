package com.istef.southpark.model.listener;

import com.istef.southpark.model.thread.TaskStruct;

public interface TaskCleanUpListener {
	void taskCleanup(TaskStruct taskStruct);
}
