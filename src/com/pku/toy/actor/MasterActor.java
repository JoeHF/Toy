package com.pku.toy.actor;

import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Address;

import com.pku.toy.Constant;
import com.pku.toy.logic.Master;
import com.pku.toy.model.FileModel;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.implement.MasterImpl;
import com.pku.toy.rmi.inter.IMaster;
import com.pku.toy.rmi.inter.ISlave;

public class MasterActor extends Thread {
	private String ip;
	private int port;
	private Master context;
	private Map<String, ISlave> slaveServices;
	
	public MasterActor(int _port, Master _context) {
		try {
			ip = Address.GetIpAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		port = _port;
		context = _context;
		slaveServices = new HashMap();
	}
	
	public void bindSlaveService(List<SlaveModel> slaveModels) {
		try {
			for (int i = 0; i < slaveModels.size(); i++) {
				String lookupString = "rmi://" + slaveModels.get(i).getIp() + ":" + Constant.SLAVE_PORT + "/Slave";
				ISlave slaveService = (ISlave)Naming.lookup(lookupString);
				System.out.println("bind slave service:" + slaveModels.get(i).getIp());
				slaveServices.put(slaveModels.get(i).getIp(), slaveService);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createWorkingThread(List<WorkingThreadData> workingThreadDatas) {
		try {
			for (int i = 0; i < workingThreadDatas.size(); i++) {
				ISlave slaveService = slaveServices.get(workingThreadDatas.get(i).getIp());
				slaveService.createWorkingThread(workingThreadDatas.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notifySlaveFetchFile(List<FileModel> fileModels) {
		try {
			for (int i = 0; i < fileModels.size(); i++) {
				ISlave slaveService = slaveServices.get(fileModels.get(i).getIp());
				slaveService.receiveFetchFile(fileModels.get(i).getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			IMaster masterActorService = new MasterImpl(this);
			LocateRegistry.createRegistry(port);
			Naming.rebind( "rmi://" + this.ip + ":" + port + "/Master", masterActorService);
			System.out.println("master actor bind ip address:" + getIpAddress());
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public List<WorkingThreadData> getWorkingThreadData() {
		List<WorkingThreadData> workingThreadDatas = new ArrayList<>();
		try {
			for (int i = 0; i < slaveServices.size(); i++) {
				if (slaveServices.get(i).getWorkingThreadDatas() != null) {
					workingThreadDatas.addAll(slaveServices.get(i).getWorkingThreadDatas());	
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workingThreadDatas;
	}
	
	public String getIpAddress() {
		return this.ip;
	}
	
}
