package es.unizar.tmdad.service;

public interface CounterService {

    void newMessageReceived(String msg);
    void newMessageSent(String msg);
}
