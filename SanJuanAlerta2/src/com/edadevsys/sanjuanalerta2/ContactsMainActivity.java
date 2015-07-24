package com.edadevsys.sanjuanalerta2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.edadevsys.sanjuanalerta2.database.DataBaseHandler;
import com.edadevsys.sanjuanalerta2.model.Contact;

public class ContactsMainActivity extends Activity implements OnClickListener {

    private static final String TAG = "CMActivity.java";
    private static final int PICK_CONTACT_REQUEST = 1;
    private Uri contactUri;
    private String[] projection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_main_activity);

        Button btnSelectContact = (Button) findViewById(R.id.btnSelectContact);

        Button btnViewContact = (Button) findViewById(R.id.btnViewContacts);

        Button btnGoHome = (Button) findViewById(R.id.btnGoMain);

        btnSelectContact.setOnClickListener(this);

        btnViewContact.setOnClickListener(this);

        btnGoHome.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSelectContact:

                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                if (pickContactIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                }
                break;

            case R.id.btnViewContacts:
                //connect to database for count row

                int countContact = DataBaseHandler.getDataBaseInstance(ContactsMainActivity.this).getContactsCount();

                if (countContact > 0) {

                    Intent intentViewContact = new Intent(ContactsMainActivity.this, ContactList.class);

                    startActivity(intentViewContact);

                    finish();
                } else {

                    Toast.makeText(ContactsMainActivity.this, getString(R.string.text_no_load_contacts), Toast.LENGTH_LONG).show();

                }

                break;

            case R.id.btnGoMain:

                finish();

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);




        // Check which request it is that we're responding to
        try {


            if (requestCode == PICK_CONTACT_REQUEST) {

				                // Make sure the request was successful
				                if (resultCode == RESULT_OK) {
				
				                    // Get the URI that points to the selected contact
				                    contactUri = data.getData();
				                    // We only need the NUMBER column, because there will be only one row in the result
				                    // String[] projection = {Phone.NUMBER};
				
				                    // Perform the query on the contact to get the NUMBER column
				                    // We don't need a selection or sort order (there's only one result for the given URI)
				                    // CAUTION: The query() method should be called from a separate thread to avoid blocking
				                    // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
				                    // Consider using CursorLoader to perform the query.
				
				                    projection = new String[]{
				                            ContactsContract.Contacts.DISPLAY_NAME,
				                            ContactsContract.Contacts.HAS_PHONE_NUMBER,
				                            ContactsContract.Contacts._ID,
				                    };
				                    
				                    obtainContacts();
				                   
									
				                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult");
        } 

                
    }

	private void obtainContacts() {
		// TODO Auto-generated method stub
        Cursor phone;

        Cursor cursor = getContentResolver()
                .query(contactUri, projection, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?",
                        new String[]{"1"}, null);
        try {


            if (cursor != null && cursor.moveToFirst()) {


                // Retrieve the phone number from the NUMBER column

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

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
                String number = phone.getString(column);


                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhoneNumber(number);

                boolean isNumberExist = DataBaseHandler.getDataBaseInstance(ContactsMainActivity.this).isNumberExist(number);
                int count = DataBaseHandler.getDataBaseInstance(ContactsMainActivity.this).getContactsCount();


                if (count == 10) {
                    Toast.makeText(ContactsMainActivity.this, getString(R.string.text_list_fill), Toast.LENGTH_LONG).show();
                } else {
                    if (!(isNumberExist)) {

                        DataBaseHandler.getDataBaseInstance(ContactsMainActivity.this).addContact(contact);
                        Toast.makeText(ContactsMainActivity.this, getString(R.string.text_saved), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ContactsMainActivity.this, getString(R.string.text_saved_allready), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(ContactsMainActivity.this, getString(R.string.text_no_phone_contact), Toast.LENGTH_SHORT).show();
            }

        } finally {
            assert cursor != null;
            cursor.close();
        }


    }
}