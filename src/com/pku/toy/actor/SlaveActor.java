package com.pku.toy.actor;

import com.pku.toy.WorkingThread;

public class SlaveActor {
	private String ip;
	private int port;
	private WorkingThread context;
	
	public SlaveActor(String _ip, int _port, WorkingThread _context) {
		ip = _ip;
		port = _port;
		context = _context;
	}
}
