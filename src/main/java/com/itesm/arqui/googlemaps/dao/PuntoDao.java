package com.itesm.arqui.googlemaps.dao;

import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Punto;
import com.itesm.arqui.googlemaps.pojo.templates.DaoTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PuntoDao extends DaoTemplate<Punto> {

    @Override
    public Class<Punto> domainClass() {
        return Punto.class;
    }

    @Autowired
    private JdbcTemplate jdbc;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Optional<Punto> getByPueblos(Pueblos p1, Pueblos p2){

        String sql = "Select * from `puntos` where (inicio=? AND destino=?) OR (destino=? AND inicio=?)";
        try {
            BeanPropertyRowMapper<Punto> rowMapper = new BeanPropertyRowMapper<>(Punto.class);
            Punto miclase=jdbc.queryForObject(sql, rowMapper, p1.getNombre(), p2.getNombre(), p2.getNombre(), p1.getNombre());
            logger.info("Obteniendo un punto por nombres "+p1.getNombre()+" y "+p2.getNombre());
            return Optional.of(miclase);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}