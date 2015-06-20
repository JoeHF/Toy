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

	@Override
	public void notifyStartAll() throws RemoteException {
		
	}
	
	@Override
	public void notifyStartReadFile() throws RemoteException {
		slaveActor.receiveStartReadFile();
	}

	@Override
	public List<WorkingThreadData> getWorkingThreadDatas()
			throws RemoteException {
		System.out.println("slaveimpl: slave get working thread datas");
		return null;
	}

}