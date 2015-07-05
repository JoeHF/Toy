package com.pku.toy.dht;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Period;
import java.util.*;

import com.pku.toy.Constant;
import com.pku.toy.model.DHTPeerData;


public class DHTPeer extends UnicastRemoteObject implements IDHTPeer, Serializable
{

	
	private static long PRIME = 990001;
	
	// Local address
	public String address;
	public long    peerId;
	public int threadId;
	public int port;
	public int tag;
	
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
		System.out.println("===================**DHTPeer**====================");
		System.out.println("PeerId: " + peerId +  "\nAddress: " + address + "\nThreadId: " + threadId);
		System.out.print("Router: \n");
		for( Long key : router.keySet() )
			System.out.println("" + key + "-->" + router.get(key) );
//		System.out.print("remoteDHTPeers: \n");
//		for( Long key : remoteDHTPeers.keySet() )
//			try {
//				System.out.println("" + key + "-->" + remoteDHTPeers.get(key).getInfo() );
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		System.out.print("localHashMap: \n");
		for( Long key : localHashMap.keySet() )
			System.out.println("" + key + "-->" + localHashMap.get(key) );
		System.out.println("====================**DHTPeer**===================");
	}
	
	//--------------------------------zzy--------------
	@Override
	public String toString()
	{
		return "[PeerId=" + peerId + " , Address=" + address +"]";
	}
	
	public DHTPeerData getDhtPeerData()
	{
		DHTPeerData peerData = new DHTPeerData();
		peerData.address = new String( this.address );
		peerData.peerId  = this.peerId;
		peerData.threadId = this.threadId;
		peerData.port    = this.port;
		peerData.router = new TreeMap<>();
		for ( Long key : this.router.keySet() )
			peerData.router.put( new Long(key) , new String( this.router.get(key)) );
		return peerData;
	}
	
	public void setRouter(TreeMap<Long, String> router) 
	{
		this.router = new TreeMap<>();
		for ( long key : router.keySet() )
			this.router.put( key , router.get(key) );
		return;
	}
	
	public void clearData()
	{
		try {
			this.tag = Constant.PEER_DOWN;
			Naming.unbind( this.address );
			UnicastRemoteObject.unexportObject( this , true );
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void installLocalPeer( DHTPeerData peer )
	// install a local DHTPeer, fill RouteTable, create local RMI server.
	{
		this.address = peer.address;
		this.peerId  = peer.peerId;
		this.port    = peer.port;
		this.threadId = peer.threadId;
		this.setRouter( peer.router );
		localHashMap = new HashMap<Long, Double>();
		
		try 
		{
			System.out.println( this.getInfo() + " rebind: " + this.address );
			LocateRegistry.createRegistry( this.port );
			Naming.rebind( this.address ,  this );
		}
		catch ( Exception e ) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//--------------------------------jdc--------------
	public void reinstallLocalPeer( DHTPeerData peer )
	// install a local DHTPeer, fill RouteTable, create local RMI server.
	{
		this.address = peer.address;
		this.peerId  = peer.peerId;
		this.port    = peer.port;
		this.threadId = peer.threadId;
		this.setRouter( peer.router );
		localHashMap = new HashMap<Long, Double>();
		
		try 
		{
			System.out.println( this.getInfo() + " rebind: " + this.address );
			//LocateRegistry.createRegistry( this.port );
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
				System.out.println( this.getInfo() + " ---connect to---> " + peer.getInfo() );
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
	public Map<Long,Double>  getMaps(List<Long> keys) throws RemoteException 
	{
		// TODO Auto-generated method stub
		// Get operation in groups. Return a subMap containing all the keys, regardless of where the keys store.
		
		// get local maps
		List<Long>  keyList = new ArrayList<Long>();
		for ( long key : keys )
			if ( this.getPeerIdByKey(key) == this.peerId )
				keyList.add( key );
		HashMap<Long, Double> ret = (HashMap<Long, Double>)this.getLocalMaps( keyList );
		//System.out.println( this.getInfo() + "LocalKeyList: " + keyList +  " LocalMap: " + ret );
		
		// get remote maps
		for ( long routerKey : this.router.keySet() )
		{
			if ( routerKey == this.peerId ) continue;
			keyList = new ArrayList<Long>();
			for ( long key : keys )
				if ( this.getPeerIdByKey(key) == routerKey )
					keyList.add( key );
			if ( keyList.size()==0 ) continue;
			//System.out.println( this.getInfo() + remoteDHTPeers.get( routerKey ).getInfo() );
			HashMap<Long, Double> remoteMap = 
					(HashMap<Long, Double>)remoteDHTPeers.get( routerKey ).getLocalMaps( keyList );
			//System.out.println( this.getInfo() + "RomoteKey : " + routerKey + "  keyList: " + keyList + "  Map " + remoteMap );
			if ( remoteMap.containsKey( (long)Constant.PEER_DOWN ) )
				throw new RemoteException();
			for ( long key : remoteMap.keySet() )
				ret.put( key , remoteMap.get(key) );
			//System.out.println( this.getInfo() + "DHTPeer : " + peerId + " -- " + ret );
		}
		return ret;
	}
	
	@Override
	public  Map<Long,Double> getLocalMaps( List<Long> keys ) throws RemoteException
	{
		// TODO Auto-generated method stub
		// ensure that keys are all in localHashMap
		HashMap<Long,Double> ret = new HashMap<Long,Double>();
		if ( keys.size()==0 ) return ret;
		//System.out.println(this.getInfo() + "GetLocalMaps: " + keys );
		for ( long key : keys )
		{
			if ( localHashMap.containsKey(key) )
				ret.put(key, localHashMap.get(key) );
		}
		if ( this.tag == Constant.PEER_DOWN )  ret.put( (long)Constant.PEER_DOWN, (double)Constant.PEER_DOWN);
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


	public void writeCheckPoint()
	{
		// TODO Auto-generated method stub
		// 0.create Directory
		// 1.delete oldFile
		// 2.rename newFile to oldFile
		// 3.write newFile
		try 
		{
			// 0.create Directory
			File dirFile = new File( Constant.CHECKPOINT_DIR );
			if ( !dirFile.isDirectory() )
			{
				dirFile.mkdir();
			}
			//1.delete oldFile
		    File oldFile = new File( Constant.CHECKPOINT_PREFIX + this.peerId + Constant.CHECKPOINT_OLD_SUFFIX );
		    if ( oldFile.exists() )  oldFile.delete();
		    
			// 2.rename newFile to oldFile
		    File newFile = new File( Constant.CHECKPOINT_PREFIX + this.peerId + Constant.CHECKPOINT_NEW_SUFFIX );
		    if ( newFile.exists() ) newFile.renameTo( oldFile );
		    
			// 3.write newFile
		    newFile = new File( Constant.CHECKPOINT_PREFIX + this.peerId + Constant.CHECKPOINT_NEW_SUFFIX );
			BufferedWriter writer = new BufferedWriter( 
     		       new OutputStreamWriter( new FileOutputStream( newFile ), "UTF-8") );
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


	public void restoreFromCheckPoint()
	{
		// TODO Auto-generated method stub
		// 1.find oldFile
		// 2.find newFile
		String line;
		String[] map;
		try
		{
			// 1.find oldFile
			File file = new File( Constant.CHECKPOINT_PREFIX + this.peerId + Constant.CHECKPOINT_OLD_SUFFIX );
			if( !file.exists() )
			{
				file = new File( Constant.CHECKPOINT_PREFIX + this.peerId + Constant.CHECKPOINT_NEW_SUFFIX );
				if ( !file.exists() )  
					{
					    System.err.println("Peer " + this.peerId + " does not have CheckPoint!");
					    return;
					}
			}
			BufferedReader reader = new BufferedReader( 
     		       new InputStreamReader( new FileInputStream( file ), "UTF-8") );
			line = reader.readLine();
			//this.peerId = Long.parseLong( line );
			this.localHashMap = new HashMap<>();
			while ( true )
			{
				line = reader.readLine();
				if ( line==null ) break;
				map = line.split( "\t" );
				localHashMap.put( Long.parseLong(map[0]), Double.parseDouble(map[1]) );
			}
			System.out.println("Peer " + this.peerId + " restore Values from " + file.getAbsolutePath() );
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
		//System.out.println( "[" + this.toString() + localHashMap.size() + "]");
		return this.toString();
	}
	
	//-------------------------------------------------------------------------------------------------------
	public DHTPeer() throws RemoteException 
	{
		// TODO Auto-generated constructor stub
		super();
		this.tag = Constant.PEER_ALIVE;
	}

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
