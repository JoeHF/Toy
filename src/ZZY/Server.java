package ZZY;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;


/**
 * A TCP server that runs on port 9090.  When a client connects, it
 * sends the client the current date and time, then closes the
 * connection with that client.  Arguably just about the simplest
 * server you can write.
 */
public class Server extends Thread
{

    /**
     * Runs the server.
     * @throws SocketException 
     */
	public static String getIPAddress() throws SocketException
	{
		String ip = null;
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        if ( i.getHostAddress().substring(0, 7).equals("162.105") )
		        	ip = i.getHostAddress();
		    }
		}
		return ip;
	}
	
	@Override
	public void run()
	{
		try
		{
			IPrime primeService = new PrimeImpl();
			LocateRegistry.createRegistry( 9090 );
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			//String ip = "162.105.30.155";
			Naming.rebind( "rmi://" + ip + ":9090/IPrime", primeService);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}