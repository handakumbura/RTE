package com.RTEServer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Server {
private SSLServerSocket serverSocket;
private Socket socket;
private int port;
private StringBuffer clientMsg;

public void start()
{
	port = 19999;
	try
	{
		
		//ssl
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextInt();
		
		KeyStore publicKeyStore = KeyStore.getInstance("BKS");
		publicKeyStore.load( new FileInputStream("client.public"), "client".toCharArray() );
	
		KeyStore privateKeyStore = KeyStore.getInstance("BKS");
		privateKeyStore.load( new FileInputStream("server.private"),"server".toCharArray() );
			
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
		trustManagerFactory.init(publicKeyStore);
						
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
		keyManagerFactory.init(privateKeyStore, "server".toCharArray() );
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init( keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom );
		
		SSLServerSocketFactory sslServSoceketFactory = sslContext.getServerSocketFactory();
		
		//creating ssl server socket
		serverSocket = (SSLServerSocket) sslServSoceketFactory.createServerSocket(port);
		serverSocket.setNeedClientAuth(true);
		System.out.println(Calendar.getInstance().getTime()+"\t  : server started");
		
		
		
		while(true)
		{
			socket = serverSocket.accept();
			clientMsg = new StringBuffer();
			System.out.println(Calendar.getInstance().getTime()+"\t  : client connected");
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
        		System.out.println(Calendar.getInstance().getTime()+"\t  : client command executed");        	
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