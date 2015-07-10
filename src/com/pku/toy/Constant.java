package com.pku.toy;

public class Constant {
	public static int PEER_NUM = 3;
	public static int THREAD_NUM = 4;
	public static int SLAVE_PORT = 9001;
	public static int MASTER_PORT = 9000;
	public static int PEER_PORT = 9002;
	
	public static String IDLE = "idle";
	public static String WORKING = "working";
	
	public static double DampingFactor = 0.85;
	
	public static String Edge_suffixString = "_edge";
	public static String Degree_suffixString = "_degree";
	
	// peer checkpoint file name : CHECKPOINT_PREFIX + Peer.peerId + CHECKPOINT_OLD_SUFFIX
	public static String CHECKPOINT_DIR = "./CheckPoint";  
	public static String CHECKPOINT_PREFIX = "./CheckPoint/Peer";  
	public static String CHECKPOINT_OLD_SUFFIX = "_old";
	public static String CHECKPOINT_NEW_SUFFIX = "_new";
	
	public static int PEER_ALIVE = -5;
	public static int PEER_DOWN  = -10;
	
	public static long RESTART_TIME = 2000;
	
	public static long NON_FROMNODE = -100;
	
	public static int  DISPLAY_ITE  =  200000;
}


