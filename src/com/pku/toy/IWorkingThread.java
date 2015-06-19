package com.pku.toy;

import java.rmi.Remote;  
import java.rmi.RemoteException;  
import java.util.List;    

import com.pku.toy.dht.DHTPeer;


public interface IWorkingThread extends Remote
{
	public void setDHTPeer( DHTPeer peer ) throws RemoteException;
}
