package coop.bancocredicoop.guv.persistor.exceptions;

/**
 * Excepcion creada para detectar los mensajes que no deben ser enviados al backend de guv.
 */
public class MessageNotUpdateableException extends Exception {
    private String message;

    public MessageNotUpdateableException(String message) {
        this.message = message;
    }
}
