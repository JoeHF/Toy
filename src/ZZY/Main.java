package ZZY;

import ZZY.Server;

import java.net.SocketException;


public class Main {

	public Main() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public static void main( String[] args ) throws SocketException
	{
		String ip;
		
		ip = Server.getIPAddress();
		System.out.println( ip );
		System.setProperty("java.rmi.server.hostname",ip);
		
		Server server = new Server();
		//Client client = new Client();
		
		server.start();
		//client.start();
		
		System.out.println("Good Bad");
		return;
	}

}
