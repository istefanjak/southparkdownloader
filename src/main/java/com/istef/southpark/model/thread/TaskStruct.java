package com.istef.southpark.model.thread;

import java.util.UUID;

import com.istef.southpark.requests.Cancelable;

public class TaskStruct {
	enum TaskType { TASK1, TASK2 }
	public final UUID id = UUID.randomUUID();
	public String mgid;
	public TaskType type;
	public Cancelable cancelable;
	public boolean finished;
	
	public TaskStruct() {}
	
	public TaskStruct(String mgid, TaskType type, Cancelable cancelable) {
		this.mgid = mgid;
		this.type = type;
		this.cancelable = cancelable;
	}
	
	@Override
	public String toString() {
		return "TaskStruct [mgid=" + mgid + ", type=" + type + ", cancelable=" + cancelable + ", finished=" + finished
				+ "]";
	}
}