
package com.pku.toy.logic;

import javax.swing.table.DefaultTableCellRenderer;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.pku.toy.Constant;
import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.WorkingThreadData;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.rmi.RemoteException;
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
	public int id;
	public String status;
	public Object object = new Object();
	
	private int calculateStep = 0;
	private int semephone = 0;
	private Slave context;
		
	public WorkingThread(WorkingThreadData workingThreadData, Slave _context) {
		 id = workingThreadData.getId();
		 status = workingThreadData.getStatus();
		 context = _context;
		 totalNodes = -1;
	}
	
	public void startNewStep(int step) {
		synchronized (object) {
			object.notify();	
		}
	}
	
	public void run() {
		System.out.println("working thread " + id + ": start to run");
		//System.out.println( this.globalDegree );
		System.out.println("working thread " + id + ": read degree graph");

		this.calculate();
		
		System.out.println("working thread " + id + ": done calculation");	
	}
	
	public void calculate()
	{
		while(true) {	
			System.out.println("working thread " + id + ": wait");
			synchronized (object) {
				try {
					object.wait();
					calculateStep++;
					System.out.println("Thread " + id + " : work ite " + calculateStep );
					if ( totalNodes==-1 ) this.readDegree();
					this.updatePageRank( (long)this.totalNodes );
					if ( calculateStep<=2 || calculateStep%10==0 )
						this.dhtPeer.writeCheckPoint();
					display();
					if ( calculateStep == totalStep ) break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					context.notifyCalOneStepDone(id);
					this.clearData();
					this.stop();
					return;
				}	
			}
			
//			try {
//				sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				context.notifyCalOneStepDone(id);
//				this.clearData();
//				this.stop();
//				return;
//			}
			
			context.notifyCalOneStepDone(id);
		}		
	}
	
	//-----------------------zzy------------------
	
	private int totalStep;
	private int totalNodes;
	public Object object_Exception = new Object();
	
	public void display()
	{
		System.out.println("###############--WorkingThread--###################");
		System.out.println("WorkingThreadID: " + id + " Status: " + status);
		//System.out.println("edgeFilePath: " + edgeFilePath);
		//System.out.println("degrFilePath: " + degreeFilePath);
		System.out.println("currentStep: " + calculateStep );
		System.out.println("totalStep   : " + totalStep);
		//if ( dhtPeer!=null ) dhtPeer.displayPeer();
		System.out.println("###############--WorkingThread--###################\n");
	}
	
	public void initialWorkingThreadIterationNum( int totalStep, int currentStep )
	{
		this.totalStep = totalStep;
		this.calculateStep = currentStep;
		//this.display();
	}
	
	public void clearData()
	{
		dhtPeer.clearData();
		System.out.println("WorkingThread " + id + " stoped.");
	}
	
	public void initialDHTPeerHashmap()
	{
		String line;
		String[] map;
		HashMap<Long, Long> edgeMap = new HashMap<>();
		try
		{
			File file = new File( edgeFilePath );
			BufferedReader reader = new BufferedReader( 
     		       new InputStreamReader( new FileInputStream( file ), "UTF-8") );
			while ( true )
			{
				line = reader.readLine();
				if ( line==null ) break;
				map = line.split( "\t" );
				edgeMap.put( Long.parseLong(map[0]) , (long)1 );
			}
		}
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		for ( Long key : edgeMap.keySet() )
			this.dhtPeer.putLocalHashMap( key , 1.0 );
	}
	
	public void handleRemoteException()
	{
		context.reportExceptionToMaster( this.id );
				
//		synchronized ( object_Exception ) {
//			try {
//				object_Exception.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		this.calculate();
	}
	
	
	
	//-----------------------jdc-----------------
    private HashMap<Long, Long> globalDegree;
    public void readDegree() {
    	this.totalNodes = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(degreeFilePath));
			String line;
			String[] s;
			this.globalDegree = new HashMap<Long, Long>();
			while(true) {
				line = reader.readLine();
				if(line == null) break;
				s = line.split("\t");
				globalDegree.put(Long.parseLong(s[0]), Long.parseLong(s[1]));
				this.totalNodes++;
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    //d:damping factor N:point sum
	public void updatePageRank( Long N ) throws InterruptedException
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(edgeFilePath));
			String line;
			String[] s;
			String lastKey = "";
		    List<Long> neighbors = new ArrayList<Long>(); 
		    
		    List<String> nLastKey = new ArrayList<>();
		    List<List<Long>> vnNeighbors = new ArrayList<>();
		    
		    int lineCount = 0;
		    
			while(true) {
				line = reader.readLine();
				lineCount ++ ;
				if ( lineCount % 20000 == 0 ) System.out.println("Thread " + this.id + " reads " + lineCount + " lines in EdgeGraph.");
				if(line == null) s= new String("-10\t-20").split("\t");
				else             s = line.split("\t");
				if (lastKey.equals("") || s[0].equals(lastKey)) {
					if ( Long.parseLong(s[1])!=Constant.NON_FROMNODE ) neighbors.add(Long.parseLong(s[1]));
					lastKey = s[0];
				}
				else {
					if (vnNeighbors.size() != 100) {
						vnNeighbors.add(neighbors); 
						nLastKey.add(lastKey);
					}
					else {
						HashMap<Long, Boolean> members = new HashMap<>();
						List<Long> tNeighbors = new ArrayList<>();
						for (int i = 0; i < vnNeighbors.size(); i++) {
							List<Long> temp = vnNeighbors.get(i);
							for (int j = 0; j < temp.size(); j++) {
							    if (!members.containsKey(temp.get(j))) {
							    	members.put(temp.get(j), true);	
							    	tNeighbors.add(temp.get(j));
							    }
							}
						}	
						Map<Long, Double> neighborPageRank = dhtPeer.getMaps(tNeighbors);
							//System.out.println(  "Key : " + lastKey + "\tnNeighbors:" + neighbors );
							//System.out.println(  "neiborPR: " + neighborPageRank );
					    for (int i = 0; i < vnNeighbors.size(); i++) {
					    	List<Long> temp = vnNeighbors.get(i);
						    double sum = 0;
							for(Iterator<Long> iter = temp.iterator(); iter.hasNext() ;) {
								Long key = iter.next();
								//System.out.println( "&&" + key + neighborPageRank.get(key) + " " + globalDegree.get(key) );
								sum += 1.0*neighborPageRank.get(key)/globalDegree.get(key);
								
							} 	
							dhtPeer.put(Long.parseLong(nLastKey.get(i)), sum*Constant.DampingFactor
									                            +(1-Constant.DampingFactor)/N );
					    }
					}
					neighbors = new ArrayList<>();
					if ( Long.parseLong(s[1])!=Constant.NON_FROMNODE ) neighbors.add(Long.parseLong(s[1]));
					lastKey = s[0];
					if ( line==null ) break;
				}
				if ( this.isInterrupted() )
					throw new InterruptedException();
			}
			reader.close();
		}
		catch ( RemoteException e ) 
		{
			// TODO: handle exception
			System.out.println("working thread " + id + ": Catched Remote Exception!");
			handleRemoteException();
		}
		catch ( IOException e ) 
		{
			// TODO: handle exception
			if ( e.getClass() != RemoteException.class )
			e.printStackTrace();
		}
		
	}
	
	
}
