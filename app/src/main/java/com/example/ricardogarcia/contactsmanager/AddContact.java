package com.example.ricardogarcia.contactsmanager;

import android.app.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddContact extends AppCompatActivity implements LocationListener{

    private EditText textId;
    private EditText textName;
    private EditText textPhone;
    private ImageView image;
    private String path = "";
    private Bitmap bitmap;
    private static final int PHOTO_PATH_CODE = 1;
    private static final int TAKE_PHOTO_CODE = 2;
    private LocationManager locationManager;
    private Location location;
    private static String PHOTO="com.example.ricardogarcia.contactsmanager.PHOTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        textId= (EditText) findViewById(R.id.textId);
        textName= (EditText) findViewById(R.id.textName);
        textPhone= (EditText) findViewById(R.id.textPhone);
        image= (ImageView) findViewById(R.id.image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(textName.getText().toString().equals("")) {
                    Toast.makeText(AddContact.this, getString(R.string.noName), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                Contact c= new Contact();
                c.setId(textId.getText().toString());
                c.setName(textName.getText().toString());
                c.setPhone(textPhone.getText().toString());
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(AddContact.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    c.setLocation(city+"("+country+")");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                c.setImage(stream.toByteArray());
                Application.listContacts.add(c);
                Intent intent= new Intent(AddContact.this,ViewContacts.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void addPhoto(View view){
        final String option[]=new String[]{getString(R.string.take_photo),getString(R.string.choose_from_gallery),getString(R.string.delete)};
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(AddContact.this,android.R.layout.select_dialog_item,option);
        AlertDialog.Builder builder= new AlertDialog.Builder(AddContact.this);

        builder.setTitle(R.string.title_option);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        dispatchTakenPicture();
                        break;
                    case 1:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(photoPickerIntent, PHOTO_PATH_CODE);
                        break;
                    case 2:
                        image.setImageResource(R.mipmap.camera_ic);
                        bitmap=null;
                }
            }
        });
        final AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case PHOTO_PATH_CODE:
                if(resultCode==RESULT_OK){
                    if(data!=null){
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);
                        cursor.close();

                        try {

                            ExifInterface exif = new ExifInterface(path);
                            byte[] imageData = exif.getThumbnail();
                            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                            ExifInterface ei = new ExifInterface(path);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            Matrix matrix = new Matrix();
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        image.setImageBitmap(bitmap);
                    }
                }
                break;
            case TAKE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {

                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");

                    image.setImageBitmap(bitmap);
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


    private void dispatchTakenPicture() {
        Intent takePictureIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(bitmap!=null)
            outState.putParcelable(PHOTO, bitmap);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.getParcelable(PHOTO)!=null) {
            bitmap = savedInstanceState.getParcelable(PHOTO);
            image.setImageBitmap(bitmap);
        }

    }
}
