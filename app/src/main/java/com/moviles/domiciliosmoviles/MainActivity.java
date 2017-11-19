package com.moviles.domiciliosmoviles;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moviles.domiciliosmoviles.Services.GPSIntentService;
import com.moviles.domiciliosmoviles.adapters.PlatoAdapter;
import com.moviles.domiciliosmoviles.entities.Plato;
import com.moviles.domiciliosmoviles.persistance.PlatosDatabase;
import com.moviles.domiciliosmoviles.rest.RestClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {


    public static final String GPS_FILTER = "GPSFilter";
    private ListView listView;
    private List<Plato> platos = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog loadingDialog;
    private GPSReceiver receiver;
    private TextView gpsText;
    private PlatosDatabase databaseHelper;
    private FloatingActionButton fab;

    public static final String SYNC_WIFI = "sync_wifi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        listView=(ListView)findViewById(R.id.platos_listview);
        databaseHelper = new PlatosDatabase(this);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlatoDialogo();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,PedidosActivity.class);
                i.putExtra("name", ((Plato)platos.get(position)).getNombre());
                i.putExtra("id", ((Plato)platos.get(position)).getId());
                startActivity(i);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlatos();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        getPlatos();

        gpsText = (TextView) findViewById(R.id.gps_textview);
        receiver = new GPSReceiver(gpsText);
        this.registerReceiver(receiver, new IntentFilter(GPS_FILTER));
        Intent intent = new Intent(this, GPSIntentService.class);
        intent.setAction(GPSIntentService.GETPOSITION);
        this.startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alert:
                return true;
            case R.id.action_sincronizar:
                sincronizarTabla();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hello World")
                .setTitle("Dialogo de Alerta")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do something
                    }
                });
        return builder.create();
    }

    public void getPlatos() {

        platos = databaseHelper.getAllPlatos();
        PlatoAdapter itemsAdapter = new PlatoAdapter(MainActivity.this,platos);
        listView.setAdapter(itemsAdapter);
        mSwipeRefreshLayout.setRefreshing(false);

        /*AsyncTask getPlatosWithDialog = new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                loadingDialog = new ProgressDialog(MainActivity.this);
                loadingDialog.setMessage("Cargando Platos");
                loadingDialog.setTitle("Espera...");
                loadingDialog.show();
            }
            @Override
            protected Void doInBackground(Void... params) {

                Plato plato1 = new Plato(1,"Prueba",300,"https://static01.nyt.com/images/2016/03/21/multimedia/recipe-lab-kale-caesar/recipe-lab-kale-caesar-superJumbo.jpg");
                Plato plato2 = new Plato(2,"Prueba2",300,"http://www.loquesecocinaenestacasa.com/web/media/k2/items/cache/7e64c4d2a4a242251ffdaa790b21fa01_XL.jpg");

                platos = new ArrayList();
                platos.add(plato1);
                platos.add(plato2);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PlatoAdapter itemsAdapter = new PlatoAdapter(MainActivity.this, platos);
                if(listView!=null) {
                    listView.setAdapter(itemsAdapter);
                }if(loadingDialog!=null){
                    loadingDialog.dismiss();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute();*/
    }

    private class GPSReceiver extends BroadcastReceiver {
        private TextView textView;
        private Exception exception;
        public GPSReceiver(TextView view){ textView = view; }
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("position");
            textView.setText(text);
        }
    }

    private void addPlatoDialogo() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_plato,null);
        new AlertDialog.Builder(this)
                .setTitle("Agregar Plato")
                .setView(view)
                .setPositiveButton("Agregar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String nombre = ((TextView)view.findViewById(R.id.plato_nombre)).getText().toString();
                                int precio = Integer.parseInt(((TextView)view.findViewById(R.id.plato_precio)).getText().toString());
                                Plato nuevo = new Plato(0,nombre,precio,"");
                                databaseHelper.addPlato(nuevo);
                                getPlatos();
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do something
                            }
                        }
                )
                .create().show();
    }


    private boolean connectedToWifi(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void sincronizarTabla(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean syncConnPref = sharedPref.getBoolean(SYNC_WIFI, true);
        if(syncConnPref){
            if(connectedToWifi()){
                sincronizarPlatos();
            }else{
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("No hay conexcion WIFI")
                        .setPositiveButton("Ok", null)
                        .create().show();
            }
        }else{
            sincronizarPlatos();
        }
    }

    private void sincronizarPlatos()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for(int i=0; i<platos.size(); i++){
            DatabaseReference myRef = database.getReference("platos/"+platos.get(i).getId());
            myRef.setValue(platos.get(i));
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle("Sincronizacion exitosa")
                .setMessage("incronizacion exitosa")
                .setPositiveButton("Ok", null)
                .create().show();
    }



}
