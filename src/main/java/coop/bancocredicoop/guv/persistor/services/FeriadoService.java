package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.Feriado;
import coop.bancocredicoop.guv.persistor.repositories.FeriadoRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class FeriadoService {

    @Autowired
    private FeriadoRepository feriadoRepository;


    /**
     * Calcula el siguiente dia habil, para una fecha dada.
     *
     * @param fecha             la fecha a partir de la cual se busca el dia habil.
     * @param cantidadDiasSumar la cantidad de dias a sumar.
     * @return retorna la primera fecha encontrada que sea un dia habil (es decir, que no sea fin de semana o un feriado nacional
     */
    public Date calcularProximoDiaHabil(Date fecha, int cantidadDiasSumar) {
        return this.calcularProximoDiaHabil(fecha, cantidadDiasSumar, Boolean.FALSE);
    }

    /**
     * Calcula el siguiente dia habil, para una fecha dada, hacia adelante o hacia atras, truncando la hora.
     *
     * @param fecha             la fecha a partir de la cual se busca el dia habil.
     * @param cantidadDiasSumar la cantidad de dias a sumar.
     * @param haciaAtras        flag para definir si se calcula el proximo dia habil a futuro o pasado.
     * @return retorna la primera fecha encontrada que sea un dia habil (es decir, que no sea fin de semana o un feriado nacional
     */
    public Date calcularProximoDiaHabil(Date fecha, int cantidadDiasSumar, Boolean haciaAtras) {
        return this.calcularProximoDiaHabil(fecha, cantidadDiasSumar, haciaAtras, Boolean.TRUE);
    }

    /**
     * Calcula el siguiente dia habil, para una fecha dada, hacia adelante o hacia atras con opcion de truncar la hora.
     *
     * @param fecha             la fecha a partir de la cual se busca el dia habil.
     * @param cantidadDiasSumar la cantidad de dias a sumar.
     * @param haciaAtras        flag para definir si se calcula el proximo dia habil a futuro o pasado.
     * @param truncarHora       flag para definir si se trunca la hora o no.
     * @return retorna la primera fecha encontrada que sea un dia habil (es decir, que no sea fin de semana o un feriado nacional
     */
    public Date calcularProximoDiaHabil(Date fecha, int cantidadDiasSumar, Boolean haciaAtras, Boolean truncarHora) {
        Date fechaCalculada = truncarHora ? DateUtils.truncate(fecha, Calendar.DAY_OF_MONTH) : fecha;
        int dias = haciaAtras ? -1 * cantidadDiasSumar : cantidadDiasSumar;
        fechaCalculada = DateUtils.addDays(fechaCalculada, dias);
        return this.calcularProximoDiaHabil(fechaCalculada, haciaAtras);
    }

    /**
     * Calcula el siguiente dia habil, para una fecha dada, hacia adelante o hacia atras, incrementando de a 1 dia hasta encontar el dia habil.
     *
     * @param fecha      la fecha a partir de la cual se busca el dia habil.
     * @param haciaAtras flag para definir si se calcula el proximo dia habil a futuro o pasado.
     * @return retorna la primera fecha encontrada que sea un dia habil (es decir, que no sea fin de semana o un feriado nacional
     */
    public Date calcularProximoDiaHabil(Date fecha, Boolean haciaAtras) {
        Feriado feriado = this.feriadoRepository.findByFechaAndNacionalTrue(fecha);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        int dia = calendar.get(Calendar.DAY_OF_WEEK);
        if ((feriado == null) && (Calendar.SATURDAY != dia) && (Calendar.SUNDAY != dia)) {
            return fecha;
        }
        int cantidadDiasSumar = haciaAtras ? -1 : 1;
        return this.calcularProximoDiaHabil(DateUtils.addDays(fecha, cantidadDiasSumar), haciaAtras);
    }

}
