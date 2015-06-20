package com.pku.toy.test;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.SocketException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.Address;
import ZZY.IPrime;

import com.pku.toy.Constant;
import com.pku.toy.logic.Master;
import com.pku.toy.logic.Slave;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.rmi.inter.ISlave;

public class TestStart {
	public int index = -1;

	public static void main( String[] args ) throws SocketException
	{
		javax.swing.SwingUtilities.invokeLater(new TestStart().new MyRunnable());
		
		
	}
	
	public class MyRunnable implements Runnable {
		public void run() {
			//Create and set up the window.
	        JFrame frame = new JFrame("CheckBox");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 
	        //Create and set up the content pane.
	        JComponent newContentPane = new JcheckBox();
	        newContentPane.setOpaque(true); //content panes must be opaque
	        frame.setContentPane(newContentPane);
	 
	        //Display the window.
	        frame.pack();
	        frame.setVisible(true);
		}
	}
	
	public void chooseIdent(int ident) {
		System.out.println("chooseIdent:" + ident);
		if(ident == 0) {
			/*
			List<SlaveModel> slaveModels = new ArrayList<SlaveModel>();
			SlaveModel slaveModel = new SlaveModel();
			try {
				slaveModel.setIp(Address.GetIpAddress());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			slaveModels.add(slaveModel);
			
			
			System.out.println("start");
			Master master = new Master(slaveModels);
			master.startEnv();
			
			master.startReadFile();*/
			System.out.println("end");
			return;
		} else {
			
			Slave slave = new Slave();
			slave.startEnv();
			System.out.println("slave start:" + slave.getAddress());
		}
		
			
	}
	
	public class JcheckBox extends JPanel
		    implements ItemListener {
		Checkbox masterButton;
		Checkbox slaveButton;
		
		public JcheckBox() {
			super(new BorderLayout());
			
			CheckboxGroup group = new CheckboxGroup();
			//Create the check boxes.
			masterButton = new Checkbox("Master", false, group);	
			slaveButton = new Checkbox("Slave", false, group);
			masterButton.addItemListener(this);
			slaveButton.addItemListener(this);
				
			//Put the check boxes in a column in a panel
			JPanel checkPanel = new JPanel(new GridLayout(0, 1));
			checkPanel.add(masterButton);
			checkPanel.add(slaveButton);
			
			add(checkPanel, BorderLayout.LINE_START);
			setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		}
		
		/** Listens to the check boxes. */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();
			
			if (source == masterButton) {	
				index = 0;
				chooseIdent(index);
			} else if (source == slaveButton) {
				index = 1;
				chooseIdent(index);
			}
			
		}
	}
	
}
