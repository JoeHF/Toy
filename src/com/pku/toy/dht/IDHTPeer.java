package com.pku.toy.dht;


import java.rmi.Remote;  
import java.rmi.RemoteException;  
import java.util.List;    
    
public interface IDhtPeer extends Remote
{  

	public Double get( Long key ) throws RemoteException;
	
	public List<Double> getKeys( List<Long> keys ) throws RemoteException;

	public void put( Long key ) throws RemoteException;

	public boolean containsKey() throws RemoteException;

	public void remove( Long key ) throws RemoteException;
	
	public int size() throws RemoteException;

	public void writeCheckPoint( String FileName ) throws RemoteException;

	public void restoreFromCheckPoint( String fileName ) throws RemoteException;
	
}
