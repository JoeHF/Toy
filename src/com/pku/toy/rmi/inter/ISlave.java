package com.pku.toy.rmi.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;

public interface ISlave extends Remote {
	public void notifyStartAll() throws RemoteException;
	public List<WorkingThreadData> getWorkingThreadDatas() throws RemoteException;
	public void receiveFetchFile(String fileName) throws RemoteException;
	public void createWorkingThread(WorkingThreadData workingThreadData) throws RemoteException;
	public void setDHTPeer(DHTPeer peer) throws RemoteException;
	public void connectToOtherPeers(int threadId) throws RemoteException;
}
