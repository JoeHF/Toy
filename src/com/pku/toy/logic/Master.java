package com.pku.toy.logic;

import java.util.List;

import com.pku.toy.Constant;
import com.pku.toy.actor.MasterActor;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;

public class Master {
	
	//-----------------hf------------------
	private MasterActor masterActor;
	private int slaveNum;
	private List<WorkingThread> threads;
	private List<SlaveModel> slaveModels;
	
	public Master() {

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
	
	public void readFile(String path) {
		masterActor.notifySlaveFetchFile();
	}
	
	public void createWorkingThread(List<WorkingThreadData> workingThreadDatas) {
		masterActor.createWorkingThread(workingThreadDatas);
	}
	
	//---------zzy-----------------------
	
	
	//----------jdc-----------------------

}
