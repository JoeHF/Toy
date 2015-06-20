package com.pku.toy.logic;

import java.util.List;

import com.pku.toy.Constant;
import com.pku.toy.actor.MasterActor;
import com.pku.toy.actor.SlaveActor;

public class Slave {
	
	//-----------------hf------------------
	private SlaveActor slaveActor;
	private int threadNum;
	private List<WorkingThread> threads;
	
	public Slave() {
		this.slaveActor = new SlaveActor(Constant.SLAVE_PORT, this);
	}
	
	public void startEnv() {
		this.slaveActor.run();
	}
	
	public String getAddress() {
		return slaveActor.getIpAddress();
	}
	
	public void startReadFile() {
		System.out.println("Slave: start read file");
	}
	
	private void wakeUpMasterActor() {
		
	}
	
	private void wakeUpSlaves() {
		
	}
	
	
	//---------zzy-----------------------
	
	
	//----------jdc-----------------------

}
