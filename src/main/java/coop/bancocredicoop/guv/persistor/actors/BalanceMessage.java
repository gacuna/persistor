package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import io.vavr.control.Either;

public class BalanceMessage {
    private Deposito deposito;

    public BalanceMessage() {}

    public BalanceMessage(Deposito deposito) {
        this.deposito = deposito;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    @Override
    public String toString() {
        return "BalanceMessage{" +
                "deposito=" + deposito +
                '}';
    }
}