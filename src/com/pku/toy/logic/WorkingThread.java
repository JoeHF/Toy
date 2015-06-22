
package com.pku.toy.logic;

import javax.swing.table.DefaultTableCellRenderer;

import com.pku.toy.dht.DHTPeer;
import com.pku.toy.model.WorkingThreadData;

public class WorkingThread extends Thread {
	
	DHTPeer dhtPeer;
	
	//-----------------------hf-----------------
	private int id;
	private String status;
		
	public WorkingThread(WorkingThreadData workingThreadData) {
		 id = workingThreadData.getId();
		 status = workingThreadData.getStatus();
	}
	
	//-----------------------zzy------------------
	
	
	//-----------------------jdc-----------------

}
