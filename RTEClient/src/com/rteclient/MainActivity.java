package com.rteclient;



import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.example.rteclient.R;


public class MainActivity extends Activity {
private EditText hostEditText;
private String hostName;

private EditText commandEditText;
private String commands;

//private AssetManager aManager;
private SSLSocket socket;
private KeyStore publicKeyStore;
private KeyStore privateKeyStore;
private TrustManagerFactory trustManagerFactory;
private KeyManagerFactory keyManagerFactory;
private SSLContext sslContext;
private SSLSocketFactory sslSocketFactory;
private Button connButton;
private Boolean keyAdded;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		keyAdded=false;
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void connectToServer(View view)
	{			
		connButton =  (Button) view;	
		connButton.setClickable(false);
		connButton.setText("IP set!");
		
	}
	
	public void sendCommands(View view)
	{
		if(!keyAdded)
		{
			commandEditText = (EditText) findViewById(R.id.editText2);
			commands = commandEditText.getText().toString();
		}
		
		commands += (char) 16;
		hostEditText = (EditText) findViewById(R.id.editText1);
		hostName = hostEditText.getText().toString();
		Toast.makeText(this, commands, Toast.LENGTH_LONG).show();
		new Thread(new ServerConnector()).start();		
		
	}
	
	public void enterKeyPress(View view)
	{
		getCommandText();
		keyAdded=true;
		commands += (char) 13;
		Toast.makeText(this, "Enter key press added to command string", Toast.LENGTH_LONG).show();
	}
	
	public void controlKeyPress(View view)
	{
		getCommandText();
		commands += (char) 14;
		Toast.makeText(this, "Ctrl key press added to command string", Toast.LENGTH_LONG).show();
	}
	
	public void alterKeyPress(View view)
	{
		getCommandText();
		commands += (char) 15;
		keyAdded=true;
		Toast.makeText(this, "Alt key press added to command string", Toast.LENGTH_LONG).show();
	}
	
	
	
	public void getCommandText()
	{
			commandEditText = (EditText) findViewById(R.id.editText2);
			commands = commandEditText.getText().toString();
	}
	
	class ServerConnector implements Runnable
	{
		int port = 19999;
		
		@Override
		public void run() {
			
			try
			{
								
				AssetManager manager = getAssets();				
				
				InetAddress serverAddr = InetAddress.getByName(hostName);
				SecureRandom secureRandom = new SecureRandom();
				secureRandom.nextInt();				
				
				
				publicKeyStore = KeyStore.getInstance("BKS");
				InputStream pubBKS = manager.open("server.public");
				publicKeyStore.load(pubBKS,"server".toCharArray());
				
				privateKeyStore = KeyStore.getInstance("BKS");
				InputStream priBKS = manager.open("client.private");
				privateKeyStore.load(priBKS, "client".toCharArray());
				
				trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
				trustManagerFactory.init(publicKeyStore);
										
				keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
				keyManagerFactory.init(privateKeyStore, "client".toCharArray() );
				
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init( keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom );
				
				sslSocketFactory = sslContext.getSocketFactory();
				socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr,port);
				
				BufferedOutputStream oStream = new BufferedOutputStream(socket.getOutputStream());
				OutputStreamWriter writter = new OutputStreamWriter(oStream,"US-ASCII");
				
				writter.write(commands);
				writter.flush();
				
				writter.close();
				socket.close();
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			
		}
		
	}

}
