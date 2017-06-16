package com.galeno.patricio.testbluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.galeno.patricio.testbluetooth.conexion.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Patricio on 15-06-2017.
 */

public class ConectedActivity extends AppCompatActivity implements View.OnClickListener{
    private String MAC;
    private UUID MY_UUID;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private BluetoothAdapter btAdapter;
    private EditText editText;
    private TextView textView;
    private Button button;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conected);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        editText = (EditText) findViewById(R.id.plain_text_input);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            MAC = (String) extras.get("MAC");
            MY_UUID = (UUID) extras.get("MY_UUID");
        }
        BluetoothDevice device = btAdapter.getRemoteDevice(MAC);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            showToast("Fatal Error"+ "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        // Establish the connection.  This will block until it connects.
        try {
            btSocket.connect();
            System.out.println("CONECTADO");
        } catch (Exception e) {
            System.out.println("Catch: "+e.getMessage());
            try {
                showToast("Fatal Error"+ "In onResume() and unable to connect socket during connection failure" + e.getMessage() + ".");
                btSocket.close();
            } catch (IOException e2) {
                showToast("Fatal Error"+ "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            showToast("Fatal Error"+ "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
        listen();
    }

    public void listen() {
        //RECIBIR

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(Void... params) {
                try{
                    inStream = btSocket.getInputStream();
                    while(true){
                        BufferedReader r = new BufferedReader(new InputStreamReader(inStream));

                        System.out.println("LISTO PARA LEER");
                        final String line=r.readLine();
                        System.out.println("LEIDO");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(line);

                            }
                        });
                        /*AsyncTask<Void, Void, String> task2 = new AsyncTask<Void, Void, String>() {

                            @Override
                            protected void onPreExecute() {
                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                System.out.println("HIJO DE HIJO");
                                String resultado = new Server().connectToServer("http://galenoproject.sytes.net/data/?name=ECG&data=16&save=yes", 15000);
                                System.out.println(resultado);
                                return resultado;
                            }

                            @Override
                            protected void onPostExecute(String resultado) {

                                if (resultado != null) {
                                    System.out.println(resultado);

                                    //Why god... why
                                }
                                else{
                                    System.out.println("else");
                                }
                            }
                        };
                        System.out.println("settext");
                        task2.execute();*/
                        //protected String doInBackground(Void... params) {

                        String resultado = new Server().connectToServer("http://galenoproject.sytes.net/data/?name=ECG&data="+line+"&save=yes", 15000);
                            //System.out.println(resultado);
                        //    return resultado;
                        //}
                    }
                }
                catch(Exception e){

                }
                return "a";
            }

            @Override
            protected void onPostExecute(String resultado) {

            }
        };
        task.execute();
    }

    public void showToast(String message){
        Context context = getApplicationContext();
        //CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //Boton Enviar
                case R.id.button2:
                    String message = editText.getText().toString()+"\n";
                    editText.setText("");
                    byte[] msgBuffer = message.getBytes();
                    try {
                        outStream.write(msgBuffer);
                        System.out.println("MENSAJE ENVIADO");
                    } catch (IOException e) {
                        String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
                        if (MAC.equals("00:00:00:00:00:00"))
                            msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
                        msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

                        showToast("Fatal Error"+ msg);
                    }
                    break;
            /*
            //Botón Comparar
            case R.id.button3:
                Intent i2 = new Intent(PrincipalActivity.this,ConsultarActivity.class);
                i2.putExtra("REGION",region);
                i2.putExtra("VISTA",2);
                startActivity(i2);
                break;
            //Botón Oferta
            case R.id.button4:
                //Intent i3 = new Intent(PrincipalActivity.this,ConsultarActivity.class);
                //i.putExtra("DATO",dato);
                //startActivity(i3);
                Context context = getApplicationContext();
                CharSequence text = "Aún no implementado!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
                */
            }
        }
}
