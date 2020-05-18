package com.stringmanolo.rs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.stringmanolo.rs.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Carga en la pantalla la interfaz de la aplicación. */
        setContentView(R.layout.activity_main);

        /* Manda un intent a la clase Servicio extends Service */
        Intent i = new Intent(this, Servicio.class);
        startService(i);
    } /* Fin onCreate */
} /* Fin MainActivity */
