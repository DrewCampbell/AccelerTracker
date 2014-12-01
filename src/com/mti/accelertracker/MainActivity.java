package com.mti.accelertracker;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

	
	private double xAcceleration;
	private double yAcceleration;
	private double zAcceleration;
	
	private boolean trackingOn=false;
	
	Sensor accelerometer;
	SensorManager sm;
	TextView acceleration;	
	TextView txtTracking;    
	
	private SharedPreferences sharedPreferences;
	
	//TextView xTouch;
	//TextView yTouch;
	
	private ArrayList<AccelerationEvent> events;

	
	MenuItem menuTrackStart;
	MenuItem menuTrackStop;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        acceleration = (TextView) findViewById(R.id.txtAcceleration);
        txtTracking = (TextView) findViewById(R.id.txtTrackingOnOff);        
        
		events = new ArrayList<AccelerationEvent>();
		
        //xTouch = (TextView) findViewById(R.id.txtXTouch);
        //yTouch = (TextView) findViewById(R.id.txtYTouch);     
       
        
    }

    
    @Override
    protected void onStop() {
    	super.onStop();	
    
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menuTrackStart = menu.findItem(R.id.action_start); 
		menuTrackStop = menu.findItem(R.id.action_stop); 		

		// Not sure why I have to do this since I already set it to invisible
		menuTrackStop.setVisible(false);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		

		switch (item.getItemId()) {

			case R.id.action_settings:
				final Dialog dialog_settings = new Dialog(this);
				dialog_settings.setContentView(R.layout.custom_dialog_settings);

				final Button btnSave = (Button) dialog_settings.findViewById(R.id.btnSave);
				btnSave.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog_settings.dismiss();
					}
					
				});

				final Button btnCancel = (Button) dialog_settings.findViewById(R.id.btnCancel);
				btnCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog_settings.dismiss();
					}
					
				});
				
				dialog_settings.show();
				
				
				break;		
	
		
			case R.id.action_start:

				//Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();

				trackingOn = true;
				trackAccelerationValues();

				menuTrackStart.setVisible(false);
				menuTrackStop.setVisible(true);				
		        txtTracking.setText("Tracking On");
				break;
			case R.id.action_stop:

				//Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();

				trackingOn = false;

				// Do we need to pass the arrayList as a shared preference or intent
				// then retrieve it in the GraphView?	
				// events is the array list
				
				sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences.edit();

				
				//  This seemed to now work
				Gson gson = new Gson();
				
				
				// I will now test these
				String json = gson.toJson(events);

				editor.putString("events", json);
				editor.commit();
				
				
				//  Here let's bring up new view
				setContentView(new GraphView(this));
			break;
		
	}
		
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
		
	}
		
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		acceleration.setText("X: " + event.values[0] + 
							"\nY: " + event.values[1] +
							"\nZ: " + event.values[2]);
		
		xAcceleration = event.values[0];
		yAcceleration = event.values[1];		
		zAcceleration = event.values[2];
				
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


    private void trackAccelerationValues() {

    	  //MyRunnable runnable = new MyRunnable();
    	  //runOnUiThread(runnable);	

    	  
    	  
  		// Need handler for callbacks to the UI thread
  		final Handler mHandler = new Handler();
  		trackingOn=true;
  		// Create runnable for posting
  		final Runnable mUpdateResults = new Runnable() {
  			public void run() {
  				//updateResultsInUi();

				events.add(new AccelerationEvent(xAcceleration,yAcceleration,zAcceleration));
  				
  			}
  		};
  		
  
  		
  		// Fire off a thread to do some work that we shouldn't do directly in the UI thread
  		Thread t = new Thread() {

  			
  		public void run() {
  			while(trackingOn==true) {
  				try{
  					sleep(100);
  					mHandler.post(mUpdateResults);
  				} catch(Exception e) {
  				}
  			}
  		}
  		};
  		t.start();     	  
    	  
    	  
    	  
    	  
    }

	private class MyRunnable implements Runnable {
		double lastTime;
		double thisTime;
	
		
		//  Not sure if this runs....
		  public void run() {
			  		
			  lastTime = System.currentTimeMillis();
			  while(trackingOn) {
				  
				  thisTime = System.currentTimeMillis();
				  if((thisTime-lastTime)>1000) {
					  lastTime = thisTime;
					  events.add(new AccelerationEvent(xAcceleration,yAcceleration,zAcceleration));
					
				  }
				  
				  trackingOn = false;				  
			  }			  
			  
		  }
		} 

}
