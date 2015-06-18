package com.pku.toy.dht;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DHT implements Map<Long, Double>
{
	private static long PRIME = 990001;
	
	// DHT chord route table.
	private TreeMap<Long, String> router;

	public DHT() {
		// TODO Auto-generated constructor stub
	}
	
	public void setDHT( int peerNum, long range )
	{
		long i,j;
		
	    //??????  create DHTPeers and rebind them.
	    router = new TreeMap<>();
	    j = range/peerNum;
	    for ( i=1; i<peerNum; i++ )
	    	router.put( j*i, "" );
	    router.put( range ,  "" );
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
