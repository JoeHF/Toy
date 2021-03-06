package com.pku.toy.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
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
import com.pku.toy.model.DHTPeerData;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;

public class Slave {
	
	//-----------------hf------------------
	private SlaveActor slaveActor;
	private int threadNum;
	private List<WorkingThread> threads;
	private HashMap<Integer, PrintWriter>   writers = new HashMap<>();
	private HashMap<Integer, PrintWriter>   degreeWriters = new HashMap<>();
	
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
	
	public void initialWorkingThreadIterationNum( WorkingThreadData workingThreadData, int totalStep, int currentStep, boolean restart )
	{
		System.out.println( "slave : initialWorkingThreadIterationNum");
		for ( int i=0; i<threads.size(); i++ )
		{
			if ( threads.get(i).id == workingThreadData.getId() )
			{
				threads.get(i).initialWorkingThreadIterationNum( totalStep, currentStep );
				if ( restart==true ) threads.get(i).start();
			}
		}
	}
	
	public void notifyCalOneStepDone(int threadId) {
		slaveActor.notifyCalOneStepDone(threadId);
	}
	
	public void setDHTPeer( DHTPeerData peer) {
		System.out.println("Slave create dht peer:" + peer.address);
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).id == peer.threadId) {
				try {
					threads.get(i).dhtPeer = new DHTPeer();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.get(i).dhtPeer.installLocalPeer(peer);
				threads.get(i).initialDHTPeerHashmap();
			}
		}
	}
	
	public void connectToOtherPeers(int threadId)  {
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).id == threadId) {
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
	
	public void reportExceptionToMaster(int threadId )
	{
		slaveActor.reportExceptionToMaster(threadId);
	}
	
	public void killAThread( WorkingThreadData workingThreadData )
	{
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).id == workingThreadData.getId() )
			{
				System.out.println("Slave begin to interrupt WorkingThread " + workingThreadData.getId() );
				threads.get(i).interrupt();
				//threads.set( i , null);
			}
	}
	
	
	//----------jdc-----------------------
	public void openWriter(int threadNum) {
		String fileName = "Subgraph for workthread " + Integer.toString(threadNum);
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).id == threadNum )
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
			System.out.println( "Thread " + threadNum + " finished receive Edge File" );
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
	
	public void openDegreeWriter(int threadNum) {
		String fileName = "Degree for " + Integer.toString(threadNum);
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).id == threadNum )
		        threads.get(i).degreeFilePath = fileName;
		try {
			PrintWriter writer = new PrintWriter(fileName);
			degreeWriters.put( threadNum, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeDegreeWriter(int threadNum) {
		try {
			degreeWriters.get( threadNum ).close();
			System.out.println( "Thread " + threadNum + " finished receive Degree File.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receiveDegreeFile(Map<Long, Long> edges, int threadNum ) {
		Iterator it = edges.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry) it.next();
		    Long toPoint = (Long)entry.getKey();
		    Long fromPoint = (Long)entry.getValue();
		    degreeWriters.get( threadNum ).println(Long.toString(toPoint)+"\t"+Long.toString(fromPoint));
		}
	}
	
	public void restartcreateWorkingThread(WorkingThreadData workingThreadData) {
		System.out.println("slave " + getAddress() + " thread num:" + workingThreadData.getId() + " restart working thread:" + workingThreadData.getStatus());
		int downIndex = -1;
		for ( int i=0; i<threads.size(); i++ )
			if ( threads.get(i).id == workingThreadData.getId() )
			{
				downIndex = i;
				break;
			}
		WorkingThread thread = new WorkingThread(workingThreadData, this);
		WorkingThread oldThread = threads.get(downIndex);
		thread.degreeFilePath = oldThread.degreeFilePath;
		thread.edgeFilePath = oldThread.edgeFilePath;
		//thread.calculateStep & dhtPeer
		threads.set( downIndex, thread);
		threads.get(downIndex).start();
	}
	
	public void resetDHTPeer( DHTPeerData peer) {
		System.out.println("Slave reset dht peer:" + peer.address);
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).id == peer.threadId) {
				try {
					threads.get(i).dhtPeer = new DHTPeer();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.get(i).dhtPeer.reinstallLocalPeer(peer);
				threads.get(i).dhtPeer.restoreFromCheckPoint();
				threads.get(i).dhtPeer.connectToOtherPeers();
				System.out.println( "ThreadID: " +threads.get(i).id + " has restarted and resetDHTPeer.");
				threads.get(i).display();
				break;
			}
		}
		
	}
	
	
	
}
