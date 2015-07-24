package com.edadevsys.sanjuanalerta2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class MultiSelectionAdapter extends BaseAdapter {

    private Context context = null;

    private LayoutInflater mInflater = null;

    private ArrayList<Contact> multiSelectedList = null;

    private SparseBooleanArray mSparseBooleanArray = null;
    private final OnCheckedChangeListener mChequedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);

        }
    };


    public MultiSelectionAdapter(Context context, ArrayList<Contact> listSelected) {

        this.context = context;

        mInflater = LayoutInflater.from(context);

        mSparseBooleanArray = new SparseBooleanArray(0);

        this.multiSelectedList = new ArrayList<>();

        this.multiSelectedList = listSelected;

    }

    public ArrayList<Contact> getChequedItems() {

        ArrayList<Contact> mTempArrayContactSelected = new ArrayList<>();

        int contactsSelectedCount = multiSelectedList.size();

        for (int i = 0; i < contactsSelectedCount; i++) {

            if (mSparseBooleanArray.get(i)) {

                mTempArrayContactSelected.add(multiSelectedList.get(i));
            }

        }

        return mTempArrayContactSelected;
    }

// --Commented out by Inspection START (13/07/2015 11:23):
//    public void unSelectChequedItems() {
//
//        mSparseBooleanArray.clear();
//
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return multiSelectedList.size();
    }

    public void addContactAdapter(Contact contact) {

        DataBaseHandler.getDataBaseInstance(context).addContact(contact);

    }

    public boolean isNumberExist(String number) {

        return DataBaseHandler.getDataBaseInstance(context).isNumberExist(number);

    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return multiSelectedList.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //
        Contact contact = multiSelectedList.get(position);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.row, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.setNameContact(contact.getName());

            convertView.setTag(viewHolder);

        }

        ViewHolder vHolder = (ViewHolder) convertView.getTag();

        TextView tvNameContact = (TextView) convertView.findViewById(R.id.tvNameContact);

        tvNameContact.setText(vHolder.getNameContact());

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
