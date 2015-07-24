package com.edadevsys.sanjuanalerta2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.edadevsys.sanjuanalerta2.adapter.MultiSelectionAdapter;
import com.edadevsys.sanjuanalerta2.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class MainActivityListContact extends Activity implements OnClickListener {

    private static final String TAG = "MaAcLiCo.java";

    private ListView mListView = null;

    private Button btnSaveChequedItems = null;

    private ArrayList<Contact> mainContactsArrayList = null;

    private HashMap<String, String> data = null;

    private HashSet<String> numbers = null;

    private MultiSelectionAdapter mAdapter = null;

    private Cursor mainCursor = null;


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

        mainContactsArrayList = new ArrayList<>();

        data = new HashMap<>(0);

        numbers = new HashSet<>(0);

        getAllContacts();

        mAdapter = new MultiSelectionAdapter(MainActivityListContact.this, mainContactsArrayList);

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

        if (contactSelected > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityListContact.this);
            builder.setMessage(R.string.dialog_message_aviso_message_add_contacts);
            builder.setTitle(R.string.dialog_message_aviso_message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

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

        } else {
            Toast.makeText(MainActivityListContact.this, getString(R.string.text_select_contact_at_the_least), Toast.LENGTH_SHORT).show();
        }


    }

    private void getAllContacts() {
        try {

            String[] projection = new String[]{

                    ContactsContract.Contacts._ID,
            };

            mainCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

            final int contactIdColumnIndex = mainCursor.getColumnIndex(ContactsContract.Contacts._ID);

            if (mainCursor.moveToFirst()) {

                while (!mainCursor.isAfterLast()) {        // still a valid entry left?

                    final int contactId = mainCursor.getInt(contactIdColumnIndex);


                    workingWithContactId(contactId);


                    mainCursor.moveToNext();            // move to the next entry
                }
            }


        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        } finally {
            mainCursor.close();
        }

    }


    private void workingWithContactId(int contactId) {
        Cursor mContacts = null;

        try {
            final String[] projection = new String[]{
                    Contacts.DISPLAY_NAME,    // the name of the contact
                    Contacts.PHOTO_ID,
            };
            mContacts = getContentResolver().query(Contacts.CONTENT_URI, projection,
                    Contacts._ID + "=?", new String[]{String.valueOf(contactId)}, Contacts.DISPLAY_NAME);

            while (mContacts.moveToNext()) {


                contactsTratment(mContacts, contactId);

            }//end while


            contactAddIntoHashMapList();


        } catch (Exception e) {
            Log.e(TAG, "Error in mETHOD WORKINGWITHCONTACT ID");
        } finally {

            assert mContacts != null;
            mContacts.close();
        }


    }

    private void contactAddIntoHashMapList() {
        String mName;
        String mNumber;

        for (Entry<String, String> entry : data.entrySet()) {

            String name = entry.getKey();
            String number = entry.getValue();
            mName = null;
            mNumber = null;

            if (numbers.add(name)) {

                mName = name;
                mNumber = number;

            }

            if (mName != null && mNumber != null) {

                Contact contact = new Contact();

                contact.setName(mName);
                contact.setPhoneNumber(mNumber);

                mainContactsArrayList.add(contact);

            }
        }

    }


    private void contactsTratment(Cursor mContacts, int contactId) {
        Cursor m_Phone = null;
        try {
            if (mContacts.moveToFirst()) {

                final String name = mContacts.getString(
                        mContacts.getColumnIndex(Contacts.DISPLAY_NAME));

                String[] projectionPhone = new String[]{
                        Phone.NUMBER,
                        Phone.TYPE,

                };
                m_Phone = getContentResolver().query(Phone.CONTENT_URI,
                        projectionPhone,
                        Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);


                if (m_Phone.moveToFirst()) {
                    final int contactNumberColumnIndex = m_Phone.getColumnIndex(Phone.NUMBER);
                    final int contactTypeColumnIndex = m_Phone.getColumnIndex(Phone.TYPE);

                    String number = null;
                    while (!m_Phone.isAfterLast()) {


                        final int type = m_Phone.getInt(contactTypeColumnIndex);


                        switch (type) {

                            case Phone.TYPE_MOBILE: {

                                // mobile number
                                number = m_Phone.getString(contactNumberColumnIndex);
                                break;
                            }

                        }//end switch

                        if (number != null) {
                            data.put(name, number);


                        }
                        m_Phone.moveToNext();


                    }//end while


                }

            }


        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            assert m_Phone !=null;
            m_Phone.close();
        }


    }

    //********************************************************************************************

    @SuppressLint("InlinedApi")
    public class AsyncSendContactPhoneLocalToDataBaseApp extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivityListContact.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage(getString(R.string.text_save_alert_wait));
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (mAdapter != null) {

                    ArrayList<Contact> mArrayContacts = mAdapter.getChequedItems();
                    int sizeArrayContacts = mArrayContacts.size();
                    for (int i = 0; i < sizeArrayContacts; i++) {


                        if (!(mAdapter.isNumberExist(mArrayContacts.get(i).getPhoneNumber()))) {
                            mAdapter.addContactAdapter(mArrayContacts.get(i));
                        }

                    }

                    return true;

                }

            } catch (Exception e) {
                Log.e(TAG, "");
                // TODO: handle exception
            }
            return false;


        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result) {
                Toast.makeText(MainActivityListContact.this, getString(R.string.text_done), Toast.LENGTH_SHORT).show();


            }
        }

    }
//****************************************************************************************************
}
