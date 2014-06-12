package it.weconstudio.utilities;

public interface ParserEventsListener {
    public void dataAvailable(JsonParser obj);
    public void transitionDetected(JsonParser obj);
}