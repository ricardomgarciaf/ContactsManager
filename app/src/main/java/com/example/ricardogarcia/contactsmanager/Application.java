package com.example.ricardogarcia.contactsmanager;

import java.util.ArrayList;

/**
 * Created by ricardogarcia on 1/22/16.
 */
public class Application extends android.app.Application {
    public static ArrayList<Contact> listContacts;

    @Override
    public void onCreate() {
        super.onCreate();
        listContacts= new ArrayList<Contact>();
    }
}
