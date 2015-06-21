package com.pku.toy.model;
import com.pku.toy.Constant.WorkingThreadStatus;;

public class WorkingThreadData {
	private String ip;
	private int threadNum;
	private String lastContactTime;
	private WorkingThreadStatus status;
	
	public WorkingThreadData(String _ip, int _threadNum, WorkingThreadStatus _status) {
		ip = _ip;
		threadNum = _threadNum;
		status = _status;
	}
	
	public int getId() {
		return this.threadNum;
	}
	
	public WorkingThreadStatus getStatus() {
		return this.status;
	}
	
	public String getIp() {
		return this.ip;
	}
}
