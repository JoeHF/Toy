package ZZY;

import java.rmi.Remote;  
import java.rmi.RemoteException;  
import java.util.List;    
    
public interface IPrime extends Remote
{  
    public List<Integer> GetPrimeList( int n ) throws RemoteException;
}
