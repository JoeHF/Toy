package ZZY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class PrimeImpl extends UnicastRemoteObject implements IPrime { 
    public PrimeImpl() throws RemoteException {  
        super();  
        // TODO Auto-generated constructor stub  
    }

	@Override
	public List<Integer> GetPrimeList(int n) throws RemoteException 
	{
		// TODO Auto-generated method stub
		int i,j,c;
		ArrayList<Integer> list = new ArrayList<>();
		
		if ( n<2 ) return list;
		for ( i=2; i<=n; i++ )
		{
			c = 0;
			for ( j=2; j<i; j++ )
				if ( i%j==0 ) c++;
			if ( c==0 ) list.add(i);
		}
		return list;
	}
}