package com.edadevsys.sanjuanalerta2.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.edadevsys.sanjuanalerta2.utils.LocationPhones;
import java.io.IOException;
import java.util.List;


public class LocalizationApp extends Activity implements LocationListener {

    public static boolean isGPS_enabled = false;
    public static boolean isNETWORK_enabled = false;
    private Location location;
    private Geocoder geocoder;
    private Criteria criteria = null;
    protected String provider;
    protected LocationManager locationManager;
    public static boolean configChangeRestore = false;

    public LocalizationApp(){}

    public LocalizationApp(Location location,Geocoder geocoder){
        this.location = location;
        this.geocoder = geocoder;

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {

            getGeoCodeFromLatLon(location);

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String updateScreen(Location location) {
        return obtainLatitud()+" "+obtainLongitude();
    }

    private String obtainLatitud(){
        return String.valueOf(location.getLatitude());
    }

    private String obtainLongitude(){
        return String.valueOf(location.getLongitude());
    }
    public String[] getGeoCodeFromLatLon(Location location) {

        List<Address> addresses;
        String street;
        String number;
        String city;
        String[]geoLocationUser = null;


        try {


            if(geocoder != null) {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {

                    street = addresses.get(0).getThoroughfare();
                    number = addresses.get(0).getSubThoroughfare();
                    city = addresses.get(0).getLocality();

                    geoLocationUser[0] = street;
                    geoLocationUser[1] = number;
                    geoLocationUser[2] = city;
                    geoLocationUser[3] = LocationPhones.searchPhoneInMapStructure(city);


                }
            }

        } catch (NullPointerException e) {
            Log.e("", "Locale is null");

        } catch (IOException e) {
            Log.e("", "Service Not Available");

        } catch (Exception e) {
            Log.e("", "Error in method getGeoCodeFromLatLon class MainActivity");
        }
        return geoLocationUser;
    }

    public void initLocation() {
        try {
            // retrieve LocationManager from GetSystemServices
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            initializeCriteriaLatitudeLongitude();
            provider = locationManager.getBestProvider(criteria, false);
        } catch (Exception e) {
            Log.e("", "Error in Method init class LocalizationApp");
        }

    }

    public void initializeCriteriaLatitudeLongitude() {

        criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_LOW);

        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setSpeedRequired(true);

        criteria.setAltitudeRequired(false);

        criteria.setBearingRequired(false);

        criteria.setCostAllowed(false);

    }

    public void isProviderEnabled() {

        isGPS_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNETWORK_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    public void confingChangeRestore(){
        if (configChangeRestore) {

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            startActivity(intent);
        }

    }



}
