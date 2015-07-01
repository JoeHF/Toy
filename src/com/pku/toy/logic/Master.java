package com.pku.toy.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.pku.toy.Constant;
import com.pku.toy.actor.MasterActor;
import com.pku.toy.dht.DHT;
import com.pku.toy.model.FileModel;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;

public class Master {
	
	//-----------------hf------------------
	private Object object1 = new Object();
	private Object object2 = new Object();
	private MasterActor masterActor;
	private int slaveNum;
	private List<WorkingThreadData> threads;
	private List<SlaveModel> slaveModels;
	private Set<Integer> idleThreadNumber = new HashSet<Integer>();
	private DHT dht;
	private int workThreadDoneNum = 0;
	private Map<Integer, String> threadGraphMap = new HashMap<>();
	
	public Master() {
		
	}
	
	public boolean isIdleThread(int id) {
		return idleThreadNumber.contains(id);
	}
	
	public void startActor() {
		this.masterActor = new MasterActor(Constant.MASTER_PORT, this);
		this.masterActor.run();
	}
	
	public void startEnv(List<SlaveModel> _slaveModels) {
		slaveNum = _slaveModels.size();
		slaveModels = _slaveModels;
		masterActor.bindSlaveService(_slaveModels);
	}
	
	public String getAddress() {
		return masterActor.getIpAddress();
	}
	
	public void readFile(String filePath) {
		analyzeGraph(filePath);
		int j = 0;
		for (int i = 0; i < threads.size(); i++) {
			WorkingThreadData threadData = threads.get(i);
			if (threadData.getStatus().equals(Constant.WORKING)) {
				threadGraphMap.put(threadData.getId(), Constant.Edge_suffixString + j);
				j++;
			}
		}
		
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getStatus().equals(Constant.WORKING)) {
				masterActor.transferSubgraph(i,threads.get(i).getIp(), threadGraphMap.get(i));
			}
		}	
	}
	
	public void createWorkingThread(List<WorkingThreadData> workingThreadDatas) {
		for (int i = 0; i < workingThreadDatas.size(); i++) {
			if (workingThreadDatas.get(i).getStatus().equals(Constant.IDLE)) {
				idleThreadNumber.add(workingThreadDatas.get(i).getId());
			}
		}
		
		threads = workingThreadDatas;
		System.out.println("Master start create working thread");
		masterActor.createWorkingThread(workingThreadDatas);
		
		createDHTService();   //create dht service
	}
	
	private void createDHTService() {
		List<PeerModel> peerModels = new ArrayList<>();
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getStatus().equals(Constant.WORKING)) {
				PeerModel peer = new PeerModel();
				System.out.println("Master create dht peer service:" + "rmi://" + threads.get(i).getIp() + ":" + Constant.PEER_PORT + "/DHTService" + threads.get(i).getId());
				peer.peerAddress = "rmi://" + threads.get(i).getIp() + ":" + Constant.PEER_PORT + "/DHTService" + threads.get(i).getId();
				peer.slaveService = "rmi://" + threads.get(i).getIp() + ":" + Constant.SLAVE_PORT + "/Slave";
				peer.threadId = threads.get(i).getId();
				peerModels.add(peer);	
			}
		}
		
		dht = new DHT();
		dht.setDHT(peerModels, 1000);
	}
	
	public void receiveOneStepDone(int threadId) {
		synchronized (object1) {
			workThreadDoneNum++;
			System.out.println("Master receive threadId " + threadId + " done " + workThreadDoneNum + "/3 has done");
			if (workThreadDoneNum == 3) {
				synchronized (object2) {
					object2.notify();
					workThreadDoneNum = 0;
				}
			}
		}
	}
	
	public void startCalculate(int totalStep) {
		MyRunnerThread myRunnerThread = new MyRunnerThread(totalStep);
		myRunnerThread.start();
	}
	
	private class MyRunnerThread extends Thread {
		private int stepNum;
		public MyRunnerThread(int _stepNum) {
			stepNum = _stepNum;
		}
		
		public void run() {
			for (int i = 0; i < stepNum; i++) {
				masterActor.notifyWorkingThreadCalculation(i);
				try {
					synchronized (object2) {
						object2.wait();
						System.out.println("Master receive all thread done");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//---------zzy-----------------------
	private long range;
	private Map<Long, Long> degrees;
	
	private void analyzeGraph( String graphFileName )
	// generate sub-edge-graph and degree graph  
	// graphFileName+"_edge"+"0/1/2"
	// graphFileName+"_degree"+"0/1/2"
	{
		generateDegreeFile( graphFileName );
	}
	
	private long generateDegreeFile( String graphFileName )
	{
		long fromNode, toNode, nodeDegree;
		String line;
		String[] edge;
		
		range = 0;
		degrees = new HashMap<>();
		try
		{
			File file = new File( graphFileName );
			BufferedReader reader = new BufferedReader( 
     		       new InputStreamReader( new FileInputStream( file ), "UTF-8") );
			while ( true )
			{
				line = reader.readLine();
				if ( line==null ) break;
				edge = line.split( "\t" );
				fromNode = Long.parseLong( edge[0] );
				toNode   = Long.parseLong( edge[1] );
				if ( fromNode > range ) range = fromNode;
				if ( toNode   > range ) range = toNode;
				if ( degrees.containsKey( fromNode ) )
				{
					nodeDegree = degrees.get( fromNode );
					degrees.put( fromNode, nodeDegree + 1 );
				}
				else
					degrees.put( fromNode, (long)1 );
			}
		}
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return range;
	}
	
	//----------jdc-----------------------

}
