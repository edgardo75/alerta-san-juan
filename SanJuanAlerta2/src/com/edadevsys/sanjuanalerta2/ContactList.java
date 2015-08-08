package com.edadevsys.sanjuanalerta2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edadevsys.sanjuanalerta2.adapter.ContactsAdapter;
public class ContactList extends Activity {

    private static final String TAG = "ContactList.java";

    private ListView listView;

    private ContactsAdapter adapterContact;

    private View view;

    @SuppressWarnings("SameReturnValue")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_contact_db);

        bindComponents();

        init();



    }// end onCreate

    // init method to instantiate adapter
    private void init() {

        adapterContact = new ContactsAdapter(ContactList.this);

        listView.setAdapter(adapterContact);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulistcontactapp, menu);
        setMenuBackground();
        return true;
    }

    private void setMenuBackground() {
        // TODO Auto-generated method stub


        getLayoutInflater().setFactory(new Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
                    try { // Ask our inflater to create the view
                        LayoutInflater f = getLayoutInflater();
                        view = f.createView(name, null, attrs);
                        /* The background gets refreshed each time a new item is added the options menu.
                        * So each time Android applies the default background we need to set our own
	                    * background. This is done using a thread giving the background change as runnable
	                    * object */
                        menuBackGround();
                        return view;
                    } catch (InflateException | ClassNotFoundException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        delayMenu();
    }

    private void delayMenu() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openOptionsMenu();
            }
        },1000);

    }

    
    private void menuBackGround() {
        new Handler().post(new Runnable() {
            public void run() {
                // sets the background color
                view.setBackgroundResource(R.color.menubg);
                // sets the text color
                ((TextView) view).setTextColor(Color.WHITE);
                // sets the text size
                ((TextView) view).setTextSize(14);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.menu_del_select_row:

                int contactSelect = adapterContact.getChequedItems().size();
                if (contactSelect > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactList.this);

                    builder1.setTitle(R.string.dialog_message_aviso_message);

                    builder1.setMessage(R.string.dialog_message_borrado_select);

                    builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //delete selected contacts from listview (checkbox)
                            int delSeleted = delSelectedContacs();
                            if (delSeleted == 1) {

                                Toast.makeText(ContactList.this, getString(R.string.text_corret_operation), Toast.LENGTH_SHORT).show();

                                setupData();
                                if (adapterContact.getCount() == 0) {

                                    //Intent intent = new Intent(ContactList.this,ContactsMainActivity.class);
                                    //startActivity(intent);
                                    finish();
                                }
                            } else {

                                Toast.makeText(ContactList.this, getString(R.string.text_no_operation), Toast.LENGTH_SHORT).show();
                            }


                        }


                    });
                    builder1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

                    AlertDialog dialog1 = builder1.create();

                    dialog1.show();
                    delayMenu();

                } else {
                    Toast.makeText(ContactList.this, getString(R.string.text_select_contact_at_the_least), Toast.LENGTH_LONG).show();
                    delayMenu();
                }


                return true;

            case R.id.menu_del_all:

                AlertDialog.Builder builder = new AlertDialog.Builder(ContactList.this);

                builder.setTitle(R.string.dialog_message_aviso_message);

                builder.setMessage(R.string.dialog_message_borrado_all);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // dell all contacts from database
                        int dellAll = delAllContact();
                        if (dellAll == 1) {
                            Toast.makeText(ContactList.this, getString(R.string.text_corret_operation), Toast.LENGTH_SHORT).show();
                            // update adapter
                            setupData();

                            //Intent intent = new Intent(ContactList.this,ContactsMainActivity.class);
                            //startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(ContactList.this, getString(R.string.text_no_operation), Toast.LENGTH_SHORT).show();
                        }

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
                delayMenu();

                return true;


            case R.id.menu_return:

                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }


    }

    // method bindComponents
    private void bindComponents() {


        listView = (ListView) findViewById(android.R.id.list);

    }


    // method instantiate adapter
    private void setupData() {
        try {

            adapterContact = new ContactsAdapter(ContactList.this);

            listView.setAdapter(adapterContact);

        } catch (Exception e) {
            Log.e(TAG, "Error method setupData");
        }


    }

    @SuppressWarnings("finally")
    private int delSelectedContacs() {
        int retorno;
        try {

            retorno = adapterContact.dellSeletedItems();

        } catch (Exception e) {
            retorno = -1;
            Log.e(TAG, "Error at the delete table");
        }
        return retorno;


    }

    @SuppressWarnings("finally")
    private int delAllContact() {
        int retorno;
        try {

            retorno = adapterContact.dellAllRows();

        } catch (Exception e) {
            retorno = -1;
            Log.e(TAG, "Error at the delete table");
        }
        return retorno;

    }

}

