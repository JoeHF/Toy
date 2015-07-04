package com.pku.toy.model;

import java.io.Serializable;

public class PeerModel implements Serializable
{
    public String peerAddress;
    public String slaveService;
    public int    threadId;
    
	public PeerModel() {
		// TODO Auto-generated constructor stub
	}
	
	public PeerModel( PeerModel o )
	{
		this.peerAddress = new String( o.peerAddress );
		this.slaveService = new String( o.slaveService );
		this.threadId = o.threadId;
	}

}
