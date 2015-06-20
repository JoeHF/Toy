package com.pku.toy.rmi.implement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.pku.toy.rmi.inter.IMaster;


public class MasterImpl extends UnicastRemoteObject implements IMaster {

	public MasterImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

}
