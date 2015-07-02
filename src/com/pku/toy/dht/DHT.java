package com.pku.toy.dht;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.modelmbean.ModelMBean;

import com.pku.toy.model.PeerModel;
import com.pku.toy.rmi.inter.ISlave;

import ZZY.IPrime;

public class DHT implements Map<Long, Double>
{
	private static long PRIME = 990001;
	
	// DHT chord route table.
	private TreeMap<Long, String> router;

	public DHT() {
		// TODO Auto-generated constructor stub
	}
	
	public static List<Long> separateKeyRange( long range, int peerNum )
	{
		List<Long>  ret = new ArrayList<Long>();
		long i,j;
		
	    j = range/peerNum;
	    for ( i=1; i<peerNum; i++ )
	    	ret.add( j*i );
	    ret.add(range);
	    return ret;
	}
	
	public void setDHT( List<PeerModel> peerAddr, long range )
	{
		long i,j,slaveId;
		int  x;
		int k;
		
	    //create DHT Route Table
		//??????????????????????????---how to separate the key range, Master tell it? ---????????????????
	    router = new TreeMap<>();
	    j = range/peerAddr.size();
	    for ( i=1; i<peerAddr.size(); i++ )
	    	router.put( j*i, peerAddr.get((int) i-1).peerAddress );
	    router.put( range ,  peerAddr.get( peerAddr.size()-1 ).peerAddress );
	    
	    // create a DHT peer, let working thread copy its TreeMap.
	    DHTPeer peer = new DHTPeer();
	    peer.setRouter( router );

	    for ( long key : router.keySet() )
	    {
	    	String val = router.get( key );
	    	peer.address = val;
	    	peer.peerId  = key;
	    	try {
	    		PeerModel targetModel = null;
	    		for ( PeerModel model : peerAddr )
	    			if ( model.peerAddress.equals( val ) )
	    			{
	    				targetModel = model;
	    				break;
	    			}
	    		
	    		peer.threadId = targetModel.threadId;
				ISlave slave = (ISlave)Naming.lookup( targetModel.slaveService );
				slave.setDHTPeer(peer);
			}
	    	catch ( Exception e )
	    	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    for ( long key : router.keySet() )
	    {
	    	String val = router.get( key );
	    	peer.address = val;
	    	peer.peerId  = key;
	    	try {
	    		PeerModel targetModel = null;
	    		for ( PeerModel model : peerAddr )
	    			if ( model.peerAddress.equals( val ) )
	    			{
	    				targetModel = model;
	    				break;
	    			}
				ISlave slave = (ISlave)Naming.lookup( targetModel.slaveService );
				slave.connectToOtherPeers(targetModel.threadId);
			}
	    	catch ( Exception e )
	    	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Double get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double put(Long key, Double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends Long, ? extends Double> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Double> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<Long, Double>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

}
