package com.pku.toy.rmi.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.DHTPeerData;
import com.pku.toy.model.PeerModel;
import com.pku.toy.model.WorkingThreadData;

public interface ISlave extends Remote {
	public void notifyStartAll() throws RemoteException;
	public List<WorkingThreadData> getWorkingThreadDatas() throws RemoteException;
	public void receiveFetchFile(String fileName) throws RemoteException;
	public void createWorkingThread(WorkingThreadData workingThreadData) throws RemoteException;
	public void restartcreateWorkingThread(WorkingThreadData workingThreadData) throws RemoteException;
	public void setDHTPeer(DHTPeerData peer) throws RemoteException;
	public void resetDHTPeer(DHTPeerData peer) throws RemoteException;
	public void connectToOtherPeers(int threadId) throws RemoteException;
	public void notifyCalculation(int step) throws RemoteException;
	public void notifyBindMaster(String ip) throws RemoteException;
	public void openWriter(int threadNum) throws RemoteException;
	public void closeWriter(int threadNum) throws RemoteException;
	public void receiveSubgraph(Map<Long, List<Long>> edges,int threadNum) throws RemoteException;
	public void openDegreeWriter(int threadNum) throws RemoteException;
	public void closeDegreeWriter(int threadNum) throws RemoteException;
	public void receiveDegreeFile(Map<Long, Long> edges,int threadNum) throws RemoteException;
	public void initialWorkingThreadIterationNum( WorkingThreadData workingThreadData, int totalStep, int currentStep, boolean restart ) throws RemoteException;
    public void killAThread(WorkingThreadData workingThreadData) throws RemoteException;
}
