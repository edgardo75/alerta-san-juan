package com.edadevsys.sanjuanalerta2.utils;

import java.util.HashMap;
import java.util.Map;

public class LocationPhones{
    private static HashMap<String, String> phoneLocality = new HashMap<String, String>();
    
    public static void createHashMapLocality(){

        phoneLocality.put("San Juan","0264154562700");
        phoneLocality.put("Rivadavia","0264154562700");
        phoneLocality.put("Zonda","0264154562700");
        phoneLocality.put("Rawson","0264154562700");
        phoneLocality.put("9 de Julio","0264154562700");
        phoneLocality.put("Chimbas","0264154562700");
        phoneLocality.put("Albardón","0264154562700");
        phoneLocality.put("Jáchal","0264154562700");
        phoneLocality.put("Iglesia","0264154562700");
        phoneLocality.put("Valle Fértil","0264154562700");
        phoneLocality.put("San Juan","0264154562700");
        phoneLocality.put("Caucete","0264154562700");
        phoneLocality.put("25 de Mayo","0264154562700");
        phoneLocality.put("Sarmiento","0264154562700");
        phoneLocality.put("Media Agua","0264154562700");
        phoneLocality.put("Calingasta","0264154562700");
        phoneLocality.put("Pocito","0264154562700");
        phoneLocality.put("Santa Lucía","0264154562700");
        phoneLocality.put("Angaco","0264154562700");
        phoneLocality.put("San Martín","0264154562700");
        phoneLocality.put("Ullun","0264154562700");
        phoneLocality.put("Villa Aberastain","0264154562700");


    }
    public static String searchPhoneInMapStructure(String city){
        String cityGetInBack = "";

        if(!phoneLocality.isEmpty()) {
            for (Map.Entry<String, String> entry : phoneLocality.entrySet()) {

                if (entry.getKey().equalsIgnoreCase(city)) {
                    cityGetInBack = entry.getValue();
                    break;
                }
            }
        }

        return cityGetInBack;
    }
}