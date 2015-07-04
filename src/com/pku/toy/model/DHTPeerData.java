package com.pku.toy.model;

import java.io.Serializable;
import java.util.TreeMap;

public class DHTPeerData implements Serializable
{
	public String address;
	public long peerId;
	public int  port;
	public int  threadId;
	public TreeMap<Long, String>    router;
	
}
