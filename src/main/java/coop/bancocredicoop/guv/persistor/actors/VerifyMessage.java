package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import org.springframework.util.ObjectUtils;

public class VerifyMessage {
    private Long id;
    private String token;

    public VerifyMessage() {}

    public VerifyMessage(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "VerifyMessage {" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}