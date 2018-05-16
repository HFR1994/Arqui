package com.itesm.arqui.googlemaps.dao;

import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.pojo.templates.DaoTemplate;
import com.itesm.arqui.googlemaps.pojo.types.Primitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PueblosDao extends DaoTemplate<Pueblos> {

    @Override
    public Class<Pueblos> domainClass() {
        return Pueblos.class;
    }

    @Autowired
    private JdbcTemplate jdbc;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Optional<Pueblos> getByName(String uuid){

        String sql = "Select * from `pueblos` where nombre = ?";
        try {
            BeanPropertyRowMapper<Pueblos> rowMapper = new BeanPropertyRowMapper<>(Pueblos.class);
            Pueblos miclase=jdbc.queryForObject(sql, rowMapper, uuid);
            logger.info("Obteniendo pueblos por nombre " + uuid);
            return Optional.of(miclase);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Map<String, Object>> getPueblos(){

        String sql = "SELECT * FROM pueblos";

        return jdbc.queryForList(sql);

    }

    public List<Map<String, Object>> getEndpoints(String inicio, String fin){

        String sql = "SELECT * FROM pueblos WHERE nombre != '"+inicio+"' OR nombre !='"+fin+"'";

        return jdbc.queryForList(sql);

    }
}