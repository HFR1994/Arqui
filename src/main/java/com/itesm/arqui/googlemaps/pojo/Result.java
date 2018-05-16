package com.itesm.arqui.googlemaps.pojo;

import com.itesm.arqui.googlemaps.pojo.annotations.Password;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

public class Result {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer code;

    private String message;

    private HashMap data;

    public static final Integer OK = 200;

    public static final Integer CREATED = 201;

    public static final Integer NO_CONTENT = 204;

    public static final Integer NOT_MODIFIED = 304;

    public static final Integer BAD_REQUEST = 400;

    public static final Integer UNAUTHORIZED = 401;

    public static final Integer FORBIDDEN = 403;

    public static final Integer NOT_FOUND = 404;

    public static final Integer GONE = 410;

    public static final Integer INTERNAL_SERVER_ERROR = 500;

    public static final Integer SERVICE_UNAVAILABLE = 503;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap getData() {

        HashMap<String, Object> value = new HashMap<>();
        HashMap<String,String> response = new HashMap<>();

        response.put("timestamp", String.valueOf(Timestamp.from(Instant.now())));
        response.put("status", String.valueOf(getCode()));
        response.put("message", getMessage());

        value.put("_response",response);
        if (data == null || data.isEmpty()){
            value.put("payload", null);
        }else{
            value.put("payload", data);
        }

        return value;
    }

    public <T> HashMap getData(T klazz) {

        HashMap<String, Object> value = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();

        response.put("timestamp", String.valueOf(Timestamp.from(Instant.now())));
        response.put("status", String.valueOf(getCode()));
        response.put("message", getMessage());

        value.put("_response", response);

        HashMap<String, Object> datos = new HashMap<>();

        if (klazz != null) {
            for (Field field : klazz.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName().substring(0, 1).toLowerCase() + field.getName().substring(1);
                Object data;
                try {
                    data = PropertyUtils.getProperty(klazz, field.getName());

                    if(field.getAnnotation(Password.class) == null){
                        datos.put(name, data);
                    }else{
                        datos.put(name, "");
                    }


                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            try {
                String uuid = String.valueOf(PropertyUtils.getProperty(klazz, "uuid"));
                Boolean status = (Boolean) PropertyUtils.getProperty(klazz, "status");

                datos.put("uuid",uuid);
                datos.put("status", status);

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            value.put("payload", datos);
        } else {
            value.put("payload", null);
        }

        return value;
    }

    public void setData(HashMap data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
