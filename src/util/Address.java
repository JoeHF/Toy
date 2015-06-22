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
		        if (temp.length == 4 && (!temp[0].equalsIgnoreCase("127"))) {
					ip = i.getHostAddress();
				}
		    }
		}
		
		return ip;
	}
	

}
