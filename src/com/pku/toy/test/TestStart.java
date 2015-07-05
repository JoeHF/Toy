package com.pku.toy.test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.SocketException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import util.Address;
import ZZY.IPrime;

import com.pku.toy.Constant;
import com.pku.toy.logic.Master;
import com.pku.toy.logic.Slave;
import com.pku.toy.model.FileModel;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.inter.ISlave;

public class TestStart {
	public int ident = -1; //ident 0琛ㄧず鏄惎鍔╯lave ident1琛ㄧず鏄惎鍔╩aster
	private Master master;
	private Slave slave;
	private List<String> ipList = new ArrayList<>();
	private String ip1 = "162.105.30.234";
	private String ip2 = "162.105.30.122";
	private String fileName;

	public static void main( String[] args ) throws SocketException
	{
		javax.swing.SwingUtilities.invokeLater(new TestStart().new MyRunnable());	
	}
	
	public class MyRunnable implements Runnable {
		public void run() {
			//Create and set up the window.
	        JFrame frame = new JFrame("Pagerank");
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
	
	
	
	public class JcheckBox extends JPanel
		    implements ItemListener, ActionListener {
		Checkbox masterCheckbox;
		Checkbox slaveCheckbox;
		Button startActorButton;
		Label text1;
		Label text2;
		TextField slaveIp1;
		TextField slaveIp2;
		Button settingEnv;
		Button createWorkingThread;
		Label file;
		TextField filePath;
		Label empty;
		Button readFile;
		Button startCalculation;
		TextField downThreadId;
		Button killThread;
		
		public JcheckBox() {
			super(new BorderLayout());
			
			CheckboxGroup group = new CheckboxGroup();
			//Create the check boxes.
			empty = new Label();
			masterCheckbox = new Checkbox("Master", false, group);	
			slaveCheckbox = new Checkbox("Slave", false, group);
			startActorButton = new Button("start deamon thread");
			
			slaveIp1 = new TextField(ip1); 
			slaveIp1.setEditable(true);
			slaveIp2 = new TextField(ip2); 
			slaveIp2.setEditable(true);
			text1 = new Label("first node ip");
			text2 = new Label("second node ip");
			settingEnv = new Button("initialize cluster");
			createWorkingThread = new Button("create working thread");
			file = new Label("graph file name");
			filePath = new TextField("graph.txt");
			readFile = new Button("read graph");
			startCalculation = new Button("begin to calculate");
			downThreadId = new TextField("Thread Id(0..3)");
			killThread = new Button("kill this thread");
			
			masterCheckbox.addItemListener(this);
			slaveCheckbox.addItemListener(this);
				
			//Put the check boxes in a column in a panel
			JPanel checkPanel1 = new JPanel(new GridLayout(2, 2, 10, 10));
			checkPanel1.add(masterCheckbox);
			checkPanel1.add(slaveCheckbox);
			checkPanel1.add(startActorButton);
			startActorButton.addActionListener(this);
			
			JPanel checkPanel2 = new JPanel(new GridLayout(7, 2, 10, 10));
			checkPanel2.add(text1);
			checkPanel2.add(text2);		
			checkPanel2.add(slaveIp1);
			checkPanel2.add(slaveIp2);
			checkPanel2.add(settingEnv);
			settingEnv.addActionListener(this);
			checkPanel2.add(createWorkingThread);
			createWorkingThread.addActionListener(this);
			checkPanel2.add(file);
			checkPanel2.add(empty);
			checkPanel2.add(filePath);
			checkPanel2.add(readFile);
			readFile.addActionListener(this);
			checkPanel2.add(startCalculation);
			startCalculation.addActionListener(this);
			checkPanel2.add( new JPanel() );
			checkPanel2.add(downThreadId);
			checkPanel2.add(killThread);
			killThread.addActionListener(this);
			
			
			add(checkPanel1, BorderLayout.NORTH);
			add(checkPanel2, BorderLayout.SOUTH);
			setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		}
		
		/** Listens to the check boxes. */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();		
			if (source == masterCheckbox) {	
				ident = 1;
			} else if (source == slaveCheckbox) {
				ident = 0;
			}		
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == startActorButton) {
				if (ident == 0) {
					startSlaveActor();	
				} else {
					startMasterActor();
				}
			} else if (source == settingEnv) {
				System.out.println("slave1 ip:" + slaveIp1.getText());
				System.out.println("slave2 ip:" + slaveIp2.getText());
				List<SlaveModel> slaveModels = new ArrayList<SlaveModel>();
				SlaveModel slaveModel = new SlaveModel();
				slaveModel.setIp(slaveIp1.getText());			
				slaveModels.add(slaveModel);
				slaveModel = new SlaveModel();
				slaveModel.setIp(slaveIp2.getText());
				slaveModels.add(slaveModel);
				startEnv(slaveModels);
				ipList.add(slaveIp1.getText());
				ipList.add(slaveIp1.getText());
				ipList.add(slaveIp2.getText());
				ipList.add(slaveIp2.getText());
			} else if (source == createWorkingThread) {
				createWorkingThread();
			} else if (source == readFile) {
				fileName = filePath.getText();
				readFile(filePath.getText());
			} else if (source == startCalculation) {
				startCalculation();
			} else if (source == killThread){
				String retString = killAThread( downThreadId.getText() );
				downThreadId.setText( retString );
			}
		}
	}
	
	public void startCalculation() {
		
		master.initialWorkingThreadIterationNum( 0 , 50 ,true);
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		master.startCalculate(0,50);
	}
	public void startSlaveActor() {
		slave = new Slave();
		try {
			slave.startActor();
		} catch (Exception e) {
			System.out.println("Warning:slave port has been occupied");
		}
	}
	
	public void startMasterActor() {
		master = new Master();
		try {
			master.startActor();
		} catch (Exception e) {
			System.out.println("Warning:master port has been occupied");
		}
	}
	
	public void startEnv(List<SlaveModel> slaveModels) {
		master.startEnv(slaveModels);
	}
	
	public void readFile(String filePath) {
		List<FileModel> fileModels = new ArrayList<>();
		for (int i = 0; i < Constant.THREAD_NUM; i++) {
			if (!master.isIdleThread(i)) {
				FileModel fileModel = new FileModel(ipList.get(i), fileName + "_part" + i);
				fileModels.add(fileModel);
			}
		}
		
		master.readFile(filePath);
	}
	
	public void createWorkingThread() {
		Random random = new Random();
		int idleNum = Math.abs(random.nextInt()) % 4;
		System.out.println("Idle thread num:" + idleNum);
		
		List<WorkingThreadData> workingThreadDatas = new ArrayList<>();
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
			if (i != idleNum) {
				WorkingThreadData workingThreadData = new WorkingThreadData(ipList.get(i), i, Constant.WORKING);
				workingThreadDatas.add(workingThreadData);
			} else {
				System.out.println("idle thread number is:" + i);
				WorkingThreadData workingThreadData = new WorkingThreadData(ipList.get(i), i, Constant.IDLE);
				workingThreadDatas.add(workingThreadData);
			}
		}
		
		master.createWorkingThread(workingThreadDatas);
	}
	
	public String killAThread( String threadId )
	{
		try
		{
		    int id = Integer.parseInt( threadId );
			return master.killAThread( id );
		}
		catch ( NumberFormatException e )
		{
			return "Invalid Input";
		}
	}
	
}
