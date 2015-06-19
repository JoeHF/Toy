package com.pku.toy.dht;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DHTPeer implements IDHTPeer
{

	private static long PRIME = 990001;
	
	// Local address
	public  String  address;
	public  long    peerId;
	
	// DHT chord route table.
	private TreeMap<Long, String>    router;
	
	// RMI Remote DHTPeers
	private HashMap<Long, IDHTPeer>  remoteDHTPeers;
	
	// Local HashMap.
	private HashMap<Long, Double> localHashMap;
	
	private  long getPeerIdByKey( Long key )
	// Get the peerId that contain the key.
	{
		for ( long k : router.keySet() )
			if ( k >= key ) return k;
		return -1;
	}
	
	//--------------------------------zzy--------------
	
	public void setRouter(TreeMap<Long, String> router) 
	{
		this.router = new TreeMap<>();
		for ( long key : router.keySet() )
			this.router.put( key , router.get(key) );
		return;
	}
	
	public void installLocalPeer( DHTPeer peer )
	// install a local DHTPeer, fill RouteTable, create local RMI server.
	{
		this.address = peer.address;
		this.peerId  = peer.peerId;
		this.setRouter( peer.router );
		localHashMap = new HashMap<Long, Double>();
		
		try 
		{
			Naming.rebind( this.address ,  this );
		}
		catch ( Exception e ) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectToOtherPeers()
	// After all the peers are installed, DHT notice all peers to create RMI client object.
	{
		this.remoteDHTPeers = new HashMap<>();
		for ( long key : this.router.keySet() )
		{
			String lookupString = router.get(key);
			try 
			{
				IDHTPeer peer = (IDHTPeer)Naming.lookup( lookupString );
				this.remoteDHTPeers.put( key, peer );
			}
            catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public Double get(Long key) throws RemoteException 
	{
		// TODO Auto-generated method stub
		// If local peer contains key, return value; else, get value from other peers.
		long id;
		
		id = this.getPeerIdByKey( key );
		if ( id == this.peerId )
			return localHashMap.get( key );
		return null;
	}


	@Override
	public List<Double> getKeys(List<Long> keys) throws RemoteException 
	{
		// TODO Auto-generated method stub
		// get operation in groups.
		return null;
	}


	@Override
	public void put(Long key) throws RemoteException 
	{
		// TODO Auto-generated method stub
		// If key lie in local ID range, change it.
		
	}


	@Override
	public boolean containsKey() throws RemoteException 
	{
		// TODO Auto-generated method stub
		// Judge if local peer contians key.
		return false;
	}


	@Override
	public void remove(Long key) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int size() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeCheckPoint(String FileName) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void restoreFromCheckPoint(String fileName) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	//-------------------------------------------------------------------------------------------------------
	public DHTPeer() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
