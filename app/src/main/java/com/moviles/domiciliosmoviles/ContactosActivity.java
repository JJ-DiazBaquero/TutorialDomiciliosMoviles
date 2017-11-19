package com.moviles.domiciliosmoviles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;

import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactosActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    private final static int[] TO_IDS = {
            android.R.id.text1
    };


    ListView contactslv;
    SimpleCursorAdapter mCursorAdapter;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactslv = (ListView)findViewById(R.id.contacts_listView);
        contactslv.setOnItemClickListener(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{
            Log.d("Prueba","Si hay permisos");
            getLoaderManager().initLoader(
                    0,  // The identifier of the loader to initialize
                    null,       // Arguments for the loader (in this case, none)
                    this);      // The context of the activity

            // Creates a new cursor adapter to attach to the list view
            mCursorAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.detail_contact,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0);
            // Sets the ListView's backing adapter.
            contactslv.setAdapter(mCursorAdapter);

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // Si un request es cancelado el array resultado es vacío
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Ahora que el permiso fue otorgado se debe poner en este espacio lo relacionado con la solicitud de contactos.
                    getLoaderManager().initLoader(
                            0,  // The identifier of the loader to initialize
                            null,       // Arguments for the loader (in this case, none)
                            this);      // The context of the activity
                    // Creates a new cursor adapter to attach to the list view
                    mCursorAdapter = new SimpleCursorAdapter(
                            this,
                            R.layout.detail_contact,
                            null,
                            FROM_COLUMNS, TO_IDS,
                            0);
                    // Sets the ListView's backing adapter.
                    contactslv.setAdapter(mCursorAdapter);

                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
