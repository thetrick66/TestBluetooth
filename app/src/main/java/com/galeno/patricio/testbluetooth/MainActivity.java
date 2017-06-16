package com.galeno.patricio.testbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT=1;
    private ArrayAdapter<String> mArrayAdapter;
    private Button Buscar;
    private ListView listView;
    private BroadcastReceiver mReceiver;
    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//UUID.randomUUID();
    private String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Buscar = (Button) findViewById(R.id.button);
        Buscar.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.activity_list_item,android.R.id.text1 );
        listView = (ListView) findViewById(R.id.lista_dispositivos);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String o = (String)listView.getItemAtPosition(position);
                address = o.split("\n")[1];
                Intent i = new Intent(MainActivity.this,ConectedActivity.class);
                i.putExtra("MAC",address);
                i.putExtra("MY_UUID",MY_UUID);
                startActivity(i);
                //Toast.makeText(getBaseContext(),o.split("\n")[1],Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(mArrayAdapter);

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            showToast("Dispositivo incompatible con Bluetooth");
        }
        else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else{
                showToast("Bluetooth Prendido!");
                search();
            }
        }
    }

    protected void OnDestroy(){

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data){
        if (resultCode == RESULT_OK) {
            showToast("Bluetooth Prendido!");
            search();
        }
        else{
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            this.finish();
        }
    }

    public void search(){
        /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName()+" PAIRED" + "\n" + device.getAddress());
            }
        }*/
        // Create a BroadcastReceiver for ACTION_FOUND
        mArrayAdapter.clear();
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                /*if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        System.out.println("------------------------------------");
                        System.out.println(device);
                        System.out.println("------------------------------------");
                    }
                }*/
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    /*try {
                        System.out.println("COMENZANDO A EMPAREJAR");
                        Method m = device.getClass().getMethod("createBond", (Class[]) null);
                        Boolean returnValue = (Boolean) m.invoke(device, (Object[]) null);
                        if (returnValue){
                            System.out.println("EMPAREJAMIENTO FINALIZADO");
                        }
                        else{
                            System.out.println("EMPAREJAMIENTO NO REALIZADO");
                        }


                    } catch (Exception e) {
                        System.out.println("ERROR: "+e.getMessage());
                    }*/
                    /*
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        System.out.println("------------------------------------");
                        System.out.println("DISPOSITIVO: "+device+" |EMPAREJADO|");
                        System.out.println("------------------------------------");
                        BluetoothDevice device2 = mBluetoothAdapter.getRemoteDevice(device.getAddress());
                        BluetoothSocket socket = null;
                        try{
                            socket = device2.createRfcommSocketToServiceRecord(MY_UUID);
                        }
                        catch (Exception e){

                        }
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
                        try{
                            System.out.println("A DORMIR 1 s");
                            //Thread.sleep(500);
                            System.out.println("DESPIERTO");
                            socket.connect();
                            Thread.sleep(1000);
                            System.out.println("Conectado");
                        }
                        catch (Exception e){
                            System.out.println("ERROR AL CONECTAR: "+e.getMessage());
                        }
                        OutputStream outStream = null;
                        try {
                            outStream = socket.getOutputStream();
                        } catch (IOException e) {
                            System.out.println("ERROR AL OBTENER OUTPUTSTREAM: "+e.getMessage());
                        }
                        String message = "Hello from Android.\n";
                        byte[] msgBuffer = message.getBytes();
                        try {
                            outStream.write(msgBuffer);
                        } catch (IOException e) {
                            System.out.println("ERROR AL ESCRIBIR: "+e.getMessage());
                        }
                    }
                    */                    // Add the name and address to an array adapter to show in a ListView
                    if (mArrayAdapter.getCount()==0){
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                    else{
                        int count=0;
                        for(int i=0; i<mArrayAdapter.getCount();i++){
                            if(!mArrayAdapter.getItem(i).equals(device.getName() + "\n" + device.getAddress())){
                                count++;
                                break;
                            }
                        }
                        if (count==mArrayAdapter.getCount()){
                            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                    }
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(mReceiver, intentFilter);// Don't forget to unregister during onDestroy

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
            //Botón Buscar
            case R.id.button:
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
                showToast("A buscar!");
                search();
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
