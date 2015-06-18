package com.pku.toy.actor;

import com.pku.toy.Slave;

public class SlaveActor {
	private String ip;
	private int port;
	private Slave context;
	
	public SlaveActor(String _ip, int _port, Slave _context) {
		ip = _ip;
		port = _port;
		context = _context;
	}
}
