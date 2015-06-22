package com.pku.toy.logic;

import java.util.ArrayList;
import java.util.List;

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
	
	public Slave() {
		threadNum = 0;
		threads = new ArrayList<>();
	}
	
	public void startActor() {
		
		this.slaveActor = new SlaveActor(Constant.SLAVE_PORT, this);
		this.slaveActor.run();
	}
	
	public void createWorkingThread(WorkingThreadData workingThreadData) {
		System.out.println("slave " + getAddress() + "thread num:" + workingThreadData.getId() + " create working thread:" + workingThreadData.getStatus());
		WorkingThread thread = new WorkingThread(workingThreadData);
		threads.add(thread);
		threadNum++;
	}
	
	public void setDHTPeer(DHTPeer peer, PeerModel model ) {
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).getId() == model.threadId) {
				threads.get(i).dhtPeer = new DHTPeer();
				threads.get(i).dhtPeer.installLocalPeer(peer);
			}
		}
	}
	
	public void connectToOtherPeers( PeerModel model )  {
		for ( int i = 0 ; i < threadNum ; i++) {
			if (threads.get(i).getId() == model.threadId) {
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
	
	//---------zzy-----------------------
	
	
	//----------jdc-----------------------

}
