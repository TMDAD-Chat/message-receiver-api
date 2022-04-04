package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.service.CounterService;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class CounterServiceImpl implements CounterService {

    private final Counter incomingMessagesAmount;
    private final Counter outcomingMessagesAmount;
    private final Counter incomingMessagesBytes;
    private final Counter outcomingMessagesBytes;

    public CounterServiceImpl(Counter incomingMessagesAmount, Counter outcomingMessagesAmount, Counter incomingMessagesBytes, Counter outcomingMessagesBytes) {
        this.incomingMessagesAmount = incomingMessagesAmount;
        this.outcomingMessagesAmount = outcomingMessagesAmount;
        this.incomingMessagesBytes = incomingMessagesBytes;
        this.outcomingMessagesBytes = outcomingMessagesBytes;
    }

    @Override
    public void newMessageReceived(String msg) {
        this.incomingMessagesAmount.count();
        this.incomingMessagesBytes.increment(msg.getBytes(StandardCharsets.UTF_8).length);
    }

    @Override
    public void newMessageSent(String msg) {
        this.outcomingMessagesAmount.count();
        this.outcomingMessagesBytes.increment(msg.getBytes(StandardCharsets.UTF_8).length);
    }
}
