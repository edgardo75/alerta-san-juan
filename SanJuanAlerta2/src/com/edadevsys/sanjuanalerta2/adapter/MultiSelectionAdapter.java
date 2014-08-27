package com.edadevsys.sanjuanalerta2.adapter;

import java.util.ArrayList;

import com.edadevsys.sanjuanalerta2.R;

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

import com.edadevsys.sanjuanalerta2.database.DataBaseHandler;
import com.edadevsys.sanjuanalerta2.model.Contact;

public class MultiSelectionAdapter extends BaseAdapter {
	
	Context context = null;
	
	LayoutInflater mInflater = null;
	
	ArrayList<Contact> mList = null;
	
	SparseBooleanArray mSparseBooleanArray = null;
	
	public MultiSelectionAdapter(Context context,ArrayList<Contact> list){
		
		this.context = context;
		
		mInflater = LayoutInflater.from(context);
		
		mSparseBooleanArray = new SparseBooleanArray();
		
		this.mList = new ArrayList<Contact>();
		
		this.mList = list;
		
	}
	
	
	
	public ArrayList<Contact> getChequedItems(){
		ArrayList<Contact> mTempArray = new ArrayList<Contact>();
		
		for(int i = 0;i <mList.size() ;i++){
			
			if( mSparseBooleanArray.get(i)){
				
				mTempArray.add(mList.get(i));
			}
			
		}
		
		return mTempArray;
	}

	public void unSelectChequedItems(){
		
		mSparseBooleanArray.clear();
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public void addContactAdapter(Contact contact){
		
		DataBaseHandler.getDataBaseInstance(context).addContact(contact);
		
	}
	
	public boolean isNumberExist(String number){
		
		return DataBaseHandler.getDataBaseInstance(context).isNumberExist(number);
		
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position).getName();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 
		Contact contact = mList.get(position);
		if(convertView == null){
			
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
	
	private static class ViewHolder{
		public String name;

		public String getNameContact() {
			return name;
		}

		public void setNameContact(String name) {
			this.name = name;
		}
		
	}
	
	OnCheckedChangeListener mChequedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			mSparseBooleanArray.put((Integer)buttonView.getTag(), isChecked);
			
		}
	};

}
