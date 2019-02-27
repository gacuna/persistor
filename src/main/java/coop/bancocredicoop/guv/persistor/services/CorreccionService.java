package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CorreccionService {

    @Value(value = "${guv.web.url}")
    private String guvUrl;

    @Autowired
    private ChequeRepository repository;

    /**
     *
     * @param correccion
     * @return
     */
    public Future<Cheque> update(Function2<Correccion, Cheque, Cheque> f, Correccion correccion) {
        //TODO Mejorar con un pattern matching y loguear si no hay un cheque con ese id
        return Future.of(() -> this.repository.findById(correccion.getId()))
              .map(cheque -> f.apply(correccion, cheque.get()))
              .map(cheque -> this.repository.save(cheque));
    }

    public Function2<Correccion, Cheque, Cheque> updateImporte = (correccion, cheque) -> {
        cheque.setImporte(correccion.getImporte());
        return cheque;
    };

    public Function2<Correccion, Cheque, Cheque> updateCmc7 = (correccion, cheque) -> {
        cheque.setCmc7(correccion.getCmc7());
        return cheque;
    };

    public Function2<Correccion, Cheque, Cheque> updateFecha = (correccion, cheque) -> {
        cheque.setFechaDiferida(correccion.getFechaDiferida());
        return cheque;
    };

    public Function2<Correccion, Cheque, Cheque> updateCuit = (correccion, cheque) -> {
        cheque.setCuit(correccion.getCuit());
        return cheque;
    };

    /**
     * Envia un mensaje de verificacion del estado del deposito al backend de GUV, utilizando el verbo HTTP POST.
     *
     * @param correccion
     * @return
     */
    public Try<Integer> verificacionDepositoBackgroundPost(Correccion correccion) {
        return Try.of(() -> {
            try(CloseableHttpClient client = HttpClients.createDefault()){
                HttpPost post = new HttpPost(guvUrl + "camenviada/correccion/verificar_deposito");
                String json = "{\"id\": " + correccion.getId() + "}";
                StringEntity body = new StringEntity(json);
                post.setEntity(body);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setHeader("GUV-AUTH-TOKEN", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVNFUiIsIm91IjoiR1JHUjAwMDEiLCJQZXJtaXNvcyI6WyJHVVZDYW1SZWMuQ09OUyIsIkdVVk1vblJlYy5DT05TIiwiR1VWQ2FtRW52LkNPTlMiLCJHVVZQcm9FbnYuQ09OUyIsIkdVVk1vbkVudi5DT05TIiwiR1VWTW9uRW52LkFDVCIsIkdVVkNvcnJDaGVxLkNPTlMiLCJHVVZDb3JySW1wLkFDVCIsIkdVVkNvcnJDTUMuQUNUIiwiR1VWQ29yckZlYy5BQ1QiLCJHVVZDb3JyQ3VpdC5BQ1QiLCJHVVZCYWxhbmMuQ09OUyIsIkdVVkNvblN1cC5BQ1QiLCJHVVZDb25TdXAuQ09OUyIsIkdVVkJhbGFuYy5BQ1QiLCJHVVZDbGFGaXMuQ09OUyIsIkdVVkNsYUZpcy5BQ1QiLCJHVVZDaEVsaXlEZXIuQ09OUyIsIkdVVkRpZmVyZW5jaWEuQ09OUyIsIkdVVkFyckN0YS5DT05TIiwiR1VWQXJyQ3RhLkFDVCIsIkdVVlZlcmlmaWNhLkNPTlMiLCJHVVZWZXJGaXIuQ09OUyIsIkdVVlZlckZpci5BQ1QiLCJHVVZWZXJUZWMuQ09OUyIsIkdVVlZlclRlYy5BQ1QiLCJHVVZWZXJOb09yZC5DT05TIiwiR1VWVmVyTm9PcmQuQUNUIiwiR1VWVmVyU3VwLkNPTlMiLCJHVVZWZXJTdXAuQUNUIiwiR1VWQ29uZlJlY2guQ09OUyIsIkdVVkNvbmZSZWNoLkFDVCIsIkdVVk1vblZlcmlmLkNPTlMiLCJHVVZBZFJlY2guQ09OUyIsIkdVVlJjaEVudi5DT05TIiwiR1VWUmNoRW52LkFDVCIsIkdVVlJjaFJlYy5DT05TIiwiR1VWUmNoUmVjLkFDVCIsIkdVVlJjaENqZUludC5DT05TIiwiR1VWUmNoQ2plSW50LkFDVCIsIkdVVkRldFBwaW8uQ09OUyIsIkdVVkRldDNyby5DT05TIiwiR1VWUmVzUmVjaERlcC5DT05TIiwiR1VWUmVjaFJlZy5DT05TIiwiR1VWUmVjaFJlZy5BQ1QiLCJHVVZQZW5SZXAuQ09OUyIsIkdVVlBlblJlcC5BQ1QiLCJHVVZDaHNPTlAuQ09OUyIsIkdVVlJlY2hGQy5DT05TIiwiR1VWUmVjRkNwcGlvLkNPTlMiLCJHVVZGQ3BwaW9JbmcuQUNUIiwiR1VWRkNwcGlvUGVuLkNPTlMiLCJHVVZGQ3BwaW9QZW4uQUNUIiwiR1VWRkNwcGlvSGlzLkNPTlMiLCJHVVZSZWNGQzNyby5DT05TIiwiR1VWRkMzcm9QZW4uQ09OUyIsIkdVVkZDM3JvUGVuLkFDVCIsIkdVVkZDM3JvSGlzLkNPTlMiLCJHVVZSZWNsYW0uQ09OUyIsIkdVVlJlY2xhUHBpby5DT05TIiwiR1VWUkNwcGlvSW5nLkFDVCIsIkdVVlJDcHBpb1Blbi5DT05TIiwiR1VWUkNwcGlvUGVuLkFDVCIsIkdVVlJDcHBpb0hpcy5DT05TIiwiR1VWUmVjbGEzcm8uQ09OUyIsIkdVVlJDM3JvUGVuLkNPTlMiLCJHVVZSQzNyb1Blbi5BQ1QiLCJHVVZSQzNyb0hpcy5DT05TIiwiR1VWSW1SY2hUcnUuQ09OUyIsIkdVVk1JUmNoVHJ1LkNPTlMiLCJHVVZWZVJjaFRydS5DT05TIiwiR1VWVmVSY2hUcnUuQUNUIiwiR1VWUmVwb3J0LkNPTlMiLCJHVVZSZUVzUmEuQ09OUyIsIkdVVlJlQ29Jbi5DT05TIiwiR1VWUmVIaUNoLkNPTlMiLCJHVVZSZUNpRmkuQ09OUyIsIkdVVlJlTWFDaC5DT05TIiwiR1VWUmVQZUNvLkNPTlMiLCJHVVZSZU9wQ28uQ09OUyIsIkdVVlJlT3BCYS5DT05TIiwiR1VWQ29uZmlnLkNPTlMiLCJHVVZUYUJjb3MuQ09OUyIsIkdVVlRhQmNvcy5BQ1QiLCJHVVZUYUZpbGlhbC5DT05TIiwiR1VWVGFGaWxpYWwuQUNUIiwiR1VWVGFGZXJpLkNPTlMiLCJHVVZUYUZlcmkuQUNUIiwiR1VWVGFNb3RSZWNoLkNPTlMiLCJHVVZUYU1vdFJlY2guQUNUIiwiR1VWVGFDb2xpLkNPTlMiLCJHVVZUYUNvbGkuQUNUIiwiR1VWVGFMb2NhbGkuQ09OUyIsIkdVVlRhTG9jYWxpLkFDVCIsIkdVVlRhQ29taXMuQ09OUyIsIkdVVlRhQ29taXMuQUNUIiwiR1VWVGFNb25lLkNPTlMiLCJHVVZUYU1vbmUuQUNUIiwiR1VWVGFQcm92LkNPTlMiLCJHVVZUYVByb3YuQUNUIiwiR1VWVGFDb25mLkNPTlMiLCJHVVZUYUNvbmYuQUNUIiwiR1VWQ29uc3VsdGEuQ09OUyIsIkdVVk1hcmNhRmlzLkFDVCJdLCJHdXZWZXJzaW9uIjoiREVTQ09OT0NJREEiLCJpc3MiOiJCQ0NMLWd1diIsImlhdCI6MTU1MTE5NDQ0NiwiZXhwIjoxNTUxMzE0NDQ2fQ.G57kBpA2mmu9FahY7JH9kNcVnkRM4GJNRQ9kVFLmT9yjcrRjSAoreAEfolgFKkCYyYQW1He6CL2bb6V3xesSKg");
                CloseableHttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
                response.close();
                return status;
            }
        });
    }

}