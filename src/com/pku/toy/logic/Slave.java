package com.pku.toy.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.pku.toy.Constant;
import com.pku.toy.actor.MasterActor;
import com.pku.toy.actor.SlaveActor;
import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;

public class Slave {
	
	//-----------------hf------------------
	private SlaveActor slaveActor;
	private int threadNum;
	private List<WorkingThread> threads;
	private HashMap<Integer, PrintWriter>   writers = new HashMap<>();
	
	public Slave() {
		threadNum = 0;
		threads = new ArrayList<>();
	}
	
	public void startActor() {
		
		this.slaveActor = new SlaveActor(Constant.SLAVE_PORT, this);
		this.slaveActor.run();
	}
	
	public void createWorkingThread(WorkingThreadData workingThreadData) {
		System.out.println("slave " + getAddress() + " thread num:" + workingThreadData.getId() + " create working thread:" + workingThreadData.getStatus());
		WorkingThread thread = new WorkingThread(workingThreadData, this);
		threads.add(thread);
		threadNum++;
	}
	
	public void initialWorkingThreadIterationNum( WorkingThreadData workingThreadData, int totalStep )
	{
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).getId() == workingThreadData.getId() )
			{
				threads.get(i).initialWorkingThreadIterationNum( totalStep );
				threads.get(i).start();
			}
	}
	
	public void notifyCalOneStepDone(int threadId) {
		slaveActor.notifyCalOneStepDone(threadId);
	}
	
	public void setDHTPeer(DHTPeer peer) {
		System.out.println("Slave create dht peer:" + peer.address);
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).getId() == peer.threadId) {
				threads.get(i).dhtPeer = new DHTPeer();
				threads.get(i).dhtPeer.installLocalPeer(peer);
			}
		}
	}
	
	public void connectToOtherPeers(int threadId)  {
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).getId() == threadId) {
				threads.get(i).dhtPeer.connectToOtherPeers();
			}
		}
	}
	
	public String getAddress() {
		return slaveActor.getIpAddress();
	}
	
	public void startReadFile() {
		System.out.println("Slave: start read file");
	}	
	
	public void startCalculation(int step) {
		for (int i = 0; i < threadNum; i++) {
			if (!threads.get(i).status.equals(Constant.IDLE)) {
				threads.get(i).startNewStep(step);	
			}
		}
	}
	
	//---------zzy-----------------------
	
	
	//----------jdc-----------------------
	public void openWriter(int threadNum) {
		String fileName = "Subgraph for workthread " + Integer.toString(threadNum);
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).getId() == threadNum )
		        threads.get(i).edgeFilePath = fileName;
		try {
			PrintWriter writer = new PrintWriter(fileName);
			writers.put( threadNum, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter(int threadNum) {
		try {
			writers.get( threadNum ).close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receiveSubgraph(Map<Long, List<Long>> edges, int threadNum ) {
		Iterator it = edges.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry) it.next();
		    Long toPoint = (Long)entry.getKey();
		    List<Long> fromPoints = (List<Long>)entry.getValue();
		    for (int i = 0; i < fromPoints.size(); i++) {
		    	writers.get( threadNum ).println(Long.toString(toPoint)+"\t"+fromPoints.get(i));
		    }
		}
	}
}
