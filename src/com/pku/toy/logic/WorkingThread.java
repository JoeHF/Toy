
package com.pku.toy.logic;

import javax.swing.table.DefaultTableCellRenderer;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.pku.toy.Constant;
import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.WorkingThreadData;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator.OfDouble;


public class WorkingThread extends Thread {
	
	public DHTPeer dhtPeer;
	public String  edgeFilePath;
	public String  degreeFilePath;
	
	//-----------------------hf-----------------
	private int id;
	public String status;
	public Object object = new Object();
	
	private int calculateStep = 0;
	private int semephone = 0;
		
	public WorkingThread(WorkingThreadData workingThreadData) {
		 id = workingThreadData.getId();
		 status = workingThreadData.getStatus();
	}
	
	public void startNewStep(int step) {
		synchronized (object) {
			object.notify();	
		}
	}
	
	public void run() {
		System.out.println("working thread " + id + ": start to run");
		while(true) {	
			System.out.println("working thread " + id + ": wait");
			synchronized (object) {
				try {
					object.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			System.out.println("working thread " + id + ": done calculation");		
		}
	}
	
	//-----------------------zzy------------------
	
	
	//-----------------------jdc-----------------
    private HashMap<Long, Long> globalDegree;
    public void readDegree() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Degree.txt"));
			String line;
			String[] s;
			this.globalDegree = new HashMap<Long, Long>();
			while(true) {
				line = reader.readLine();
				if(line == null) break;
				s = line.split("\t");
				globalDegree.put(Long.parseLong(s[0]), Long.parseLong(s[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    //d:damping factor N:point sum
	public void updatePageRank( Long N ){
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Neighbors.txt"));
			String line;
			String[] s;
			String lastKey = "";
		    List<Long> neighbors = new ArrayList<Long>(); 
			while(true) {
				line = reader.readLine();
				if(line == null) break;
				s = line.split("\t");
				if (lastKey.equals("") || s[0].equals(lastKey)) {
					neighbors.add(Long.parseLong(s[1]));
					lastKey = s[0];
				}
				else {
					Map<Long, Double> neighborPageRank = dhtPeer.getMaps(neighbors);
					double sum = 0;
					for(Iterator<Long> iter = neighbors.iterator(); iter.hasNext();) {
						Long key = iter.next();
						sum += 1.0*neighborPageRank.get(key)/globalDegree.get(key);
					} 	
					dhtPeer.put(Long.parseLong(lastKey), sum*Constant.DampingFactor
							                            +(1-Constant.DampingFactor)/N );
					lastKey = s[0];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
