package com.example.iye19.remoteplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;


public class Player extends AppCompatActivity{
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String ip = getIntent().getStringExtra("ip");
        Connect connect = new Connect(this);
        connect.execute(ip);

        ((SeekBar)findViewById(R.id.seekBar)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            output.println("set_time");
                            output.println(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    /**
     * Cierra los stream de entrada y salida y el socket.
     */
    @Override
    protected void onStop(){
        super.onStop();
        output.close();
        try {
            input.close();
        }
        catch(IOException ex){}
        try {
            socket.close();
        }
        catch(IOException ex){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upper_dir) {
            output.println("open");
            output.println("-1");
            return true;
        }
        else if(id == R.id.disconnect){
            output.println("disconnect");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                output.println("volume_up");
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                output.println("volume_down");
                break;
            case KeyEvent.KEYCODE_BACK:
                output.println("open");
                output.println("-1");
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    /**
     * Actualiza el ListView que contiene los nombres de archivos y directorios del directorio actual.
     * @param ls Lista de archivos y directorios.
     */
    public void ls(String[] ls){
        String[] files = new String[ls.length];
        boolean[] isDir = new boolean[ls.length];
        for(int i=0; i<ls.length; i++){
            isDir[i] = ls[i].startsWith("d");
            files[i] = ls[i].substring(1);
        }
        final ListView listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file, files));
        listView.setAdapter(new ItemAdapter(this, files, isDir));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                output.println("open");
                output.println(position);
            }
        });
    }

    /**
     * Establece el socket que conecta con el servidor y extrae los stream de entrada y salida.
     * @param socket Socket que conecta con el servidor.
     */
    public void setSocket(Socket socket){
        try {
            this.socket = socket;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            Reader reader = new Reader(this, input);
            reader.execute();
            output.println("ls");
        }
        catch (Exception ex){}
    }

    /**
     * Envía una orden al servidor. Este método debe asociarse a un click de un botón. La orden depende del ID del botón.
     * @param v Botón clickado.
     */
    public void sendCmd(View v){
        String cmd="";
        switch (v.getId()){
            case R.id.button_play_pause:
                cmd = "play";
                break;
            case R.id.button_next:
                cmd = "next";
                break;
            case R.id.button_previous:
                cmd = "prev";
                break;
            default:
                break;
        }
        output.println(cmd);
    }
}