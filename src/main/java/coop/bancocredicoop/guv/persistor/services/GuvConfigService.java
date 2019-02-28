package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.repositories.GuvConfigRepository;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GuvConfigService {

    @Autowired
    private GuvConfigRepository guvConfigRepository;

    public <T> T getProperty(GuvConfigEnum id, Class<T> clazz) {
        Assert.notNull(clazz, "clazz no puede ser null.");
        return this.guvConfigRepository.findById(id.name())
            .map(config -> this.parseValue(config.getValor(), clazz)).orElse(null);
    }

    private <T> T parseValue(String value, Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (T) value;
        }
        if (Integer.class.equals(clazz)) {
            return (T) Integer.valueOf(value);
        }
        if (Boolean.class.equals(clazz)) {
            return (T) Boolean.valueOf(value);
        }
        if (Path.class.isAssignableFrom(clazz)) {
            return (T) Paths.get(value);
        }
        if (BigDecimal.class.equals(clazz)) {
            return (T) BigDecimal.valueOf(Double.parseDouble(value));
        }
        //TODO ADD More Conversions
        throw new IllegalArgumentException("La conversion a " + clazz + " no es soportada!");
    }

}
