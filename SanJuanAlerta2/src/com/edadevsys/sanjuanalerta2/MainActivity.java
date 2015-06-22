package com.edadevsys.sanjuanalerta2;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import com.edadevsys.sanjuanalerta2.database.DataBaseHandler;
import com.edadevsys.sanjuanalerta2.model.Contact;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	public static final String configSmsLocalFile = "conf1037";
	
	private static final int REQUEST_CODE = 0;	
	
	private BroadcastReceiver sendBroadCastReceiver = null ;	
	
	private static final String TAG = "MainActivity.java";	
	
	private static final String THE_NUMBER_EMERGENCY_POLICE = "2644845346";	
			
	private static final String THE_URL_LOCATION = "http://maps.google.com/?q=";
	
	private static final String THE_MESSAGE_PREDETER = "Emergencia";
	
	private static final String ALERTA_SAN_JUAN = "ALERTA SAN JUAN";
	
	private static final long MINIMUM_TIME = 35000; // 35 segundos
	
	private static final long MINIMUM_DISTANCE = 10; // 0 metros	
	
	private SharedPreferences saveLocal;
	
	private ProgressBar progressbar = null;
	
	private String theNumber = null;
	
	private String theMessage = null;
	
	private Boolean theFlagPoliceSendSMS = null;	
	
	private TextView textLatitud = null;
	
	private TextView textLongitud = null;
	
	private TextView textLocationStreet = null;
	
	private ImageView imgSendSMS = null;		
	
	private static DataBaseHandler db = null;	
	
	public PackageManager packageManager;
	
	public LocationManager locationManager;	
	
	public Location locationGral = null;
	
	public LocationListener locationListener = null;
	
	boolean isGPS_enabled = false;
	
	boolean isNETWORK_enabled = false;
	
	boolean configChangeRestore = false;
	
	private String provider;
	
	private Criteria criteria = null;
	
	private String theHomeUser = null;		
	
	private final static String SENT = "Mensaje Enviado....";
	
	private String currentSaveLatitud = "0.0";
	
	private String currentSaveLongitud = "0.0";
	
	private String currentSaveStreet;
	
	static final String STATE_LATITUD = "latitudValue";
	
	static final String STATE_LONGITUD = "longitudValue";
	
	static final String STATE_STREET = "streetValue";
	
	private static final int PICK_CONTACT_REQUEST = 1;

	private String name;
	
	private String number;
	
	private Contact contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.fragment_main);
		
		saveLocal = getSharedPreferences(configSmsLocalFile, 0);
		
		setSharedPreference();
		
		//***************************************************************************************
		
		
		db = DataBaseHandler.getDataBaseInstance(MainActivity.this);			
			
			initUI();
			
			init();
		
			addListenerOnButtonAlert();
		
			showAlertDialogCondition();
				
	}
	
	private void showAlertDialogCondition(){
		//*******************************
		if(provider!=null){				
			
			muestraLocaliz(locationGral);
			
			}else{
				
					final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
					
					
								alertDialog.setTitle(getString(R.string.text_change_config));
								
								
									alertDialog.setMessage(getString(R.string.text_enabled_question_location));						
								
										alertDialog.setCancelable(false);
								
										alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
										
				                        new DialogInterface.OnClickListener() {
									
				                            public void onClick(DialogInterface dialog,
				                            		
				                                    int which) {
				                            	
				                            				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				                            				
				                            						if(intent.resolveActivity(getPackageManager()) != null){
				                            							
				                            							startActivityForResult(intent, REQUEST_CODE);
				                            							
				                          						}                     	
				                            }
				                        });
								alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
										
				                        new DialogInterface.OnClickListener() {
									
				                            public void onClick(DialogInterface dialog,
				                            		
				                                    int which) {
				                            	
				                                alertDialog.cancel();
				                                
				                           
				                            }
				                        });
								alertDialog.setOnKeyListener(new OnKeyListener() {
									
									@Override
									public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {	
										
										if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE){
											//Do Nothing
										}
										
										return false;
									}
								});
								
								alertDialog.show();
		}
		
		//*******************************
		
		
	}
	
	private void muestraLocaliz(Location locationGral) {
		
			if (locationGral == null){
					//Location unknown           
	            	setupDataGeoLocalizeAndTriggerTask();
	
			}else{
					//Location known			
	    	 		updateScreen(locationGral);
			}
	}

	private void setupDataGeoLocalizeAndTriggerTask(){
		//Package global
			packageManager = getPackageManager();		
				
				isProviderEnabled();				
			
			new AsynLocation().execute();
		
	}

	

	private void initializeCriteriaLatitudLongitud(){
		
		criteria = new Criteria();
		
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			
			criteria.setSpeedRequired(true);
			
			criteria.setAltitudeRequired(false);
			
			criteria.setBearingRequired(false);
			
			criteria.setCostAllowed(false);		
			
	}
	private void init() {
		try {
			
			 
			// retrieve LocationManager from GetSystemServices
				locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
					initializeCriteriaLatitudLongitud();
			
						provider = locationManager.getBestProvider(criteria, false);
			 
			 
			
		} catch (Exception e) {
			Log.e(TAG, "Error in Method init class MainActivity");
		}
		
	}	
	

	private void initUI() {			
		
			  		imgSendSMS = (ImageView) findViewById(R.id.imageBtnSms);
			
					textLatitud = (TextView) findViewById(R.id.textLat);
			
					textLongitud = (TextView) findViewById(R.id.textLong);
			
					textLocationStreet = (TextView) findViewById(R.id.textLocationAndStreet);
			
					progressbar = (ProgressBar) findViewById(R.id.progressLocation);
			
					textLatitud.setText(currentSaveLatitud);
			
					textLongitud.setText(currentSaveLongitud);
			
					textLocationStreet.setText(currentSaveStreet);			
					
			
	}



	//******************************************************************************************************
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		//****************************************
		//*******************************
		if(locationListener != null){
			
			locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, false), MINIMUM_TIME, MINIMUM_DISTANCE, locationListener);
			
		}
		sendBroadCastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String result ="";
				intent.setPackage(getPackageName());
				
				switch (getResultCode()) {
				
					case Activity.RESULT_OK:
						
						result = getString(R.string.text_sms_send);
						
						
						break;
						
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						
						result = getString(R.string.text_sms_failure);
						
						
						break;
						
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						
						result = getString(R.string.text_sms_no_service);
						
						
						break;	
						
					case SmsManager.RESULT_ERROR_NULL_PDU:
						
						result = getString(R.string.text_null_pdu);
						
		                
		                break;
		                
		            case SmsManager.RESULT_ERROR_RADIO_OFF:
		            	
		            	result = getString(R.string.text_radio_off);
		            	
		                
		                break;
				}
		                Toast.makeText(getBaseContext(), result,Toast.LENGTH_SHORT).show();
			}
		};
		//********************************************************************
		
		//******************************
		//***************************************
		
		registerReceiver(sendBroadCastReceiver, new IntentFilter(SENT));
		
		
		
		//***************************************************************************************************
		
		new Handler().postDelayed(new Runnable() { 
			   public void run() { 
			     openOptionsMenu(); 
			   } 
			}, 1000); 
		
		
		
	}
	@Override
	protected void onPause() {
		
		super.onPause();
		
		if(locationListener != null){
			
			locationManager.removeUpdates(locationListener);
			
		}
	}
		
	@Override
	protected void onStop() {
		
		super.onStop();
		
		try {
			
			if (sendBroadCastReceiver != null) {
				
		  	      // Unregister receiver.
		    		unregisterReceiver(sendBroadCastReceiver);
		    		
			  	      // The important bit here is to set the receiver
			  	      // to null once it has been unregistered.
			  	      sendBroadCastReceiver = null;
		  	      
		  	   }
			
		} catch (Exception e) {
			
				Log.e(TAG,"Error in OnStop method in class MainActivity");
				
		}finally{
			
			if(locationListener !=null){
				
				locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, false), MINIMUM_TIME, MINIMUM_DISTANCE, locationListener);
				
			}
				
		}
		
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onRestoreInstanceState(savedInstanceState);
		
		
			currentSaveLatitud = savedInstanceState.getString(STATE_LATITUD);
		
			currentSaveLongitud = savedInstanceState.getString(STATE_LONGITUD);
			
			currentSaveStreet = savedInstanceState.getString(STATE_STREET);
		
			textLatitud.setText(currentSaveLatitud);
		
			textLongitud.setText(currentSaveLongitud);
		
			textLocationStreet.setText(currentSaveStreet);
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
				
			outState.putString(STATE_LATITUD, currentSaveLatitud);
			
			outState.putString(STATE_LONGITUD, currentSaveLongitud);
					
			outState.putString(STATE_STREET, currentSaveStreet);
			
			super.onSaveInstanceState(outState);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		
		try {
			
			if (sendBroadCastReceiver != null) { 		
	    	
	    	      // Unregister receiver.
	    		unregisterReceiver(sendBroadCastReceiver);
	    		
	    	      // The important bit here is to set the receiver
	    	      // to null once it has been unregistered.
	    	      sendBroadCastReceiver = null; 
	    	      
	    	 }else{
	    		 
	    		 Log.w(TAG,"Do not unregister receiver as it was never registered");
	    		 
	    	 }
	    	
			if(configChangeRestore){
				
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				
				startActivity(intent);
			}
			
			
			
		} catch (Exception e) {
			
			Log.e(TAG,"Error in OnDestroy method");
			
		}finally{
			
			if(locationListener !=null){
				
				locationManager.removeUpdates(locationListener);
				
			}
						
			if(db != null){
				
				db.close();
				
			}
		}
			
	}
	//******************************************************************************************************
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		
			getMenuInflater().inflate(R.menu.main, menu);
			setMenuBackground();
		return true;
	}

	private void setMenuBackground() {
		// TODO Auto-generated method stub
		    
	    getLayoutInflater().setFactory( new Factory() { 

	        @Override
	        public View onCreateView(String name, Context context,
	                AttributeSet attrs) {
	            if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	                try { // Ask our inflater to create the view  
	                    LayoutInflater f = getLayoutInflater();  
	                    final View view = f.createView( name, null, attrs );  
	                    /* The background gets refreshed each time a new item is added the options menu.  
	                    * So each time Android applies the default background we need to set our own  
	                    * background. This is done using a thread giving the background change as runnable 
	                    * object */
	                    new Handler().post( new Runnable() {  
	                        public void run () {  
	                            // sets the background color   
	                            view.setBackgroundResource( R.color.menubg);
	                            // sets the text color              
	                            ((TextView) view).setTextColor(Color.WHITE);
	                            // sets the text size              
	                            ((TextView) view).setTextSize(18);
	            }
	                    } );  
	                return view;
	            }
	        catch ( InflateException e ) {}
	        catch ( ClassNotFoundException e ) {}  
	    } 
	            return null;
	        }});
	}
	



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
				int id = item.getItemId();
				
						switch (id) {		
						
							case R.id.menu_contact:		
								
								
								/*Intent pickContactIntent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);			
								pickContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);		
									if(pickContactIntent.resolveActivity(getPackageManager()) != null){
										startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
									}*/
								
								
								Intent intentContact = new Intent(MainActivity.this, ContactsMainActivity.class);
								
								startActivity(intentContact);
								
								
							return true;	
							
							/*case R.id.menu_view_contacts:
								
								int countContact = DataBaseHandler.getDataBaseInstance(MainActivity.this).getContactsCount();
								
								if(countContact>0){
									
									Intent intentViewContact = new Intent(MainActivity.this,ContactList.class);
									
									startActivity(intentViewContact);
									
									
								}else{
								
									Toast.makeText(MainActivity.this, getString(R.string.text_no_load_contacts), Toast.LENGTH_LONG).show();
								
								}
								
								
								return true;*/
							
							
							case R.id.menu_config:			
								
								Intent intentConfigApp = new Intent(MainActivity.this, ConfigAppActivity.class);
								
								startActivity(intentConfigApp);
								
							return true;	
							
							
							case R.id.menu_end:			
								
								finish();			
								
								return true;
								
							default:
								
								return super.onOptionsItemSelected(item);
								
						}
		
		
	}

	
	
	 //********************************************************************************************************
    public void sendMessage(String theNumber, String myMsg) {	
    	
    	try {    		
    		
    		//Pending message get broadcast
    		PendingIntent sentPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT);
    		
			SmsManager sms = SmsManager.getDefault();
			
			sms.sendTextMessage(theNumber, null, myMsg, sentPI, null);
			
		} catch (Exception e) {
			
			Log.w(TAG, "Error in sendMessage method");
			
		}
    			
		
	}
  //********************************************************************************************************
    private void addListenerOnButtonAlert() {
		
		imgSendSMS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// the local variable obtain from sharedPreference
				
				 theNumber = saveLocal.getString(getString(R.string.configNumberString), "");
				 
				 theMessage = saveLocal.getString(getString(R.string.configMessageString), "");
				 
				 theFlagPoliceSendSMS = saveLocal.getBoolean(getString(R.string.configFlagNumberString), false);
				 
				 theHomeUser = saveLocal.getString(getString(R.string.configHomeUser), "");
				 
				 
				 
				 
				 //number of message send and the message text default
				 
							 if(!( theNumber.isEmpty() && theMessage.isEmpty() ) ){
								 
								 	// Flag indicating whether or not send a text message
								 
								 if(theFlagPoliceSendSMS || db.getContactsCount()>0){
									 
									 // begin asyn task
									 
										new AsyncTaskSplash().execute();
										
								 }else{
									 
									 Toast.makeText(MainActivity.this,
									          getString(R.string.text_alert_send_msg),
									          Toast.LENGTH_LONG).show();
								 }	
								}else{
									
									 Toast.makeText(MainActivity.this,
									          getString(R.string.text_input_number_phone),
									          Toast.LENGTH_LONG).show();
									
								}
				
			}
		});
		
	}
	//****************************************************************************************************************************************************
    public class AsyncTaskSplash extends AsyncTask<Void, Void, Boolean>{
    	
    	int myprogress;
    	
    	private final ProgressDialog dialog = new ProgressDialog(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
    	
    	//-----------------------------------------------------------------------------------------------------------------------------------
		@SuppressWarnings("finally")
		protected Boolean doInBackground(Void...arg0) {
			
			Double latitud;
			
			Double longitud;	

			boolean retorno = false;
			
			boolean valueStreetFromGps = false; 
			
			StringBuilder strMessageBuilder = new StringBuilder();
			
			String street;
			
				try {
				
					
									latitud = Double.parseDouble(textLatitud.getText().toString().replace(",", "."));
								
									longitud = Double.parseDouble(textLongitud.getText().toString().replace(",", "."));
															
									street = textLocationStreet.getText().toString();
									
									
									//Armo message
 										
											
											strMessageBuilder.append(ALERTA_SAN_JUAN).append("\n").append(theMessage).append(" ");
											
											//check value street obtain from Geocoder
												if(! street.isEmpty() ){			
											
														strMessageBuilder.append(street).append("\n");
											
														valueStreetFromGps = true;
														
												}else{
											
													strMessageBuilder.append("\n");
												}						
										
							
												if( (latitud.compareTo(Double.parseDouble("0.0")) == 0) && (longitud.compareTo(Double.parseDouble("0.0")) == 0)){
																			// do nothing
														
												}else{
													
													//check values from variable latitud and longitud
													
														if ( (latitud < 0) && (longitud < 0) || (latitud > 0) && (longitud < 0) || 
																(latitud < 0) && (longitud > 0) || (latitud > 0) && (longitud > 0)){					
														
																strMessageBuilder.append(THE_URL_LOCATION).append(latitud).append(",").append(longitud).append("\n");
																	
														}
												}				
									
									//check value from data personal optional from configuration app
									if( !theHomeUser.isEmpty() && !valueStreetFromGps){
									
										strMessageBuilder.append(theHomeUser);
										
									}
									
									//Finish assembling the message
									
									//Begin Send Message
									//Send message to 911 (Police)
									if(theFlagPoliceSendSMS){
										
									    sendMessage(theNumber,strMessageBuilder.toString());
									
									}
												
												if(db.getContactsCount()>0){
													
													//Reading all contacts		
													List<Contact>contacts =db.getAllContacts();				
													
													for (int i = 0; i < contacts.size(); i++) {
														
														sendMessage(contacts.get(i).getPhoneNumber(), strMessageBuilder.toString());
														
													}
													
													
											
												}
										
									retorno = true;				
				
									
				} catch (Exception e) {
					
					retorno = false;
					Log.e(TAG, "Error in method doInBackGroud AsyncTaskSplash class MainActivity");
				}finally{
					
					if(db !=null)
						db.close();
					
					return retorno;
				}
		}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------	
		protected void onPreExecute() 
		{
						
			dialog.setTitle(getString(R.string.text_send_alert_wait));
			dialog.setMessage(getString(R.string.text_send_alert_sms));
			dialog.setIndeterminate(true);			
			dialog.show();
			
		}
		 @Override
		  protected void onPostExecute(final Boolean result) {
		   
			 if (this.dialog.isShowing()) {
			        this.dialog.dismiss();
			     }
				
		  }
		
    	
    }
   //*******************************************************************************************************
	
	private void setSharedPreference() {
		
			SharedPreferences.Editor editor = saveLocal.edit();
		
			   String theNumber = saveLocal.getString(getString(R.string.configNumberString), "");				
			   String theMessage = saveLocal.getString(getString(R.string.configMessageString), "");
			   String theHome = saveLocal.getString(getString(R.string.configHomeUser), "");			   
			   Boolean theFlagPoliceSendSMSTheNumber = saveLocal.getBoolean(getString(R.string.configFlagNumberString), false);
			   
						
					if(theNumber.isEmpty()&&theMessage.isEmpty()){
						
						editor.putString(getString(R.string.configMessageString), THE_MESSAGE_PREDETER);
						editor.putString(getString(R.string.configNumberString), THE_NUMBER_EMERGENCY_POLICE);
					}else{
						
						editor.putString(getString(R.string.configMessageString), theMessage);
						editor.putString(getString(R.string.configNumberString), theNumber);
					}
					
					
		 				if(theFlagPoliceSendSMSTheNumber){
		 						editor.putBoolean(getString(R.string.configFlagNumberString), true);
		   				}else{
		   						editor.putBoolean(getString(R.string.configFlagNumberString), false);
		   				}
		 				
		 			if(theHome.isEmpty()){		 				
		 				editor.putString(getString(R.string.configHomeUser), theHome);
		 			}	
					editor.commit();
		
	}
	private void updateScreen(Location location) {
		
		
			if(location != null){	
				
				if( !(String.valueOf(location.getLatitude()).equals("0.0") && String.valueOf(location.getLatitude()).equals("0.0"))){
				
					String latitud  = new DecimalFormat("###.######").format(location.getLatitude());
					
					String longitud = new DecimalFormat("###.######").format(location.getLongitude());
				
					textLatitud.setText(latitud);
					
					textLongitud.setText(longitud);
				}
			}
		
	}
	private void configGPS(){
		
	   locationListener = new MyLocationListener();
		
	   locationGral = new Location(LocationManager.GPS_PROVIDER);
	
	   MainActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME, MINIMUM_DISTANCE, locationListener);
			}
		});
				
	}

	private void configNET(){
		
		
		
		locationGral = new Location(LocationManager.NETWORK_PROVIDER);
		
		locationListener = new MyLocationListener();
		
		MainActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME, MINIMUM_DISTANCE, locationListener);
			}
		});
	
	
		
	}
