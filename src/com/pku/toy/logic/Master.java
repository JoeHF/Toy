package com.pku.toy.logic;

import java.util.List;

import com.pku.toy.Constant;
import com.pku.toy.actor.MasterActor;
import com.pku.toy.model.SlaveModel;

public class Master {
	
	//-----------------hf------------------
	private MasterActor masterActor;
	private int slaveNum;
	private List<WorkingThread> threads;
	
	public Master(List<SlaveModel> slaveModels) {
		this.masterActor = new MasterActor(Constant.MASTER_PORT, this, slaveModels);
		slaveNum = slaveModels.size();
	}
	
	public void startEnv() {
		this.masterActor.run();
	}
	
	public String getAddress() {
		return masterActor.getIpAddress();
	}
	
	public void startReadFile() {
		masterActor.notifyStartReadFile();
	}
	
	private void wakeUpMasterActor() {
		
	}
	
	private void wakeUpSlaves() {
		
	}
	
	
	//---------zzy-----------------------
	
	
	//----------jdc-----------------------

}
