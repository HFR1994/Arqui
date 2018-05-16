package com.itesm.arqui.googlemaps.pojo.templates;

import com.itesm.arqui.googlemaps.pojo.types.Primitive;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


public abstract class DaoTemplate<T>{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract Class<T> domainClass();


    public Optional<T> getByUuid(String uuid){

        String value = Primitive.getDBName(domainClass().getSimpleName());
        Class<T> clase = domainClass();

        String sql = "Select * from `"+value+"` where uuid = ?";
        try {
            BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clase);
            T miclase=jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.info("Obteniendo "+value+" por uuid " + uuid);
            return Optional.of(miclase);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public Optional<T> insert(T klazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLIntegrityConstraintViolationException {

        String value = Primitive.getDBName(klazz.getClass().getSimpleName());

        String smt = "INSERT INTO `"+value+"`";
        String newUuid = UUID.randomUUID().toString();
        String col = "(`uuid`,";
        String val = " VALUES (?,";
        ArrayList<Object> data = new ArrayList<>();
        Field[] fields = klazz.getClass().getDeclaredFields();
        data.add(newUuid);

        for (Field field : fields) {
            field.setAccessible(true);

            String name= field.getName().substring(0, 1).toLowerCase() + field.getName().substring(1);

            col = col.concat("`"+name+"`,");
            val = val.concat("?,");

            data.add(PropertyUtils.getProperty(klazz, name));
        }

        col = col.concat("`status`,`created_at`,`modified_at`)");
        val = val.concat("?,?,?)");

        data.add(true);
        data.add(Timestamp.from(Instant.now()));
        data.add(Timestamp.from(Instant.now()));

        smt=smt.concat(col+val);

        try {
            int count = jdbcTemplate.update(smt,data.toArray());
            logger.info("Insertando "+ value);
            if (count == 0){
                return Optional.empty();
            }else{
                return getByUuid(newUuid);
            }
        } catch (DataIntegrityViolationException f){

            System.out.println(String.valueOf(f.getCause()));

            if(String.valueOf(f.getCause()).contains("CONSTRAINT")){
                String text = String.valueOf(f.getCause());
                int type = text.indexOf("CONSTRAINT");
                text = text.substring(type+12);
                text = text.substring(0, text.indexOf("`"));
                switch (text){
                    case "user1_uuid_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe el usuario 1");
                    case "user2_uuid_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe el usuario 2");
                    case "chat_user_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe ese usuario");
                    case "chat_match_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe ese match");
                    case "house_place_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe esa casa");
                    case "place_user_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe ese usuario");
                    case "place_specification_place_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe esa casa");
                    case "user_images_user_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe ese usuario");
                    case "agenda_match_fk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! No existe ese match");
                }

            }else{
                String text = String.valueOf(f.getCause());
                int type = text.indexOf("for key");
                text = text.substring(type+9,text.length()-1);
                switch (text){
                    case "match_unique_key_pk":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! Ya existe este match");
                    case "user__email":
                        throw new SQLIntegrityConstraintViolationException("¡Ups! Ese correo ya esta en uso");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Boolean> delete(T klazz) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String uuid = PropertyUtils.getProperty(klazz, "uuid").toString();

        String value = Primitive.getDBName(klazz.getClass().getSimpleName());

        try {
            int count = jdbcTemplate.update("UPDATE `" + value + "` SET `status`=0 WHERE `uuid`=?", uuid);
            logger.info("Borrando "+value);
            if (count == 0){
                return Optional.of(false);
            }else{
                return Optional.of(true);
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<T> update(T klazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLIntegrityConstraintViolationException {

        String value = Primitive.getDBName(klazz.getClass().getSimpleName());
        String uuid = PropertyUtils.getProperty(klazz, "uuid").toString();
        String smt = "UPDATE `"+value+"`";
        String col = " SET";
        ArrayList<Object> data = new ArrayList<>();
        Field[] fields = klazz.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            String name= field.getName().substring(0, 1).toLowerCase() + field.getName().substring(1);

            if(PropertyUtils.getProperty(klazz, name) != null){
                col = col.concat(" `"+name+"` = ?,");
                data.add(PropertyUtils.getProperty(klazz, name));
            }

        }

        col = col.concat(" `modified_at` = ? WHERE `uuid`=?");
        data.add(Timestamp.from(Instant.now()));
        data.add(uuid);

        smt=smt.concat(col);

        try {
            int count = jdbcTemplate.update(smt,data.toArray());
            logger.info("Estoy modificando "+ value);
            if (count == 0){
                return Optional.empty();
            }else{
                return getByUuid(uuid);
            }
        } catch (Exception e){
            logger.error(String.valueOf(e.getCause()));
            if(String.valueOf(e.getCause()).contains("Duplicate")){
                throw new SQLIntegrityConstraintViolationException();
            }
        }
        return Optional.empty();
    }

}
