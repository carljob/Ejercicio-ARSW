package com.arsw.eda;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Dummy que simula un productor de eventos.
 */

@Component
public class TransferenciaEvent {

    private static final String STREAM = "banco.transferencias";

    private final StringRedisTemplate redis;
    private final Random random = new Random();

    public TransferenciaEvent(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Scheduled(fixedRate = 3000)
    public void generarEvento() {
        Map<String, String> evento = Map.of(
            "eventType",  "TransferenciaCreada",
            "eventId",    "evt-" + UUID.randomUUID().toString().substring(0, 8),
            "transferId", "tr-"  + UUID.randomUUID().toString().substring(0, 8),
            "from",       "cta-101",
            "to",         "cta-202",
            "amount",     String.valueOf(random.nextInt(9_900_000) + 100_000),
            "currency",   "COP"
        );

        var id = redis.opsForStream().add(MapRecord.create(STREAM, evento));
        System.out.println("[XADD] " + STREAM + " → " + id + " | " + evento);
    }
}
