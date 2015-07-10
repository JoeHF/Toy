package util;

import java.net.*;
import java.util.Enumeration;

public class Address {
	public static String GetIpAddress() throws SocketException 
	{	
		String ret=null;
		for (
			    final Enumeration< NetworkInterface > interfaces =
			        NetworkInterface.getNetworkInterfaces( );
			    interfaces.hasMoreElements( );
			)
			{
			    final NetworkInterface cur = interfaces.nextElement( );

			    if ( cur.isLoopback( ) )  continue;

			    for ( final InterfaceAddress addr : cur.getInterfaceAddresses( ) )
			    {
			        final InetAddress inet_addr = addr.getAddress( );
			        if ( !( inet_addr instanceof Inet4Address ) )
			            continue;
			        ret = new String ( inet_addr.getHostAddress() );
					System.out.println("Util get ip address:" + ret );
			    }
			}
		return ret;
	}
	
	public static String GetIpAddress_Old() throws SocketException
	{
		String ip = null;
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    System.out.println( n );
		    System.out.println( n.isLoopback() );
		    Enumeration ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        System.out.println( i.getHostAddress() );
		        System.out.println( i instanceof Inet4Address);
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
	
	public static void main(String[] args)
	{
		try {
			Address.GetIpAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
