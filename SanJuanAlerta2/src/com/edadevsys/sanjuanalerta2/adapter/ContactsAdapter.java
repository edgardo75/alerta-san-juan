package com.edadevsys.sanjuanalerta2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.edadevsys.sanjuanalerta2.R;
import com.edadevsys.sanjuanalerta2.database.DataBaseHandler;
import com.edadevsys.sanjuanalerta2.model.Contact;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class ContactsAdapter extends BaseAdapter {
    private static final String TAG = "ContactsAdapter.java";
    private Context context = null;
    private LayoutInflater inflater = null;
    private List<Contact> contacts = null;

    private SparseBooleanArray mSparseBooleanArray = null;
    private final OnCheckedChangeListener mChequedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub

            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);

        }
    };

    public ContactsAdapter(Context context) {
        super();

        this.context = context;

        mSparseBooleanArray = new SparseBooleanArray(0);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contacts = DataBaseHandler.getDataBaseInstance(context).getAllContacts();

    }

// --Commented out by Inspection START (13/07/2015 11:23):
//    public ContactsAdapter(Context context, LayoutInflater inflater, ArrayList<Contact> contacts) {
//
//        super();
//
//        mSparseBooleanArray = new SparseBooleanArray(0);
//
//        this.context = context;
//
//        this.inflater = inflater;
//
//        this.contacts = contacts;
//
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

// --Commented out by Inspection START (13/07/2015 11:23):
//    public void addContactAdapter(Contact contact) {
//
//        DataBaseHandler.getDataBaseInstance(context).addContact(contact);
//
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

    @Override
    public int getCount() {

        return contacts.size();

    }

// --Commented out by Inspection START (13/07/2015 11:23):
//    public boolean isNumberExist(String number) {
//
//        return DataBaseHandler.getDataBaseInstance(context).isNumberExist(number);
//
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

    @Override
    public Contact getItem(int position) {

        return contacts.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

// --Commented out by Inspection START (13/07/2015 11:23):
//    public ArrayList<Contact> getAllItems() {
//
//		/*List<Contact> mTempArrayContactsDb = DataBaseHandler.getDataBaseInstance(context).getAllContacts();
//
//		ArrayList<Contact> mTempArrayContact = new ArrayList<Contact>();
//
//		for(Contact cn: mTempArrayContactsDb){
//
//			mTempArrayContact.add(cn);
//
//		}*/
//
//        return (ArrayList<Contact>) DataBaseHandler.getDataBaseInstance(context).getAllContacts();
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

    public ArrayList<Contact> getChequedItems() {

        ArrayList<Contact> mTempArrayChequed = new ArrayList<>();


        int contactsCount = contacts.size();

        for (int i = 0; i < contactsCount; i++) {

            if (mSparseBooleanArray.get(i)) {

                mTempArrayChequed.add(contacts.get(i));
            }

        }

        return mTempArrayChequed;
    }

    public int dellAllRows() {

        int retorno;

        try {

            DataBaseHandler.getDataBaseInstance(context).deleteAllRows();

            retorno = 1;

        } catch (Exception e) {
            retorno = -1;
            Log.e(TAG, "Error in method dellAllRows class ContacsAdapter");
        }


        return retorno;
    }

    public int dellSeletedItems() {
        int retorno;
        try {
            ArrayList<Contact> mTemArraySelectedContacts = getChequedItems();
            int contactsCountSelected = mTemArraySelectedContacts.size();

            for (int i = 0; i < contactsCountSelected; i++) {

                DataBaseHandler.getDataBaseInstance(context).deleteContact(mTemArraySelectedContacts.get(i));

            }

            retorno = 1;
        } catch (Exception e) {
            retorno = -1;
            Log.e(TAG, "Failed to Delete Selected Records");
        }

        return retorno;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = contacts.get(position);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.row, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.setNameContact(contact.getName());

            convertView.setTag(viewHolder);
        }

        ViewHolder vHolder = (ViewHolder) convertView.getTag();

        TextView nameContact = (TextView) convertView.findViewById(R.id.tvNameContact);

        nameContact.setText(vHolder.getNameContact());

        CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.chkEnable);

        mCheckBox.setTag(position);

        mCheckBox.setOnCheckedChangeListener(mChequedChangeListener);

        return convertView;
    }

    private static class ViewHolder {
        public String name;

        public String getNameContact() {
            return name;
        }

        public void setNameContact(String name) {
            this.name = name;
        }

    }
}
