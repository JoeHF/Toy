package com.pku.toy.model;

import java.io.Serializable;

public class FileModel implements Serializable{
	private String ip;
	private String name;
	
	public FileModel(String _ip, String _name) {
		ip = _ip;
		name = _name;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public String getName() {
		return this.name;
	}
}
