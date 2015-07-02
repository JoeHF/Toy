package com.pku.toy.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
				threadGraphMap.put(threadData.getId(), filePath + Constant.Edge_suffixString + j);
				j++;
			}
		}
		
		System.out.println( "\n" + threadGraphMap  + "\n" );
		
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getStatus().equals(Constant.WORKING)) {
				masterActor.transferSubgraph( threads.get(i).getId(),threads.get(i).getIp(), 
						                      threadGraphMap.get( threads.get(i).getId() ));
			}
		}	
		
		createDHTService();   //create dht service
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
		dht.setDHT(peerModels, this.range );
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
	
	public void initialWorkingThreadIterationNum( int totalStep )
	{
		
		//  to notify how many times working thread show run;
		for (int i = 0; i < threads.size(); i++) 
		{
			if (threads.get(i).getStatus().equals(Constant.WORKING)) {
				System.out.println("master initialWorkingThreadIterationNum " + i);
				masterActor.initialWorkingThreadIterationNum( threads.get(i),totalStep);
			}
		}
	}
	
	private void analyzeGraph( String graphFileName )
	// generate sub-edge-graph and degree graph  
	// graphFileName+"_edge"+"0/1/2"
	// graphFileName+"_degree"
	{
		generateDegreeFile( graphFileName );
		
		List<Long> keyList = DHT.separateKeyRange( range, Constant.PEER_NUM );
		for ( int ite = 0; ite<keyList.size(); ite++ )
		{
			long lowBound, highBound, fromNode, toNode;
			HashMap< Long, List<Long> > edges = new HashMap<>();
			String line;
			String[] edge;
			
			if ( ite==0 ) lowBound = -1;
			else          lowBound = keyList.get(ite-1);
			highBound = keyList.get(ite);
			
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
					if ( toNode<=lowBound || toNode>highBound ) continue;
					if ( edges.containsKey( toNode ) )
						edges.get( toNode ).add( fromNode );
					else
					{
						 edges.put( toNode, new ArrayList<Long>() );
						 edges.get( toNode ).add( fromNode );
					}
				}
				
				File fout = new File( graphFileName + Constant.Edge_suffixString + ite );
				BufferedWriter writer = new BufferedWriter(
	     		       new OutputStreamWriter( new FileOutputStream( fout ), "UTF-8") );
				
	            for ( long node : edges.keySet() )
	            for ( long from : edges.get(node) )
	            {
	            	writer.write( "" + node + "\t" + from + "\n" );
	            }
				writer.flush();
				writer.close();
			}
			catch ( IOException e) 
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	private void generateDegreeFile( String graphFileName )
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
			
			File fout = new File( graphFileName + Constant.Degree_suffixString );
			BufferedWriter writer = new BufferedWriter(
     		       new OutputStreamWriter( new FileOutputStream( fout ), "UTF-8") );
			
            for ( long node : degrees.keySet() )
            {
            	writer.write( "" + node + "\t" + degrees.get(node) + "\n" );
            }
			writer.flush();
			writer.close();
		}
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	//----------jdc-----------------------

}
