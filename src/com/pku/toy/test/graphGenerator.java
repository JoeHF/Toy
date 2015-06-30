package com.pku.toy.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class graphGenerator 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		int i,j,k,t,n;
		n = 15;
		for ( i=0; i<3; i++ )
		try 
		{
			File file = new File( "part"+i );
			BufferedWriter writer = new BufferedWriter( 
     		       new OutputStreamWriter( new FileOutputStream( file ), "UTF-8") );
			for ( j=0; j<5; j++ )
			{
				k = i*5 + j;
				if ( k%5==0 )
				{
					for ( t = (k+n-5)%n; t<=(k+n-1)%n; t++ )
						writer.write( "" + k + "\t" + t + "\n" );
				}
				else 
					writer.write( "" + k + "\t" + ((k+n-1)%n) + "\n" );
			}
			writer.flush();
			writer.close();
		} 
		catch ( IOException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
