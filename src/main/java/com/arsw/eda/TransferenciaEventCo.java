package com.arsw.eda;

import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Consumidor del stream banco.transferencias.
 */
@Component
public class TransferenciaEventCo {

    private static final String STREAM = "banco.transferencias";
    private static final String GRUPO  = "fraude-group";
    private static final String CONSUMIDOR = "consumidor-1";

    private final StringRedisTemplate redis;

    public TransferenciaEventCo(StringRedisTemplate redis) {
        this.redis = redis;
        try {
            redis.opsForStream().createGroup(STREAM, ReadOffset.from("0"), GRUPO);
        } catch (Exception e) {
        }
    }

    @Scheduled(fixedRate = 3000)
    public void consumirEvento() {
        List<MapRecord<String, Object, Object>> mensajes = redis.opsForStream().read(
                Consumer.from(GRUPO, CONSUMIDOR),
                StreamReadOptions.empty().count(1),
                StreamOffset.create(STREAM, ReadOffset.lastConsumed())
        );

        if (mensajes == null || mensajes.isEmpty()) {
            System.out.println("[CONSUMIDOR] Sin eventos nuevos");
            return;
        }

        for (MapRecord<String, Object, Object> mensaje : mensajes) {
            System.out.println("[XREADGROUP] id=" + mensaje.getId() + " | " + mensaje.getValue());
            redis.opsForStream().acknowledge(STREAM, GRUPO, mensaje.getId());
            System.out.println("[XACK] confirmado → " + mensaje.getId());
        }
    }
}