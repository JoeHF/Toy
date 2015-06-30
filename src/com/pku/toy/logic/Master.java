package com.pku.toy.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	private MasterActor masterActor;
	private int slaveNum;
	private List<WorkingThreadData> threads;
	private List<SlaveModel> slaveModels;
	private Set<Integer> idleThreadNumber = new HashSet<Integer>();
	private DHT dht;
	
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
	
	public void readFile(List<FileModel> fileModels) {
		masterActor.notifySlaveFetchFile(fileModels);
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
	
	//---------zzy-----------------------
	private long range;
	private void analyzeGraph( String graphFileName )
	// generate sub-edge-graph and degree graph  
	// graphFileName+"_edge"+"0/1/2"
	// graphFileName+"_degree"+"0/1/2"
	{
		
	}
	
	//----------jdc-----------------------

}
