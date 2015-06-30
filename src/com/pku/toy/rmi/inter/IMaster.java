package com.pku.toy.rmi.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.pku.toy.model.WorkingThreadData;

public interface IMaster extends Remote{
	public Map<Integer, Integer> getGraphData(String file) throws RemoteException;
	public void notifyMasterOneStepDone(int threadId) throws RemoteException;
}
