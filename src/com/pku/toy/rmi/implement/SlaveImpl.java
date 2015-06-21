package com.pku.toy.rmi.implement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.pku.toy.actor.SlaveActor;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.inter.ISlave;

public class SlaveImpl extends UnicastRemoteObject implements ISlave{
	private SlaveActor slaveActor;
	public SlaveImpl(SlaveActor _slaveActor) throws RemoteException {
		super();
		this.slaveActor = _slaveActor;
		// TODO Auto-generated constructor stub
	}

	public void createWorkingThread(WorkingThreadData workingThreadData) throws RemoteException {
		System.out.println("debug:slave impl create working thread");
		slaveActor.createWorkingThread(workingThreadData);
	}
	
	@Override
	public void receiveFetchFile() throws RemoteException {
		slaveActor.startFetchFile();
	}

	@Override
	public List<WorkingThreadData> getWorkingThreadDatas()
			throws RemoteException {
		System.out.println("slaveimpl: slave get working thread datas");
		return null;
	}

	@Override
	public void notifyStartAll() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
