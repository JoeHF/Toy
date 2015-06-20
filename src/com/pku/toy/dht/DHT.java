package com.pku.toy.dht;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.pku.toy.IWorkingThread;
import ZZY.IPrime;

public class DHT implements Map<Long, Double>
{
	private static long PRIME = 990001;
	
	// DHT chord route table.
	private TreeMap<Long, String> router;

	public DHT() {
		// TODO Auto-generated constructor stub
	}
	
	public void setDHT( int peerNum, List<String> peerAddr,  long range)
	{
		long i,j;
		int k;
		
	    //create DHT Route Table
		//??????????????????????????---how to separate the key range, Master tell it? ---????????????????
	    router = new TreeMap<>();
	    j = range/peerAddr.size();
	    for ( i=1; i<peerAddr.size(); i++ )
	    	router.put( j*i, peerAddr.get((int) i-1) );
	    router.put( range ,  peerAddr.get( peerAddr.size()-1 ) );
	    
	    // create a DHT peer, let working thread copy its TreeMap.
	    DHTPeer peer = new DHTPeer();
	    peer.setRouter( router );
	    for ( long key : router.keySet() )
	    {
	    	String val = router.get( key );
	    	peer.address = val;
	    	peer.peerId  = key;
	    	try {
				IWorkingThread thread = (IWorkingThread)Naming.lookup( val );
				thread.setDHTPeer(peer);
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
				IWorkingThread thread = (IWorkingThread)Naming.lookup( val );
                thread.connectToOtherPeers();
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
