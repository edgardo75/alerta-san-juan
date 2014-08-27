package com.edadevsys.sanjuanalerta2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import com.edadevsys.sanjuanalerta2.adapter.MultiSelectionAdapter;
import com.edadevsys.sanjuanalerta2.model.Contact;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivityListContact extends Activity implements OnClickListener {
	
	private static final String TAG = "MainActivityListContact.java";
	
	ListView mListView = null;
	
	Button btnSaveChequedItems = null;
	
	ArrayList<Contact> mContactsArrayList = null;
	
	HashMap<String, String> data = null;
	
	HashSet<String>numbers = null;
	
	MultiSelectionAdapter mAdapter = null;
	
	Cursor mCursor = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		bindComponents();
		init();
		addListener();
	}

	
	private void addListener() {
		// TODO Auto-generated method stub
		btnSaveChequedItems.setOnClickListener(MainActivityListContact.this);
	}


	private void init() {
		// TODO Auto-generated method stub
		
		
			mContactsArrayList = new ArrayList<Contact>();
		
				data = new HashMap<String, String>();
		
				numbers = new HashSet<String>();
		
				getAllContacts();	
		
				mAdapter = new MultiSelectionAdapter(MainActivityListContact.this, mContactsArrayList);
		
				mListView.setAdapter(mAdapter);
		
				mAdapter.notifyDataSetChanged();
		
	}

	private void bindComponents() {
		
		
		mListView = (ListView) findViewById(android.R.id.list);
		
		btnSaveChequedItems = (Button) findViewById(R.id.btnSaveChequedItems);
		
	}


	@Override
	public void onClick(View v) {
		int contactSelected = mAdapter.getChequedItems().size();
		
		if(contactSelected>0){
			
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityListContact.this);
						builder.setMessage(R.string.dialog_message_aviso_message_add_contacts);
						builder.setTitle(R.string.dialog_message_aviso_message);
						builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
								
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											
											new AsyncSendContactPhoneLocalToDataBaseApp().execute();
											
										
											
										}
										
										
									});	
									builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
											
										}
									});	
										
								AlertDialog dialog = builder.create();		
									dialog.show();	
						
		}else{
			Toast.makeText(MainActivityListContact.this, getString(R.string.text_select_contact_at_the_least), Toast.LENGTH_SHORT).show();
		}
		
		
		
		
		
	}
	
	private void getAllContacts() {
		
		
		
		try {
				
					String[]projection = new String[]{
						
							ContactsContract.Contacts._ID,
					};
			
					mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
					
					final int contactIdColumnIndex = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
				

					
					if(mCursor.moveToFirst()) {
						
						while( !mCursor.isAfterLast() ) {		// still a valid entry left?
							
							final int contactId = mCursor.getInt(contactIdColumnIndex);
						
								
								workingWithContactId(contactId);
						
							
							mCursor.moveToNext();			// move to the next entry
						}
					}
			
			
			
		} catch (Exception e) {
		
			Log.e(TAG,"Error en MainActivityListContact MainActivityListContact");
		}finally{
			
			if(mCursor != null)
				mCursor.close();
		}
		
	}


	private void workingWithContactId(int contactId) {
		Cursor mContacts = null;
		Cursor mPhone = null;
		Contact contact = null;
		
		try {
			final String[] projection = new String[] {
					Contacts.DISPLAY_NAME,	// the name of the contact				
					Contacts.PHOTO_ID,
				};
			 mContacts = getContentResolver().query(Contacts.CONTENT_URI, projection, 
					Contacts._ID + "=?", new String[]{String.valueOf(contactId)}, Contacts.DISPLAY_NAME);
			
			while (mContacts.moveToNext()) {
					
			
							if(mContacts.moveToFirst()) {
								final String name = mContacts.getString(
										mContacts.getColumnIndex(Contacts.DISPLAY_NAME));
									
									
									
									
									String[] projectionPhone = new String[]{
											Phone.NUMBER,
											Phone.TYPE,
											
									};
									mPhone = getContentResolver().query(Phone.CONTENT_URI,	
											projectionPhone, 
											Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
									
									
									if(mPhone.moveToFirst()){
										final int contactNumberColumnIndex = mPhone.getColumnIndex(Phone.NUMBER);
										final int contactTypeColumnIndex = mPhone.getColumnIndex(Phone.TYPE);
										
										String number=null;
											while (!mPhone.isAfterLast()) {
												
												
												final int type = mPhone.getInt(contactTypeColumnIndex);
												
												
												switch (type) {
												case Phone.TYPE_HOME:{
												    // home number
												
													
												    break;}
												   case Phone.TYPE_MOBILE:{
												
												    // mobile number
													   number = mPhone.getString(contactNumberColumnIndex);
												    break;}
												   case Phone.TYPE_WORK:{
												    // work(office) number
												
													
												    break;}
												}//end switch
													if(number != null){
														data.put(name, number);
													
													
													}
													mPhone.moveToNext();
													
													
												
											}//end while
											
											
											mPhone.close();
									}
									
							}				
				
			}//end while
			
			
			
			
			String mName;
			String mNumber;
			for (Entry<String, String> entry : data.entrySet()) {
			    String name = entry.getKey();
			    String number = entry.getValue().toString();
			     mName = null;
			     mNumber = null;
			    
			    if(numbers.add(name)){
			    	
			    	mName = name;
					mNumber = number;
					
			    }
			    
			    if(mName != null && mNumber != null){
			    	
			    	contact = new Contact();
			    	
			    		contact.setName(mName);
			    		contact.setPhoneNumber(mNumber);
			    	
			    	mContactsArrayList.add(contact);	
			    	
			    }
			}	
			
		} catch (Exception e) {
			Log.e(TAG, "Error in mETHOD WORKINGWITHCONTACT ID");
		}finally{
			
			if(mPhone!=null)
				mPhone.close();
			if(mContacts!=null)
				mContacts.close();
		}
		
		
		
	}
	
	//********************************************************************************************
	
@SuppressLint("InlinedApi")
public class AsyncSendContactPhoneLocalToDataBaseApp extends AsyncTask<Void, Void, Boolean>{

	private final ProgressDialog dialog = new ProgressDialog(MainActivityListContact.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog.setMessage(getString(R.string.text_save_alert_wait));
		dialog.show();
	}
	@SuppressWarnings("finally")
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean retorno = false;
		try {
			if(mAdapter != null){
				
				ArrayList<Contact> mArrayContacts = mAdapter.getChequedItems();
					
				for (int i = 0; i < mArrayContacts.size(); i++) {		 												 
					
					
					
						if(!(mAdapter.isNumberExist(mArrayContacts.get(i).getPhoneNumber()))){
								mAdapter.addContactAdapter(mArrayContacts.get(i));										
						}
					
				}
				
				return true;
				 
			}
			
		} catch (Exception e) {
			retorno = false;
			Log.e(TAG, "Error sending contacts AsyncSendContactPhoneLocalToDataBaseApp");
			// TODO: handle exception
		}finally{
			return retorno;
		}
		
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		//progressBar.setVisibility(View.INVISIBLE);
		 if (dialog.isShowing()) {
		        dialog.dismiss();
		     }
		     if (result.booleanValue()) {
		    	 Toast.makeText(MainActivityListContact.this, getString(R.string.text_done), Toast.LENGTH_SHORT).show();
		    	 
		    	 
		    	 
		     }
	}
	
}
//****************************************************************************************************
}
