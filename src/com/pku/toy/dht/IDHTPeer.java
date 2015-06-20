package com.pku.toy.dht;


import java.rmi.Remote;  
import java.rmi.RemoteException;  
import java.util.*;   
    
public interface IDHTPeer extends Remote
{  

	public Double get( Long key ) throws RemoteException;
	
	public Map<Long,Double> getMaps( List<Long> keys ) throws RemoteException;
	
	public Map<Long,Double> getLocalMaps( List<Long> keys ) throws RemoteException;

	public void put( Long key, Double value ) throws RemoteException;

	public boolean containsKey( Long key ) throws RemoteException;

	public void remove( Long key ) throws RemoteException;
	
	public int size() throws RemoteException;

	public void writeCheckPoint( String filePath ) throws RemoteException;

	public void restoreFromCheckPoint( String filePath ) throws RemoteException;
	
}
