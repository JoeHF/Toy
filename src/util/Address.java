package util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Address {
	public static String GetIpAddress() throws SocketException {
		
		String ip = null;
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        String[] temp = i.getHostAddress().split("\\.");
		        
		        //System.out.println(i.getHostAddress());
		        if (temp.length == 4 && (temp[0].equalsIgnoreCase("162") || temp[0].equalsIgnoreCase("172"))) {
					ip = i.getHostAddress();
				}
		    }
		}
		System.out.println("Util get ip address:" + ip);
		
		return ip;
	}
	
	

}
