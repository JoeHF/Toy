package com.pku.toy.test;

import java.net.SocketException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import util.Address;
import ZZY.IPrime;

import com.pku.toy.Constant;
import com.pku.toy.logic.Master;
import com.pku.toy.logic.Slave;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.rmi.inter.ISlave;

public class TestStart {
	public static void main( String[] args ) throws SocketException
	{
		List<SlaveModel> slaveModels = new ArrayList<SlaveModel>();
		SlaveModel slaveModel = new SlaveModel();
		slaveModel.setIp(Address.GetIpAddress());
		slaveModels.add(slaveModel);
		
		Slave slave = new Slave();
		slave.startEnv();
		
		System.out.println("start");
		Master master = new Master(slaveModels);
		master.startEnv();
		
		master.startReadFile();
		System.out.println("end");
		return;
	}
}
