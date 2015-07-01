package com.pku.toy.rmi.implement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import com.pku.toy.actor.SlaveActor;
import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.PeerModel;
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
		slaveActor.createWorkingThread(workingThreadData);
	}
	
	@Override
	public void receiveFetchFile(String fileName) throws RemoteException {
		slaveActor.startFetchFile(fileName);
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
	
	

	@Override
	public void setDHTPeer(DHTPeer peer) throws RemoteException {
		// TODO Auto-generated method stub
		slaveActor.setDHTPeer(peer);
	}

	@Override
	public void connectToOtherPeers(int threadId) throws RemoteException {
		// TODO Auto-generated method stub
		slaveActor.connectToOtherPeers(threadId);
	}

	@Override
	public void notifyCalculation(int step) throws RemoteException {
		// TODO Auto-generated method stub
		slaveActor.receiveStartCalculation(step);
		
	}

	@Override
	public void notifyBindMaster(String ip) throws RemoteException {
		// TODO Auto-generated method stub
		slaveActor.receiveBindMaster(ip);
		
	}
	
	public void openWriter(int threadNum) throws RemoteException {
		slaveActor.openWriter(threadNum);
	}
	
	public void closeWriter() throws RemoteException {
		slaveActor.closeWriter();
	}
	
	public void receiveSubgraph(Map<Long, List<Long>> edges) throws RemoteException {
		slaveActor.receiveSubgraph(edges);
	}
	

}
