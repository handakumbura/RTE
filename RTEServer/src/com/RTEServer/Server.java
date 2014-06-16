package com.RTEServer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Server {
private ServerSocket serverSocket;
private Socket socket;
private int port;
private StringBuffer clientMsg;

public void start()
{
	port = 19999;
	try
	{
		serverSocket = new ServerSocket(port);
		clientMsg = new StringBuffer();
		System.out.println("-- server started at \t\t :"+Calendar.getInstance().getTime());
		while(true)
		{
			socket = serverSocket.accept();
			
			BufferedInputStream iStream = new BufferedInputStream(socket.getInputStream());
			InputStreamReader reader = new InputStreamReader(iStream);
			
			int c;
	        while ( (c = reader.read()) != 16)
	        {
	          clientMsg.append( (char) c);
	        }
	        
	        String msg = clientMsg.toString();
	        Roboto robot = new Roboto();
	        
	        BufferedOutputStream oStream = new BufferedOutputStream(socket.getOutputStream());
        	OutputStreamWriter writter = new OutputStreamWriter(oStream);
	        if(robot.initiate(msg))
	        {
	        	System.out.println("-- client command executed at \t\t :"+Calendar.getInstance().getTime());        	
	        	writter.write("Successful"+(char) 16);
	        	writter.flush();
	        }
	        else
	        {
	        	System.out.println("--- Opps! ---");
	        	writter.write("failed");
	        	writter.flush();
	        }
		}
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
}


	
	
}
