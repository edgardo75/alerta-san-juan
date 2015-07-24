package com.edadevsys.sanjuanalerta2.model;

public class Contact {

    //instance variable
    private int _id;
    private String _name;
    private String _phone_number;

    // Empty Constructor
    public Contact() {

    }

    // --Commented out by Inspection START (13/07/2015 11:23):
//    // constructor
//    public Contact(String name, String _phone_number) {
//        this._name = name;
//        this._phone_number = _phone_number;
//    }
// --Commented out by Inspection STOP (13/07/2015 11:23)

    // getting id
    public int getID() {
        return _id;
    }

    // setting id
    public void setID(int _id) {
        this._id = _id;
    }

    // getting name
    public String getName() {
        return _name;
    }

    // setting name
    public void setName(String _name) {
        this._name = _name;
    }

    // getting phone_number
    public String getPhoneNumber() {
        return _phone_number;
    }

    // setting phone_number
    public void setPhoneNumber(String _phone_number) {
        this._phone_number = _phone_number;
    }


}
