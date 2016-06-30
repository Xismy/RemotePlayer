package com.example.iye19.remoteplayer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by iye19 on 06/04/2016.
 */

public class Reader extends AsyncTask <Object, Object , String>{
    private Player player;
    private BufferedReader input;

    /**
     * Tarea encargada de la recepción de mensajes enviados por el servidor.
     * @param player Activity de la clase Player donde se actualizará la UI.
     * @param bufferedReader Entrada del Socket conectado al servidor.
     */
    Reader(Player player, BufferedReader bufferedReader){
        this.player = player;
        input = bufferedReader;
    }

    /**
     * Recive respuestas del servidor y las manda a la hebra principal.
     * @param params No se utiliza.
     * @return Razón por la que finaliza el bucle.
     */
    @Override
    protected String doInBackground(Object[] params) {
        String type= "";
        ArrayList<String> response;
        boolean end = false;
        String reason = "Desconectado";

        while(!end) {
            response = new ArrayList<>();
            try {
                type = input.readLine();
                String line = input.readLine();
                if(type.equals("disconnect")) {
                    end = true;
                }


                while (!line.equals("")) {
                    response.add(line);
                    line = input.readLine();
                }
                publishProgress(new Object[]{type, response});
            }
            catch (SocketTimeoutException sotoex){
                end = true;
                reason = "La conexión ha expirado";
            }
            catch (SocketException | NullPointerException ceex){
                end = true;
                reason = "Error de conexión";
            }
            catch (IOException ioex) {}
        }

        return reason;
    }

    /**
     * Interpreta las respuestas del servidor y actualiza la UI.
     * @param values Respuesta del servidor.
     */
    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        String type = (String)((Object[])values)[0];
        ArrayList<String> response = (ArrayList)((Object[])values)[1];
        String[] response_array = new String[response.size()];
        response.toArray(response_array);
        switch (type){
            case "ls":
                player.ls(response_array);
                break;
            case "next":
            case "prev":
            case "open":
                if(response_array.length==2)
                    if(response_array[0].startsWith("playing")) {
                        int time = Integer.parseInt(response_array[0].substring(8));
                        ((SeekBar)player.findViewById(R.id.seekBar)).setMax(time);
                        ((TextView)player.findViewById(R.id.total_time)).setText("/"+milis2time_format(time));
                        Toast.makeText(player, response_array[1], Toast.LENGTH_LONG).show();
                        break;
                    }
                player.ls(response_array);
                break;
            case "time":
                int time = Integer.parseInt(response.get(0));
                ((SeekBar)player.findViewById(R.id.seekBar)).setProgress(time);
                ((TextView)player.findViewById(R.id.current_time)).setText(milis2time_format(time));
                break;
            case "volume_up":
            case "volume_down":
                String volume = response.get(0);
                Fragment fragment = player.getFragmentManager().findFragmentById(R.id.fragment);
                ((TextView)fragment.getView().findViewById(R.id.volume)).setText(volume);
                ((Volume)fragment).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(player.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        player.finish();
    }

    /**
     * Expresa un tiempo dado en ms en minutos y segundos en la forma: "00:00".
     * @param ms Tiempo dado en ms.
     * @return Tiempo dado en el formato indicado.
     */
    private String milis2time_format(int ms){
        int sec = ms/1000;
        int min = sec/60;
        sec %= 60;
        String time = String.format("%02d:%02d", min, sec);
        return time;
    }
}