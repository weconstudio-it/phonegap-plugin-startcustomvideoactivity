package it.weconstudio.utilities;

import android.net.Uri;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Specializzazione classe di gestione JSON per formato RSI.ch Sport
 */
public class Flags extends JsonParser implements ParserEventsListener {

    private static Flags instance = null;

    private Uri jsonUrl;
    private Flags me;
    private Boolean streamingVideo = null;
    private Timer timer = null;

    /**
     * Costruttore protetto, pattern Singleton
     * @param jsonUrl
     */
    protected Flags(Uri jsonUrl) {
        // Exists only to defeat instantiation.

        me = this;
        this.jsonUrl = jsonUrl;
        this.setJsonRemoteUrl(jsonUrl);
        this.addListener(this);
        this.update();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                me.update();
            }
        }, 0, 10000);
    }

    /**
     * Ritorna un'istanza dell'oggetto di lettura flag
     * @param jsonUrl
     * @return
     */
    public static Flags getInstance(Uri jsonUrl) {
        if(instance == null) {
            instance = new Flags(jsonUrl);
        }
        return instance;
    }

    public Boolean get_streaming_video(){
        return this.mappa.get("streaming_video").toString().equals("1");
    }

    public String get_nome_web(int n){
        return this.mappa.get(String.format("nome_web%d", n)).toString();
    }
    public String get_nome_web1() {
        return get_nome_web(1);
    }
    public String get_nome_web2() {
        return get_nome_web(2);
    }
    public String get_nome_web3() {
        return get_nome_web(3);
    }
    public String get_nome_web4() {
        return get_nome_web(4);
    }

    public Uri get_link_web(int n){
        return Uri.parse((String)this.mappa.get(String.format("link_web%d", n).toString()));
    }

    public Uri get_link_web1() {
        return get_link_web(1);
    }
    public Uri get_link_web2() {
        return get_link_web(2);
    }
    public Uri get_link_web3() {
        return get_link_web(3);
    }
    public Uri get_link_web4() {
        return get_link_web(4);
    }

    public String get_nome_flusso(int n){
        return (String)this.mappa.get(String.format("nome_flusso%d", n).toString());
    }
    public String get_nome_flusso1(){
        return get_nome_flusso(1);
    }
    public String get_nome_flusso2(){
        return get_nome_flusso(2);
    }
    public String get_nome_flusso3(){
        return get_nome_flusso(3);
    }
    public String get_nome_flusso4(){
        return get_nome_flusso(4);
    }
    public String get_nome_flusso5(){
        return get_nome_flusso(5);
    }

    public Uri get_link_flussoandroid(int n){
        return Uri.parse(this.mappa.get(String.format("link_flusso%dandroid", n).toString()));
    }
    public Uri get_link_flusso1android(){
        return get_link_flussoandroid(1);
    }
    public Uri get_link_flusso2android(){
        return get_link_flussoandroid(2);
    }
    public Uri get_link_flusso3android(){
        return get_link_flussoandroid(3);
    }
    public Uri get_link_flusso4android(){
        return get_link_flussoandroid(4);
    }
    public Uri get_link_flusso5android(){
        return get_link_flussoandroid(5);
    }

    public Uri get_promoandroid(){
        return Uri.parse((String)this.mappa.get("promoandroid"));
    }
    public Uri get_promo_web(){
        return Uri.parse((String)this.mappa.get("promo_web"));
    }
    public Uri get_img_streaming_video_offline(){
        return Uri.parse(Flags.serverName() + "img/" + (String)this.mappa.get("img_streaming_video_offline"));
    }
    public Uri get_link_rsi_browser(){
        return Uri.parse((String)this.mappa.get("link_rsi_browser"));
    }

    public WebViewStatus get_webview_attivo(int number) {
        String ret = this.mappa.get(String.format("webview%d_attivo", number)).toString().toUpperCase();

        if(ret.equals("TRUE")){
            return WebViewStatus.TRUE;
        }else if(ret.equals("ONCLICK")){
            return WebViewStatus.ONCLICK;
        }else{
            return WebViewStatus.FALSE;
        }
    }
    public WebViewStatus get_webview1_attivo(){
        return this.get_webview_attivo(1);
    }
    public WebViewStatus get_webview2_attivo(){
        return this.get_webview_attivo(2);
    }
    public WebViewStatus get_webview3_attivo(){
        return this.get_webview_attivo(3);
    }
    public WebViewStatus get_webview4_attivo(){
        return this.get_webview_attivo(4);
    }

    /**
     * Ritorna l'host a cui ci si collega per il recupero dei dati
     * @return
     */
    public static String serverName() {
        return "http://f1.rsi.ch/RSI_F1/";
        //return "http://192.168.0.136/";
    }

    @Override
    public void dataAvailable(JsonParser obj) {
        if (this.get_streaming_video() != streamingVideo) {
            this.transitionDetected(this);

            streamingVideo = this.get_streaming_video();
        }
    }

    @Override
    public void transitionDetected(JsonParser obj) {
        for (ParserEventsListener p : this.listenersList) {
            if (!p.equals(this))
                p.transitionDetected(this);
        }
    }

    public static boolean release() {
        if (instance != null) {
            instance.timer.cancel();
            //instance.conn.cancel(true);
            instance = null;
        }

        return true;
        //return super.release();
    }
}