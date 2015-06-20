package com.pku.toy.rmi.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.pku.toy.model.WorkingThreadData;

public interface ISlave extends Remote {
	public void notifyStartAll() throws RemoteException;
	public List<WorkingThreadData> getWorkingThreadDatas() throws RemoteException;
	public void notifyStartReadFile() throws RemoteException;
}