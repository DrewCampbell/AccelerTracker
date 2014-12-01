package com.mti.accelertracker;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.SurfaceView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GraphView extends SurfaceView {

	Paint paint;
	
	private SharedPreferences sharedPreferences;
	
	public GraphView(Context context) {
		super(context);
		
		paint = new Paint();
		
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);

		
	
	
	}


	
	
	@Override
	protected void onDraw(Canvas canvas) {

		double thisX, thisY, thisZ;
		Float lastX=null;
		Float lastY=null;
		Float lastZ=null;
		
		canvas.drawColor(Color.GRAY);



		
		//This worked  Now can I create the graph from the arrayaLiest? 
		
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		
		Gson gson = new Gson();
		String json = sharedPreferences.getString("events", null);
		Type type = new TypeToken<ArrayList<AccelerationEvent>>() {}.getType();
		ArrayList<AccelerationEvent> events = gson.fromJson(json, type);
		
		int numEvents = events.size();
		int eventNumber;

		int xPartition = getWidth()/numEvents;		
		int yPartition = getHeight()/4;
		float gravityLine = (float) yPartition * .98f;
		
		
		
		// set the color labels
		paint.setTextSize(30);
		paint.setColor(Color.RED);
		canvas.drawText("X ", 50, 100, paint);
		paint.setColor(Color.BLUE);
		canvas.drawText("Y", 50, 150, paint);
		paint.setColor(Color.YELLOW);
		canvas.drawText("Z", 50, 200, paint);
		
		// set the 0 acceleration line
		paint.setColor(Color.GREEN);		
		canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2,paint);				 

		//  gravity is value of one g
		paint.setColor(Color.GREEN);		
		canvas.drawLine(0, getHeight()/2 - gravityLine, getWidth(), getHeight()/2 - gravityLine,paint);				 
		paint.setTextSize(20);
		canvas.drawText("1 g", 10, getHeight()/2 - gravityLine-10, paint);
		
		paint.setColor(Color.GREEN);		
		canvas.drawLine(0, getHeight()/2 + gravityLine, getWidth(), getHeight()/2 + gravityLine,paint);				 
		paint.setTextSize(20);
		canvas.drawText("-1 g", 10, getHeight()/2 + gravityLine-10, paint);
		
		

		
		eventNumber = 0;
	
		 for (AccelerationEvent event : events) {
		     thisX = event.getX();
		     thisY = event.getY();
		     thisZ = event.getZ();

		     Float xFloat = (float) thisX;
			 Float yFloat = (float) thisY;
			 Float zFloat = (float) thisZ;


			 // This will be an option in the settings
			 /*
			 paint.setColor(Color.RED);
			 canvas.drawCircle(xPartition + eventNumber * xPartition, getHeight()/2 - gravityLine/10 * xFloat,5,paint);			     
			 paint.setColor(Color.BLUE);
			 canvas.drawCircle(xPartition + eventNumber * xPartition, getHeight()/2 - gravityLine/10 * yFloat,5,paint);
			 paint.setColor(Color.YELLOW);
			 canvas.drawCircle(xPartition + eventNumber * xPartition, getHeight()/2 - gravityLine/10 * zFloat,5,paint);
			 */
			 
			 if(lastX!=null){
				 paint.setColor(Color.RED);
				 canvas.drawLine(xPartition + (eventNumber-1) * xPartition, getHeight()/2 - yPartition/10 * lastX,xPartition +  eventNumber * xPartition, getHeight()/2 - yPartition/10 * xFloat,paint);
			 }
			 if(lastY!=null){
				 paint.setColor(Color.BLUE);
				 canvas.drawLine(xPartition + (eventNumber-1) * xPartition, getHeight()/2 - gravityLine/10 * lastY, xPartition + eventNumber * xPartition, getHeight()/2 - gravityLine/10 * yFloat,paint);
			 }
			 if(lastZ!=null){
				 paint.setColor(Color.YELLOW);
				 canvas.drawLine(xPartition + (eventNumber-1) * xPartition, getHeight()/2 - gravityLine/10 * lastZ, xPartition + eventNumber * xPartition, getHeight()/2 - gravityLine/10 * zFloat,paint);
			 }			 
			 

			 
			 eventNumber +=1;
			 
			 lastX=xFloat;
			 lastY=yFloat;
			 lastZ=zFloat;
			 
		 }		
	
	}
}
