package com.pku.toy.actor;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Map;

import util.Address;

import com.pku.toy.Constant;
import com.pku.toy.dht.DHTPeer;
import com.pku.toy.logic.Slave;
import com.pku.toy.logic.WorkingThread;
import com.pku.toy.model.DHTPeerData;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.implement.SlaveImpl;
import com.pku.toy.rmi.inter.IMaster;
import com.pku.toy.rmi.inter.ISlave;

public class SlaveActor extends Thread {
	private String ip;
	private int port;
	private Slave context;
	private IMaster masterService;
	
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
	
	public void restartcreateWorkingThread(WorkingThreadData workingThreadData) {
		context.restartcreateWorkingThread(workingThreadData);
	}
	
	public void initialWorkingThreadIterationNum( WorkingThreadData workingThreadData, int totalStep )
	{
		context.initialWorkingThreadIterationNum(workingThreadData, totalStep);
	}
	
	public void receiveBindMaster(String ip) {
		
		try {
			String lookupString = "rmi://" + ip + ":" + Constant.MASTER_PORT + "/Master";
			masterService = (IMaster)Naming.lookup(lookupString);
			System.out.println("Slave actor bind master service:" + ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void notifyCalOneStepDone(int threadId) {
		try {
			masterService.notifyMasterOneStepDone(threadId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startFetchFile(String fileName) {
		System.out.println("Slave Actor:receive start read file:" + fileName);
	}

	public void setDHTPeer(DHTPeerData peer) {
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
	
	public void openWriter(int threadNum) {
		context.openWriter(threadNum);
	}
	
	public void closeWriter(int threadNum) {
		context.closeWriter(threadNum);
	}
	
	public void receiveSubgraph(Map<Long, List<Long>> edges,int threadNum) {
		 context.receiveSubgraph(edges,threadNum);
	}
	
	public void openDegreeWriter(int threadNum) {
		context.openDegreeWriter(threadNum);
	}
	
	public void closeDegreeWriter(int threadNum) {
		context.closeDegreeWriter(threadNum);
	}
	
	public void receiveDegreeFile(Map<Long, Long> edges,int threadNum) {
		 context.receiveDegreeFile(edges,threadNum);
	}
}
