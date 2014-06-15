package com.example.rteclient;



import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
private EditText hostEditText;
private String hostName;
private Socket socket;
private EditText commandEditText;
private String commands;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		hostEditText = (EditText) findViewById(R.id.editText1);
		hostName = hostEditText.getText().toString();
		
		new Thread(new ServerConnector()).start();
		
		hostEditText.setEnabled(false);
		
		Toast.makeText(this, "connection to server created.", Toast.LENGTH_LONG).show();
		
	}
	
	public void sendCommands(View view)
	{
		commandEditText = (EditText) findViewById(R.id.editText2);
		commands = commandEditText.getText().toString()+ (char) 16;
		
		try
		{
			BufferedOutputStream oStream = new BufferedOutputStream(socket.getOutputStream());
			OutputStreamWriter writter = new OutputStreamWriter(oStream,"US-ASCII");
			
			writter.write(commands);
			writter.flush();
		}
		catch(Exception ex)
		{
			
		}
		
	}
	
	class ServerConnector implements Runnable
	{
		int port = 19999;
		
		@Override
		public void run() {
			
			try
			{
				InetAddress serverAddr = InetAddress.getByName(hostName);
				socket = new  Socket(serverAddr,port);

			}
			catch(Exception ex)
			{
				
			}
			
			
		}
		
	}

}
