package com.pku.toy.dht;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

import com.pku.toy.Constant;


public class DHTPeer implements IDHTPeer, Serializable
{

	
	private static long PRIME = 990001;
	
	// Local address
	public String address;
	public long    peerId;
	public int threadId;
	public int port;
	
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
	
	public void displayPeer()
	{
		System.out.println("=======================================");
		System.out.println("PeerId: " + peerId +  "\nAddress: " + address + "\nThreadId: " + threadId);
		System.out.print("Router: ");
		for( Long key : router.keySet() )
			System.out.println("" + key + "-->" + router.get(key) );
		System.out.print("remoteDHTPeers: ");
		for( Long key : remoteDHTPeers.keySet() )
			System.out.println("" + key + "-->" + remoteDHTPeers.get(key) );
		System.out.print("localHashMap: ");
		for( Long key : localHashMap.keySet() )
			System.out.println("" + key + "-->" + localHashMap.get(key) );
		System.out.println("=======================================\n");
	}
	
	//--------------------------------zzy--------------
	@Override
	public String toString()
	{
		return "" + peerId + "---" + address;
	}
	
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
		this.port    = peer.port;
		this.setRouter( peer.router );
		localHashMap = new HashMap<Long, Double>();
		
		try 
		{
			System.out.println( "DHTRebind " + this.address );
			LocateRegistry.createRegistry( this.port );
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
				System.out.println( this.peerId + " ---> " + peer.getInfo() );
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
		double value;
		
		id = this.getPeerIdByKey( key );
		if ( id == this.peerId )
			return localHashMap.get( key );
		value = remoteDHTPeers.get( id ).get( key );
		return value;
	}


	@Override
	public Map<Long,Double> getMaps(List<Long> keys) throws RemoteException 
	{
		// TODO Auto-generated method stub
		// Get operation in groups. Return a subMap containing all the keys, regardless of where the keys store.
		
		// get local maps
		List<Long>  keyList = new ArrayList<Long>();
		for ( long key : keys )
			if ( this.getPeerIdByKey(key) == this.peerId )
				keyList.add( key );
		System.out.println( "DHTPeer : " + this.peerId );
		HashMap<Long, Double> ret = (HashMap<Long, Double>)this.getLocalMaps( keyList );
		System.out.println( "LocalKeyList: " + keyList +  "\nLocalMap: " + ret );
		
		// get remote maps
		for ( long routerKey : this.router.keySet() )
		{
			if ( routerKey == this.peerId ) continue;
			keyList.clear();
			for ( long key : keys )
				if ( this.getPeerIdByKey(key) == routerKey )
					keyList.add( key );
			HashMap<Long, Double> remoteMap = 
					(HashMap<Long, Double>)remoteDHTPeers.get( routerKey ).getLocalMaps( keyList );
			System.out.println("RomoteKey : " + routerKey + "  ++ " + keyList + " ++ Map " + remoteMap );
			for ( long key : remoteMap.keySet() )
				ret.put( key , remoteMap.get(key) );
			System.out.println( "DHTPeer : " + peerId + " -- " + ret);
		}
		return ret;
	}
	
	@Override
	public Map<Long,Double> getLocalMaps( List<Long> keys ) throws RemoteException
	{
		// TODO Auto-generated method stub
		// ensure that keys are all in localHashMap
		HashMap<Long,Double> ret = new HashMap<Long,Double>();
		System.out.println("GetLocalMaps: " + keys );
		for ( long key : keys )
		{
			if ( localHashMap.containsKey(key) )
				ret.put(key, localHashMap.get(key) );
		}
		return ret;
	}

	@Override
	public void put( Long key, Double value ) throws RemoteException
	{
		// TODO Auto-generated method stub
		// If key lie in local ID range, change it.
		if ( this.getPeerIdByKey(key) == this.peerId )
			this.localHashMap.put( new Long(key), new Double(value) );
	}
	
	public void putLocalHashMap( Long key, Double value )
	{
		this.localHashMap.put( new Long(key), new Double(value) );
	}


	@Override
	public boolean containsKey( Long key ) throws RemoteException
	{
		// TODO Auto-generated method stub
		// Judge if local peer contians key.
		return ( localHashMap.containsKey(key) );
	}


	@Override
	public void remove(Long key) throws RemoteException {
		// TODO Auto-generated method stub
		localHashMap.remove(key);
	}


	@Override
	public int size() throws RemoteException {
		// TODO Auto-generated method stub
		return localHashMap.size();
	}


	@Override
	public void writeCheckPoint( String filePath ) throws RemoteException 
	{
		// TODO Auto-generated method stub
		try 
		{
			File file = new File( filePath );
			BufferedWriter writer = new BufferedWriter( 
     		       new OutputStreamWriter( new FileOutputStream( file ), "UTF-8") );
			writer.write( "" + this.peerId + "\n");
			for ( long key : localHashMap.keySet() )
				writer.write( "" + key + "\t" + localHashMap.get(key) + "\n" );
			writer.flush();
			writer.close();
		} 
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	@Override
	public void restoreFromCheckPoint(String filePath) throws RemoteException 
	{
		// TODO Auto-generated method stub
		String line;
		String[] map;
		try
		{
			File file = new File( filePath );
			BufferedReader reader = new BufferedReader( 
     		       new InputStreamReader( new FileInputStream( file ), "UTF-8") );
			line = reader.readLine();
			this.peerId = Long.parseLong( line );
			this.localHashMap = new HashMap<>();
			while ( true )
			{
				line = reader.readLine();
				if ( line==null ) break;
				map = line.split( "\t" );
				localHashMap.put( Long.parseLong(map[0]), Double.parseDouble(map[1]) );
			}
		}
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public String getInfo() throws RemoteException
	{
		return this.toString();
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
