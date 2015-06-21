package com.pku.toy.model;

import java.io.Serializable;

public class WorkingThreadData implements Serializable {
	private String ip;
	private int threadNum;
	private String lastContactTime;
	private String status;
	
	public WorkingThreadData(String _ip, int _threadNum, String _status) {
		ip = _ip;
		threadNum = _threadNum;
		status = _status;
	}
	
	public int getId() {
		return this.threadNum;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public String getIp() {
		return this.ip;
	}
}
