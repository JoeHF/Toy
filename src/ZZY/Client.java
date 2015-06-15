package ZZY;

import java.rmi.Naming;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Trivial client for the date server.
 */
public class Client extends Thread
{

    /**
     * Runs the client as an application.  First it displays a dialog
     * box asking for the IP address or hostname of a host running
     * the date server, then connects to it and displays the date that
     * it serves.
     */
    
    @Override
	public void run()
	{
        String serverAddress = JOptionPane.showInputDialog(
                "Enter IP Address of a machine that is\n" +
                "running the date service on port 9090:");
        System.out.println(serverAddress);
        String lookupString = "rmi://" + serverAddress + ":9090/IPrime";
        try {
            IPrime primeService = (IPrime)Naming.lookup( lookupString );
            List<Integer> answer = primeService.GetPrimeList(100);
            JOptionPane.showMessageDialog(null, answer.toString());
        } catch( Exception e )
        {
        	e.printStackTrace();
        }
        System.exit(0);
	}
}