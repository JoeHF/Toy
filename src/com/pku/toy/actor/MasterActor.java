package com.pku.toy.actor;

import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import util.Address;

import com.pku.toy.Constant;
import com.pku.toy.logic.Master;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.implement.MasterImpl;
import com.pku.toy.rmi.inter.IMaster;
import com.pku.toy.rmi.inter.ISlave;

public class MasterActor extends Thread {
	private String ip;
	private int port;
	private Master context;
	private List<ISlave> slaveServices;
	
	public MasterActor(int _port, Master _context) {
		try {
			ip = Address.GetIpAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		port = _port;
		context = _context;
		slaveServices = new ArrayList<>();
	}
	
	public void bindSlaveService(List<SlaveModel> slaveModels) {
		try {
			for (int i = 0; i < slaveModels.size(); i++) {
				String lookupString = "rmi://" + slaveModels.get(i).getIp() + ":" + Constant.SLAVE_PORT + "/Slave";
				ISlave slaveService = (ISlave)Naming.lookup(lookupString);
				slaveServices.add(slaveService);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notifyStartReadFile() {
		try {
			for (int i = 0; i < slaveServices.size(); i++) {
				slaveServices.get(i).notifyStartReadFile();
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
			IMaster masterActorService = new MasterImpl();
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
