package com.pku.toy.actor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
				slaveService.notifyBindMaster(ip);
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
	
	public void restartcreateWorkingThread(List<WorkingThreadData> workingThreadDatas) {
		try {
			for (int i = 0; i < workingThreadDatas.size(); i++) {
				ISlave slaveService = slaveServices.get(workingThreadDatas.get(i).getIp());
				slaveService.restartcreateWorkingThread(workingThreadDatas.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void initialWorkingThreadIterationNum( WorkingThreadData workingThreadData, int totalStep ) {
		try {
				ISlave slaveService = slaveServices.get( workingThreadData.getIp() );
				slaveService.initialWorkingThreadIterationNum( workingThreadData, totalStep );
		
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
	
	public void notifyWorkingThreadCalculation(int step) {
		try {
			Set set = slaveServices.entrySet();         
			Iterator i = set.iterator();         
			while(i.hasNext()){      
			     Map.Entry<String, ISlave> entry=(Map.Entry<String, ISlave>)i.next();    
			     entry.getValue().notifyCalculation(step);   
			}   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveCalOneStepDone(int threadId) {
		context.receiveOneStepDone(threadId);
	}
	
	public String getIpAddress() {
		return this.ip;
	}
	
	public void transferSubgraph(int threadNum, String threadIP, String fileName) {
		ISlave slaveService = slaveServices.get(threadIP);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			String[] s;
			List<Long> neighbors = new ArrayList<Long>();
			Map<Long, List<Long>> edges = new HashMap<>();
			int count = 0;
			String lastKey = "";
			
			slaveService.openWriter(threadNum);
			
			while(true) {
				line = reader.readLine();
				if (line == null) break;
				s = line.split("\t");
				if (lastKey.equals("") || s[0].equals(lastKey)) {
					neighbors.add(Long.parseLong(s[1]));
					lastKey = s[0];
				}
				else {
					edges.put(Long.parseLong(lastKey), neighbors);
					neighbors = new ArrayList<>();
					neighbors.add(Long.parseLong(s[1]));
					lastKey = s[0];
				}
				count++;
				if (count == 1000) {
					edges.put(Long.parseLong(lastKey), neighbors);
					slaveService.receiveSubgraph(edges,threadNum);
					edges = new HashMap<>();
					neighbors = new ArrayList<>();
					count = 0;
					lastKey = "";
				}
			}
			
			if (neighbors.size()!=0) {
				edges.put(Long.parseLong(lastKey), neighbors);
				slaveService.receiveSubgraph(edges,threadNum);
			}
			
			slaveService.closeWriter(threadNum);
		
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	public void transferDegreeFile(int threadNum, String threadIP, String fileName) {
		ISlave slaveService = slaveServices.get(threadIP);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			String[] s;
			List<Long> neighbors = new ArrayList<Long>();
			Map<Long, Long> edges = new HashMap<>();
			int count = 0;
			
			slaveService.openDegreeWriter(threadNum);
			
			while(true) {
				line = reader.readLine();
				if (line == null) break;
				s = line.split("\t");
				edges.put(Long.parseLong(s[0]) , Long.parseLong(s[1]));
				count++;
				if (count == 1000) {
					slaveService.receiveDegreeFile(edges,threadNum);
					edges = new HashMap<>();
					count = 0;
				}
			}
			
			if ( count!=0 ) {
				slaveService.receiveDegreeFile(edges,threadNum);
			}
			
			slaveService.closeDegreeWriter(threadNum);
		
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
	
	
	//--------------------------------zzy-------------------------------
	public void reportExceptionToMaster(int threadId) {
		context.reportExceptionToMaster(threadId);
	}
}