//**********************************************************************class Async**************************************************	
private class AsynLocation extends AsyncTask<Void, Integer, Void>{
	
	
	@SuppressWarnings("unused")
	private int myprogress1;
	
	@Override
	protected Void doInBackground(Void... params) {
		
		try {		
				
					
					if(isGPS_enabled){
					
						configGPS();
					
					}else{
						if(isNETWORK_enabled){
							
							configNET();
							
						}
					}
					
				
				if(Geocoder.isPresent() && locationGral != null){
					getGeoCoderFromLatitudAndLongitud(locationGral);
				}		
						
			
		} catch (Exception e) {
			Log.e(TAG, "Error in method doInBackground AsynLocation class MainACtivity");
		}
		
		return null;
	}
	@Override
	protected void onPreExecute() {

		myprogress1 = 0;
		
		progressbar.setVisibility(View.VISIBLE);
		
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		
		progressbar.setProgress(values[0]);
		
	}
	@Override
	protected void onPostExecute(Void result) {
		
		progressbar.setVisibility(View.INVISIBLE);	
		
		updateScreen(locationGral);
		
	}
	
}
//*****************************************************************end class asyn*****************************************************************
//*****************************************************************class MyLocationListener*******************************************************
private class MyLocationListener implements LocationListener{
	
	@Override
	public void onLocationChanged(Location location) {
			
		updateScreen(location);
		
		if(Geocoder.isPresent() && location != null){
			
			getGeoCoderFromLatitudAndLongitud(location);
			
			currentSaveLatitud = String.valueOf(location.getLatitude());
			
			currentSaveLongitud = String.valueOf(location.getLongitude());
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
		
	}
	
}// end class
//**********************************************************************************************************************************

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	
	super.onActivityResult(requestCode, resultCode, data);
	Cursor phone = null;
	
