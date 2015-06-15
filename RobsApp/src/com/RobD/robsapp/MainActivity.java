package com.RobD.robsapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.RobD.Moba.Utils.Zoom;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	public ImageView backImageView, foreImageView;
	private int backgroundWidth;
	private int backgroundHeight;
	private RelativeLayout backRelativeLayout;//, foreRelativeLayout;//, GUIRelativeLayout;
	private ScrollView LayoutVScroll;//, LayoutforeVScroll;
	private HorizontalScrollView LayoutHScroll;//, LayoutforeHScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// set the MainGamePanel as the View
		//setContentView(new FishGamePanel(this));
		
		setContentView(R.layout.activity_main);
		Log.d(TAG, "View added");
		
		backImageView = (ImageView) findViewById(R.id.backImageView);
		//foreImageView = (ImageView) findViewById(R.id.foreImageView);
		backRelativeLayout = (RelativeLayout) findViewById(R.id.backRelativeLayout);
		//foreRelativeLayout = (RelativeLayout) findViewById(R.id.foreRelativeLayout);
		//GUIRelativeLayout = (RelativeLayout) findViewById(R.id.GUIRelativeLayout);
		LayoutVScroll = (ScrollView) findViewById(R.id.LayoutVScroll);
		LayoutHScroll = (HorizontalScrollView) findViewById(R.id.LayoutHScroll);
		//LayoutforeVScroll = (ScrollView) findViewById(R.id.LayoutforeVScroll);
		//LayoutforeHScroll = (HorizontalScrollView) findViewById(R.id.LayoutforeHScroll);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		backgroundWidth = (int)Math.round(metrics.widthPixels * 0.9);
		backgroundHeight = metrics.heightPixels;
		
		//RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)Math.round(metrics.widthPixels * 0.1), backgroundHeight);
		//GUIRelativeLayout.setLayoutParams(params);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	}
	
	public void setBackground(final Activity activity, final float scaleAmount, final float focusX, final float focusY){		
		// Confusing math to set the background in the right position at different zoom levels
		
		activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {            	
            	int zoomInt = (int)scaleAmount;
            	float playerXP = focusX / backgroundWidth;
            	float playerYP = focusY / backgroundHeight;
            	
            	if(zoomInt > (int)Zoom.ZOOM_FAR){
                	ScrollView.LayoutParams backParams  = new ScrollView.LayoutParams(backgroundWidth * zoomInt + backgroundWidth * zoomInt / 3, backgroundHeight * zoomInt + backgroundHeight); 
                	backRelativeLayout.setLayoutParams(backParams);
                	//foreRelativeLayout.setLayoutParams(backParams);
                	switch(zoomInt){
                	case (int)Zoom.ZOOM_MID:
                		LayoutHScroll.scrollTo((int)Math.round(LayoutHScroll.getRight() *zoomInt * playerXP - backgroundWidth / 9), 0);
                		break;
                	case (int)Zoom.ZOOM_NEAR:
                		LayoutHScroll.scrollTo((int)Math.round(LayoutHScroll.getRight() *zoomInt * playerXP - (int)(backgroundWidth / 1.2)), 0);
                		break;
                	}
                	
            		//LayoutHScroll.scrollTo((int)Math.round(LayoutHScroll.getRight() *zoomInt * playerXP - backgroundWidth / 9), 0);
            		LayoutVScroll.scrollTo(0, (int)Math.round(LayoutVScroll.getBottom() * zoomInt * playerYP));
            		//LayoutforeHScroll.scrollTo((int)Math.round(LayoutHScroll.getRight() *zoomInt * playerXP), 0);
            		//LayoutforeVScroll.scrollTo(0, (int)Math.round(LayoutVScroll.getBottom() * zoomInt * playerYP));
            	}else{
                	ScrollView.LayoutParams backParams  = new ScrollView.LayoutParams(backgroundWidth * zoomInt + backgroundWidth / 9, backgroundHeight * zoomInt); 
                	backRelativeLayout.setLayoutParams(backParams);
                	//foreRelativeLayout.setLayoutParams(backParams);
            	}
            	
            	RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(backgroundWidth * zoomInt, backgroundHeight * zoomInt);
            	switch(zoomInt){
            	case (int)Zoom.ZOOM_MID:
            		imageParams = new RelativeLayout.LayoutParams(backgroundWidth * zoomInt + (int)(backgroundWidth / 2.3), backgroundHeight * zoomInt);
            		break;
            	case (int)Zoom.ZOOM_NEAR:
            		imageParams = new RelativeLayout.LayoutParams(backgroundWidth * zoomInt + (int)(backgroundWidth * 1.1), backgroundHeight * zoomInt);
            		//imageParams = new RelativeLayout.LayoutParams(backgroundWidth * zoomInt + (int)(backgroundWidth / 2.3), backgroundHeight * zoomInt);
            		break;
            	}
            	
        		imageParams.setMargins(backgroundWidth / 9 , 0, 0, 0);
        		
        		if(zoomInt > (int)Zoom.ZOOM_FAR){
        			imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        			imageParams.setMargins(backgroundWidth / 9 + 2 *backgroundWidth , 0, 0, 0);
        		}
            	
            	backImageView.setLayoutParams(imageParams);
            	
            	//RelativeLayout.LayoutParams foreParams = new RelativeLayout.LayoutParams(backgroundWidth * zoomInt, backgroundHeight * zoomInt);
            	//foreImageView.setLayoutParams(imageParams);
            }
		});
	}
}