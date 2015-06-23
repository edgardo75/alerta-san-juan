package com.edadevsys.sanjuanalerta2.database;

import java.util.ArrayList;
import java.util.List;

import com.edadevsys.sanjuanalerta2.model.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHandler extends SQLiteOpenHelper {
	
	private static final String TAG = "DataBaseHandler.java";
	//All Static variable
	//DataBase Version	
	private static final int DATABASE_VERSION = 1;
	
	//Data Base Name
	private static final String DATABASE_NAME = "contactsManager";
	
	//Contacts table Name
	private static final String TABLE_CONTACTS = "contacts";
	
	//Contacts table Columns Names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_PH_NO = "phone_number";
	
	//Delete statement
	private static final String DELETE_ROW_TABLE = "DELETE FROM " +TABLE_CONTACTS +" WHERE "+ KEY_ID +" = ";
	private static final String DELETE_ALL_ROW_TABLE = "DELETE FROM " +TABLE_CONTACTS;
	
	//Select phone number
	private static final String SELECT_PHONE_NUMBER = "SELECT * FROM "+TABLE_CONTACTS+" WHERE "+KEY_PH_NO+"=";
		
	private static DataBaseHandler databaseinstance = null;
	

	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}


	public DataBaseHandler(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}


	public static DataBaseHandler getDataBaseInstance(Context context){
		
		if (databaseinstance==null){
			databaseinstance = new DataBaseHandler(context.getApplicationContext());
		}
	
		return databaseinstance;   
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" +
				"" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " + KEY_NAME + " TEXT,"
					+ KEY_PH_NO +" TEXT NOT NULL UNIQUE" +")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Drop older table if existed
		
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
		
		//Create table again
		
		onCreate(db);

	}
	
	
	//search phone number of contact
	@SuppressWarnings("finally")
	public boolean isNumberExist(String number){
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = null;
		
		boolean retorno = false;
		
		try {
			
			cursor = db.rawQuery(SELECT_PHONE_NUMBER+ "'" +number+ "'", null);

			cursor.moveToLast();
			
		 	int count = cursor.getCount();
		 	
		 	if (cursor!=null && count>0){
		 		
		 		retorno = true;
		 		
		 	}
			
		 	
			
		} catch (SQLiteConstraintException e) {			
			Log.e(TAG,"The Number Exists class DataBaseHandler");
		}finally{
			cursor.close();
			db.close();			
			return retorno;
		}
		
	
		
	}
	// Adding new contact
	public void addContact(Contact contact) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			
			
			ContentValues values = new ContentValues();
			
			values.put(KEY_NAME, contact.getName());
			values.put(KEY_PH_NO, contact.getPhoneNumber());
			
			db.insert(TABLE_CONTACTS, null, values);
			
			db.close();
			
		} catch (SQLiteConstraintException e) {
			Log.e(TAG, "Error at the insert record");
		}finally{
			
			db.close();
			
		}
			
	}
	 
	// Getting single contact
	public Contact getContact(int id) {
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CONTACTS,new String[]{KEY_ID,KEY_NAME,KEY_PH_NO},KEY_ID + "?=",
				new String[]{String.valueOf(id)},null,null,null);
		
		if(cursor != null)
			cursor.moveToFirst();
			
		Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),				
				cursor.getString(1),cursor.getString(2));
		
		return contact;
	}
	 
	// Getting All Contacts
	@SuppressWarnings("finally")
	public List<Contact> getAllContacts() {
		
		List<Contact>contactList = new ArrayList<Contact>();
		
		//Select all Query
		String selectQuery = "SELECT * FROM "+TABLE_CONTACTS;
		
		SQLiteDatabase db = getWritableDatabase();
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		try {
				//looping through all rows and adding to list
					if(cursor.moveToFirst()){
						
						do{
							
							Contact contact = new Contact();
							
							contact.setID(Integer.parseInt(cursor.getString(0)));
							
				            contact.setName(cursor.getString(1));
				            
				            contact.setPhoneNumber(cursor.getString(2));
				            
				            // Adding contact to list
				            contactList.add(contact);				
				            
						}while(cursor.moveToNext());
						
					}
		} catch (Exception e) {
			Log.e(TAG, "Error read contact list");
		}finally{
			cursor.close();
			return contactList;
		}			
		
		
	}
	
	
	 
	// Getting contacts Count
	public int getContactsCount() {
		
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
		
        	SQLiteDatabase db = getReadableDatabase();
        
        	Cursor cursor = db.rawQuery(countQuery, null);
        
        	int count = cursor.getCount();
        
        	cursor.close();
 
        return count;
	}
	// Updating single contact
	public int updateContact(Contact contact) {
		
		SQLiteDatabase db = getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    
	    values.put(KEY_NAME, contact.getName());
	    
	    values.put(KEY_PH_NO, contact.getPhoneNumber());
	 
	    // updating row
	    return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	}
	 
	// Deleting single contact
	public void deleteContact(Contact contact) {
		
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();		
	    
	    db.execSQL(DELETE_ROW_TABLE+contact.getID());
		
		db.setTransactionSuccessful();
		
		db.endTransaction();
		
	    db.close();
	}
	
	public void deleteAllRows(){
		
		SQLiteDatabase db = databaseinstance.getWritableDatabase();
		
		db.execSQL(DELETE_ALL_ROW_TABLE);
		
		db.close();
	}
	
	public boolean chekDataBase(Context context){
		
		SQLiteDatabase checkDB = null;
		
		try {
			checkDB = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e) {
			Log.e(TAG,"Failed to check existence DataBase");
		}
		return checkDB != null ? true :false;
		
	}

}
