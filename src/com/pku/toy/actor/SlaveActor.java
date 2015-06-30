package com.pku.toy.actor;

import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import util.Address;

import com.pku.toy.dht.DHTPeer;
import com.pku.toy.logic.Slave;
import com.pku.toy.logic.WorkingThread;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.implement.SlaveImpl;
import com.pku.toy.rmi.inter.ISlave;

public class SlaveActor extends Thread {
	private String ip;
	private int port;
	private Slave context;
	
	public SlaveActor(int _port, Slave _context) {
		try {
			ip = Address.GetIpAddress();
			System.setProperty("java.rmi.server.hostname", ip);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		port = _port;
		context = _context;
	}
	
	public String getIpAddress() {
		return this.ip;
	}
	
	public void createWorkingThread(WorkingThreadData workingThreadData) {
		context.createWorkingThread(workingThreadData);
	}
	
	
	public void startFetchFile(String fileName) {
		System.out.println("Slave Actor:receive start read file:" + fileName);
	}
	

	public void setDHTPeer(DHTPeer peer) {
		// TODO Auto-generated method stub
		context.setDHTPeer(peer);
	}

	public void connectToOtherPeers(int threadId)  {
		// TODO Auto-generated method stub
		context.connectToOtherPeers(threadId);
	}
	
	public void receiveStartCalculation(int step) {
		context.startCalculation(step);
	}
	
	public void run() {
		try
		{
			ISlave slaveActorService = new SlaveImpl(this);
			LocateRegistry.createRegistry(port);
			Naming.rebind( "rmi://" + this.ip + ":" + port + "/Slave", slaveActorService);
			System.out.println("slave actor bind ip address:" + getIpAddress());
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	

}
