package com.pku.toy.actor;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import ZZY.IPrime;
import ZZY.PrimeImpl;

import com.pku.toy.Master;
import com.pku.toy.model.WorkingThreadModel;

public class MasterActor extends Thread {
	private String ip;
	private int port;
	private Master context;
	
	public MasterActor(String _ip, int _port, Master _context) {
		ip = _ip;
		port = _port;
		context = _context;
	}
	
	@Override
	public void run()
	{
		try
		{
			IPrime primeService = new PrimeImpl();
			LocateRegistry.createRegistry( 9090 );
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			//String ip = "162.105.30.155";
			Naming.rebind( "rmi://" + ip + ":9090/IPrime", primeService);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public List<WorkingThreadModel> getWorkingEnvironment() {
		List<WorkingThreadModel> workingThreadSituations = new ArrayList<>();
		
		return workingThreadSituations;
	}
}
