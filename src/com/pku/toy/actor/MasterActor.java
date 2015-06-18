package com.pku.toy.actor;

import java.util.ArrayList;
import java.util.List;

import com.pku.toy.Master;
import com.pku.toy.model.WorkingThreadModel;

public class MasterActor {
	private String ip;
	private int port;
	private Master context;
	
	public MasterActor(String _ip, int _port, Master _context) {
		ip = _ip;
		port = _port;
		context = _context;
	}
	
	public List<WorkingThreadModel> getWorkingEnvironment() {
		List<WorkingThreadModel> workingThreadSituations = new ArrayList<>();
		
		return workingThreadSituations;
	}
}
