package com.example.ricardogarcia.contactsmanager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ContactDescription extends AppCompatActivity {

    private EditText textId;
    private EditText textName;
    private EditText textPhone;
    private EditText textLocation;
    private ImageView image;
    private boolean isUpdate;
    private Contact c;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUpdate=false;
        setContentView(R.layout.activity_contact_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textId= (EditText) findViewById(R.id.textId);
        textName= (EditText) findViewById(R.id.textName);
        textPhone= (EditText) findViewById(R.id.textPhone);
        textLocation= (EditText) findViewById(R.id.textLocation);
        image= (ImageView) findViewById(R.id.image);


        Intent intent= getIntent();
        Bundle b=intent.getExtras();
        pos=b.getInt(ContactAdapter.CONTACT);
        c=Application.listContacts.get(pos);
        textId.setText(c.getId());
        textName.setText(c.getName());
        textPhone.setText(c.getPhone());
        textLocation.setText(c.getLocation());
        if(c.getImage()!=null) {
            Bitmap bm = BitmapFactory.decodeByteArray(c.getImage(), 0, c.getImage().length);
            image.setImageBitmap(bm);
        }
        textId.setFocusable(false);
        textId.setFocusableInTouchMode(false);
        textId.setClickable(false);
        textName.setFocusable(false);
        textName.setFocusableInTouchMode(false);
        textName.setClickable(false);
        textPhone.setFocusable(false);
        textPhone.setFocusableInTouchMode(false);
        textPhone.setClickable(false);
        textLocation.setFocusable(false);
        textLocation.setFocusableInTouchMode(false);
        textLocation.setClickable(false);


        final FloatingActionButton fab_remove=(FloatingActionButton) findViewById(R.id.fab_remove);
        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application.listContacts.remove(pos);
                Intent intent= new Intent(ContactDescription.this,ViewContacts.class);
                startActivity(intent);
                finish();
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isUpdate) {
                    textName.setFocusable(true);
                    textName.setFocusableInTouchMode(true);
                    textName.setClickable(true);

                    textPhone.setFocusable(true);
                    textPhone.setFocusableInTouchMode(true);
                    textPhone.setClickable(true);
                    isUpdate = true;
                    Bitmap bmp=BitmapFactory.decodeResource(getResources(),android.R.drawable.ic_menu_save);
                    fab.setImageBitmap(bmp);
                    fab_remove.setVisibility(View.VISIBLE);

                } else {

                    if(textName.getText().toString().equals("")) {
                        Toast.makeText(ContactDescription.this, getString(R.string.noName), Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    c.setName(textName.getText().toString());
                    c.setPhone(textPhone.getText().toString());
                    Application.listContacts.set(pos, c);
                    Intent intent= new Intent(ContactDescription.this,ViewContacts.class);
                    startActivity(intent);
                    finish();
                }

            }
        });



    }

}