	Cursor cursor = null;
	
	
				switch (requestCode) {
						case 0:
							isProviderEnabled();
											
								if( isGPS_enabled || isNETWORK_enabled ){
									configChangeRestore = true;
								}
									
									initializeCriteriaLatitudLongitud();
								
							   provider = locationManager.getBestProvider(criteria, false);
								
							   setupDataGeoLocalizeAndTriggerTask();		
						
						break;
						case 1:	
								Log.w(TAG, "Do Nothing");
						break;
					
						default:
							break;
				}
	
	
				 // Check which request it is that we're responding to
				try {
					
				
					if (requestCode == PICK_CONTACT_REQUEST) {
			    	
						// Make sure the request was successful
							if (resultCode == RESULT_OK) {
			        	
									// Get the URI that points to the selected contact
										Uri contactUri = data.getData();
											// We only need the NUMBER column, because there will be only one row in the result
											// String[] projection = {Phone.NUMBER};
			            
								            // Perform the query on the contact to get the NUMBER column
								            // We don't need a selection or sort order (there's only one result for the given URI)
								            // CAUTION: The query() method should be called from a separate thread to avoid blocking
								            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
								            // Consider using CursorLoader to perform the query.
										
											String[] projection = new String[] {
													ContactsContract.Contacts.DISPLAY_NAME,
													ContactsContract.Contacts.HAS_PHONE_NUMBER,
													ContactsContract.Contacts._ID,					
											};
											
											cursor = getContentResolver()
														.query(contactUri, projection, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?", 
																new String[] { "1" }, null);
			            
			            if(cursor!=null&&cursor.moveToFirst()){
			            	
			            	
						            // Retrieve the phone number from the NUMBER column
			            	
						            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						            
						            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
						            
						            String[] projection1 = {Phone.NUMBER};
						            
								            // Perform the query on the contact to get the NUMBER column
								            // We don't need a selection or sort order (there's only one result for the given URI)
								            // CAUTION: The query() method should be called from a separate thread to avoid blocking
								            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
								            // Consider using CursorLoader to perform the query.
						            			phone = getContentResolver()
						            						.query(Phone.CONTENT_URI, projection1, Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
						            					phone.moveToFirst();
					
						            					// Retrieve the phone number from the NUMBER column
						            							int column = phone.getColumnIndex(Phone.NUMBER);
						            							number = phone.getString(column);
						            
						        
																contact = new Contact();
																contact.setName(name);
																contact.setPhoneNumber(number);
									
																	boolean isNumberExist = DataBaseHandler.getDataBaseInstance(MainActivity.this).isNumberExist(number);
																		int count = DataBaseHandler.getDataBaseInstance(MainActivity.this).getContactsCount();
									
										
																		if(count==10){
																			Toast.makeText(MainActivity.this, getString(R.string.text_list_fill), Toast.LENGTH_LONG).show();
																		}else{
																				if(!(isNumberExist)){									
												
																						DataBaseHandler.getDataBaseInstance(MainActivity.this).addContact(contact);
																						Toast.makeText(MainActivity.this, getString(R.string.text_saved), Toast.LENGTH_SHORT).show();
												
																				}else{
																						Toast.makeText(MainActivity.this, getString(R.string.text_saved_allready), Toast.LENGTH_SHORT).show();
											}
									}
			            }else{
			                Toast.makeText(MainActivity.this, getString(R.string.text_no_phone_contact), Toast.LENGTH_SHORT).show();
			            }
			            
			        }
			    }
				} catch (Exception e) {
					Log.e(TAG, "Error in ActivityResult");
				}finally{
					
					if(phone!=null)
						phone.close();
					if(cursor!=null)
						cursor.close();
				}		
				
				
	
	
}
private void isProviderEnabled(){
	
	isGPS_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);	
	isNETWORK_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	
}
//**************************************************************************************************************
private void getGeoCoderFromLatitudAndLongitud(Location location){
	 
	List<Address> addresses = null;
	
	 String street = null;
	 
	 Geocoder geocoder = null;
		 
	try {
		
		
				
				geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
				
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				
					
				street = addresses.get(0).getAddressLine(0);				
				
				StringBuilder setTextLocationAndStreet = new StringBuilder();
				
				setTextLocationAndStreet.append(street);
				
				textLocationStreet.setText(street);
			
	}catch(NullPointerException e){
		Log.e(TAG, "Locale is null");
		
	}catch (IOException e) {
		Log.e(TAG, "Service Not Available");
		
	} catch (Exception e) {
		Log.e(TAG, "Error in method getGeoCoderFromLatitudAndLongitud class MainActivity");
	}
}

}//End Class MainActivity