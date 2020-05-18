package com.stringmanolo.rs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.stringmanolo.rs.Conexion;

public class Servicio extends Service {
    private static Context c;
    
    public void ServicioPrincipal() {
        /* Conectar está definido en el archivo Conexion.java
        Inicia la función lógica principal del programa */
        Conexion.Conectar();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
   
    @Override
    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        c = this;
        Conexion.Conectar();
        return Service.START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    public static Context getContextOfApplication() {
        return c;
    }
}
