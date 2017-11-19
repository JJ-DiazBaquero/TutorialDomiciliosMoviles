package com.moviles.domiciliosmoviles.Services;


import android.content.AsyncTaskLoader;
import android.content.Context;


/**
 * Created by pc on 18/10/2017.
 */
public class DeliveryManTask extends AsyncTaskLoader {
    public DeliveryManTask(Context context) {
        super(context);
    }

    @Override
    public String loadInBackground() {
        try {
            Thread.sleep(3000);
            return "Domiciliario de Prueba";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
