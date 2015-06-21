package com.pku.toy.rmi.implement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import com.pku.toy.actor.MasterActor;
import com.pku.toy.logic.Master;
import com.pku.toy.rmi.inter.IMaster;


public class MasterImpl extends UnicastRemoteObject implements IMaster {
	private MasterActor masterActor;
	public MasterImpl(MasterActor masterActor) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Map<Integer, Integer> getGraphData(String file) throws RemoteException {
		Map<Integer, Integer> graph = new HashMap<Integer, Integer>();
		
		return graph;
	}
}
