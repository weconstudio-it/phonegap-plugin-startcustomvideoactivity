package it.weconstudio.utilities;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

/**
 * Classe Business per gestione JSON su singolo livello
 */
public class JsonParser {

    /**
     * Mappa degli attributi dell'oggetto
     */
    public HashMap<String, String> mappa;

    private Uri jsonRemoteUrl;
    private Boolean dataAvailable = false;
    protected List<ParserEventsListener> listenersList = null;
    protected Connection conn;

    public JsonParser(){
        mappa = new HashMap<String, String>();
        listenersList = new ArrayList<ParserEventsListener>();

        jsonRemoteUrl = Uri.EMPTY;
    }

    /**
     * Sottoscrizione evento di aggiornamento dati
     * @param listener
     */
    public void addListener(ParserEventsListener listener) {
        if (listener != null && !listenersList.contains(listener))
            this.listenersList.add(listener);
    }

    public void reset (Boolean resetListeners) {
        this.mappa.clear();
        this.dataAvailable = false;

        if (resetListeners)
            listenersList.clear();
    }

    /**
     * Interruzione sottoscrizione evento di aggiornamento dati
     * @param listener
     */
    public void removeListener(ParserEventsListener listener) {
        if (this.listenersList.contains(listener))
            this.listenersList.remove(listener);

    }

    /**
     * Imposta l'URL del JSON da elaborare
     * @param jsonRemoteUrl
     */
    public void setJsonRemoteUrl(Uri jsonRemoteUrl){
        this.jsonRemoteUrl = jsonRemoteUrl;
    }

    /**
     * Aggiorna i propri attributi leggendoli da json
     * @return
     */
    public int update(){

        conn = new Connection();
        conn.execute(new Object[]{this.jsonRemoteUrl, this});

        return 0;
    }

    public static boolean release() {
        //return JsonParser.conn.cancel(true);
        return true;
    }

    /**
     * Indica se ci sono dati utilizzabili
     * @return
     */
    public Boolean isDataAvailable() {
        return this.dataAvailable;
    }

    private class Connection extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            getJson(objects);

            return null;
        }

        private void getJson(Object[] objects) {

            try {
                Uri jsonRemoteUrl = (Uri)objects[0];

                //instantiates httpClient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(jsonRemoteUrl.toString());

                httpPost.setHeader("Accept", "application/json");

                //Handles what is returned from the page
                ResponseHandler responseHandler = new BasicResponseHandler();

                Object response = httpClient.execute(httpPost, responseHandler);

                JSONObject jsonReceived = new JSONObject(response.toString());

                if (jsonReceived.has("root")) {
                    JSONObject jResponse = jsonReceived.getJSONObject("root");

                    Iterator i = jResponse.keys();

                    while (i.hasNext()) {

                        String key = (String)i.next();

                        String value = jResponse.getString(key);

                        mappa.put(key.toString(), value.toString());

                    }

                    dataAvailable = true;

                    if (!this.isCancelled() && !listenersList.isEmpty()) {
                        for (ParserEventsListener p : listenersList) {
                            p.dataAvailable((JsonParser)objects[1]);
                        }
                    }
                } else {
                    // azz
                }
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), R.string.msgErrorGettingResults, Toast.LENGTH_SHORT).show();
                int i = 0;
                i++;
            }

        }
    }
}
