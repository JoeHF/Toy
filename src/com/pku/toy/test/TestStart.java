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
import com.pku.toy.Constant.WorkingThreadStatus;
import com.pku.toy.logic.Master;
import com.pku.toy.logic.Slave;
import com.pku.toy.model.SlaveModel;
import com.pku.toy.model.WorkingThreadData;
import com.pku.toy.rmi.inter.ISlave;

public class TestStart {
	public int ident = -1; //ident 0表示是启动slave ident1表示是启动master
	private Master master;
	private Slave slave;
	private List<String> ipList = new ArrayList<>();
	private String ip1 = "172.17.2.177";
	private String ip2 = "172.17.2.233";

	public static void main( String[] args ) throws SocketException
	{
		javax.swing.SwingUtilities.invokeLater(new TestStart().new MyRunnable());	
	}
	
	public class MyRunnable implements Runnable {
		public void run() {
			//Create and set up the window.
	        JFrame frame = new JFrame("环境配置");
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
		Button test;
		
		public JcheckBox() {
			super(new BorderLayout());
			
			CheckboxGroup group = new CheckboxGroup();
			//Create the check boxes.
			masterCheckbox = new Checkbox("Master", false, group);	
			slaveCheckbox = new Checkbox("Slave", false, group);
			startActorButton = new Button("启动守护线程");
			ipList.add(ip1);
			ipList.add(ip1);
			ipList.add(ip2);
			ipList.add(ip2);
			slaveIp1 = new TextField(ip1); //测试ip
			slaveIp1.setEditable(true);
			slaveIp2 = new TextField(ip2); //测试ip
			slaveIp2.setEditable(true);
			text1 = new Label("第一台主机ip");
			text2 = new Label("第二台主机ip");
			settingEnv = new Button("设置集群环境");
			test = new Button("测试联机环境");
			
			masterCheckbox.addItemListener(this);
			slaveCheckbox.addItemListener(this);
				
			//Put the check boxes in a column in a panel
			JPanel checkPanel1 = new JPanel(new GridLayout(2, 2));
			checkPanel1.add(masterCheckbox);
			checkPanel1.add(slaveCheckbox);
			checkPanel1.add(startActorButton);
			startActorButton.addActionListener(this);
			
			JPanel checkPanel2 = new JPanel(new GridLayout(3, 2));
			checkPanel2.add(text1);
			checkPanel2.add(text2);		
			checkPanel2.add(slaveIp1);
			checkPanel2.add(slaveIp2);
			checkPanel2.add(settingEnv);
			settingEnv.addActionListener(this);
			
			//JPanel checkPanel3 = new JPanel(new GridLayout(3, 2));
			checkPanel2.add(test);
			test.addActionListener(this);
			
			add(checkPanel1, BorderLayout.NORTH);
			add(checkPanel2, BorderLayout.SOUTH);
			//add(checkPanel3, BorderLayout.EAST);
			setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
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
			} else if (source == test) {
				test();
			}
		}
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
	
	public void test() {
		Random random = new Random();
		int idleNum = random.nextInt() % 4;
		List<WorkingThreadData> workingThreadDatas = new ArrayList<>();
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
			if (i != idleNum) {
				WorkingThreadData workingThreadData = new WorkingThreadData(ipList.get(i), i, WorkingThreadStatus.Working);
				workingThreadDatas.add(workingThreadData);
			} else {
				WorkingThreadData workingThreadData = new WorkingThreadData(ipList.get(i), i, WorkingThreadStatus.Idle);
				workingThreadDatas.add(workingThreadData);
			}
		}
		
		master.createWorkingThread(workingThreadDatas);
	}
	
}
