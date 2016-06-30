package com.example.iye19.remoteplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by iye19 on 06/04/2016.
 */
public class Connect extends AsyncTask<String, Object, Socket> {
    Player player;

    /**
     * Tarea encargada de establecer la conexión con el servidor.
     * @param player Activity que llama a la tarea.
     */
    Connect(Player player){
        this.player = player;
    }

    /**
     * Crea un socket conectado al servidor.
     * @param params Ip del servidor.
     * @return Socket creado.
     */
    @Override
    protected Socket doInBackground(String... params) {
        Socket s = null;

        try {
           s = new Socket(params[0], 1234);
        }
        catch (Exception ex){}
        return s;
    }

    /**
     * Actualiza el socket en la hebra principal.
     * @param socket
     */
    @Override
    protected void onPostExecute(Socket socket) {
        super.onPostExecute(socket);
        player.setSocket(socket);
    }
}
