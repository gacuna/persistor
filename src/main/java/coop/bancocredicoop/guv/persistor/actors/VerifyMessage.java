package coop.bancocredicoop.guv.persistor.actors;

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
                ", token='*****" + '\'' +
                '}';
    }
}