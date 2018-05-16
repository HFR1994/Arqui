package com.itesm.arqui.googlemaps.pojo.templates;

import com.itesm.arqui.googlemaps.pojo.Result;
import com.itesm.arqui.googlemaps.pojo.annotations.Password;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ServiceTemplate<T> {

    @Autowired
    private List<DaoTemplate<T>> daos;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

    private final Pattern VALID_PASSWORD_VALIDATION =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^!&+=])(?=\\S+$).{8,}$");

    private final Pattern VALID_DATE_VALIDATION =
            Pattern.compile("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract Class<T> domainClass();

    public DaoTemplate<T> getDao(){
        for(DaoTemplate<T> dao: daos){
            if(dao.domainClass().equals(domainClass())){
                return dao;
            }
        }
        return null;
    }


    public Optional<T> getByUuid(String uuid) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

        Optional<T> val = Objects.requireNonNull(getDao()).getByUuid(uuid);
        if(val.isPresent()){
            T clase = val.get();

            Set<Field> fields = findFields(clase.getClass(), Password.class);
            for(Field field : fields){
                field.setAccessible(true);
                PropertyUtils.setSimpleProperty(clase, field.getName(),null);
            }
        }

        return val;

    }

    private HashMap<String, HashMap<String, Object>> checkData(T klazz, boolean b) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {

        HashMap<String, HashMap<String, Object>> datos = new HashMap<>();
        Field[] fields = klazz.getClass().getDeclaredFields();
        Optional<T> var = Optional.empty();

        if(klazz.getClass() != null) {

            if(!b) {
                String uuid = PropertyUtils.getProperty(klazz, "uuid").toString();
                var = getByUuid(uuid);
            }

            for (Field field : fields) {
                field.setAccessible(true);

                HashMap<String, Object> val = new HashMap<>();
                String name = field.getName().substring(0, 1).toLowerCase() + field.getName().substring(1);

                Object value = PropertyUtils.getProperty(klazz, field.getName());

                HashSet<String> error = new HashSet<>();
                if (value == null && b) {
                    error.add(" no puede estar vacio");
                } else {
                    if(value != null) {

                        boolean check = true;

                        if(var.isPresent()) {
                            String current = String.valueOf(PropertyUtils.getProperty(var.get(), field.getName()));
                            if (current.equals(String.valueOf(value))) {
                                check = false;
                            }
                        }

                        if(check) {
                            for (Annotation annotation : field.getAnnotations()) {
                                Matcher matcher;
                                switch (annotation.annotationType().getSimpleName()) {
                                    case "Email":
                                        matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(value.toString());
                                        if (!matcher.find()) {
                                            error.add("ese no es un email valido");
                                        }
                                        break;
                                    case "Password":
                                        matcher = VALID_PASSWORD_VALIDATION.matcher(value.toString());
                                        if (!matcher.find()) {
                                            error.add("el password debe:\n\tTener 8 Letras\n\tUna letra mayúscula\n\tUna letra minúscula\n\tUn caracter especial\n\tNo debe tener espacios o saltos de página");
                                        }
                                        break;
                                    case "DateFormat":
                                        matcher = VALID_DATE_VALIDATION.matcher(value.toString());
                                        if (matcher.find()) {
                                            error.add("la fechas deben ir en format yyyy-mm-dd");
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        val.put("Cambio", check);
                    }else{
                        if(var.isPresent()){
                            value = PropertyUtils.getProperty(var.get(), field.getName());
                            val.put("Cambio", false);
                        }else{
                            throw new NullPointerException("No existe ese uuid");
                        }
                    }
                }

                if(field.getAnnotation(Password.class) == null){
                    val.put("Valor", value);
                }else{
                    val.put("Valor", "");
                }

                val.put("Error", error);
                datos.put(name, val);

            }
        }
        return datos;
    }

    private void addNew(HashMap<String, HashMap<String, Object>> val, T klazz) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String uuid = String.valueOf(PropertyUtils.getProperty(klazz, "uuid"));
        Boolean status = (Boolean) PropertyUtils.getProperty(klazz, "status");

        HashMap<String, Object> mapa = new HashMap<>();
        mapa.put("Valor", uuid);
        mapa.put("Error", new HashSet<String>());
        mapa.put("Cambio", true);
        val.put("uuid",mapa);

        mapa = new HashMap<>();
        mapa.put("Valor", status);
        mapa.put("Error", new HashSet<String>());
        mapa.put("Cambio", true);
        val.put("status",mapa);
    }


    public Result insert(T klazz) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Result result = new Result();

        if(klazz.getClass() != null) {
            HashMap<String, HashMap<String, Object>> datos;
            try {
                datos = checkData(klazz, true);
            } catch (NullPointerException | NoSuchFieldException e) {
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! No especificaste el uuid del registro");
                return result;
            }

            int cuenta=0;

            for (HashMap<String, Object> entry : datos.values()) {
                //noinspection unchecked
                HashSet<String> value = (HashSet<String>) entry.get("Error");
                cuenta+=value.size();
            }

            if(cuenta==0){
                Optional<T> val;
                try {
                    val = Objects.requireNonNull(getDao()).insert(klazz);
                }catch (SQLIntegrityConstraintViolationException e) {
                    result.setCode(Result.BAD_REQUEST);
                    result.setMessage(e.getMessage());
                    return result;
                }
                if(val.isPresent()){
                    addNew(datos,val.get());
                    result.setCode(Result.CREATED);
                    result.setMessage("¡Genial! Se agregaron exitosamente los datos");
                }else{
                    result.setCode(Result.INTERNAL_SERVER_ERROR);
                    result.setMessage("¡Ups! Hubo un error por favor contacta a tu administrador");
                }
            }else{
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! Hay uno o mas errores en tu formulario");
            }

            result.setData(datos);
        }else{
            result.setCode(Result.BAD_REQUEST);
            result.setMessage("¡Ups! El cuerpo del post no puede ir vacio");
        }

        return result;
    }

    public Result delete(T klazz){

        Result result = new Result();

        if(klazz.getClass() != null) {
            Optional<Boolean> object;
            try {
                object = Objects.requireNonNull(getDao()).delete(klazz);
            } catch (NullPointerException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! No especificaste el uuid del registro");
                return result;
            }

            if(object.isPresent()){
                if(object.get()){
                    result.setCode(Result.OK);
                    result.setMessage("¡Genial! Se dio de baja exitosamente el registro");
                }else{
                    result.setCode(Result.BAD_REQUEST);
                    result.setMessage("¡Ups! No existe ese uuid de registro");
                }
            } else {
                result.setCode(Result.INTERNAL_SERVER_ERROR);
                result.setMessage("¡Ups! Hubo un error por favor contacta a tu administrador");
            }

        }else{
            result.setCode(Result.BAD_REQUEST);
            result.setMessage("¡Ups! El cuerpo del post no puede ir vacio");
        }

        return result;
    }

    public Result update(T klazz){

        Result result = new Result();

        if(klazz.getClass() != null) {

            String uuid;

            try {
                uuid = PropertyUtils.getProperty(klazz, "uuid").toString();

                Optional<T> current = Objects.requireNonNull(getDao()).getByUuid(uuid);

                if (current.isPresent()) {

                    HashMap<String, HashMap<String, Object>> datos = checkData(klazz, false);

                    int cuenta = 0;

                    for (HashMap<String, Object> entry : datos.values()) {
                        //noinspection unchecked
                        HashSet<String> value = (HashSet<String>) entry.get("Error");
                        cuenta += value.size();
                    }

                    if (klazz.getClass().equals(current.get())) {
                        result.setCode(Result.NOT_MODIFIED);
                        result.setMessage("¡Alerta! No hubo cambios en el contenido");
                    } else if (cuenta == 0) {
                        Optional<T> val = Objects.requireNonNull(getDao()).update(klazz);
                        if (val.isPresent()) {
                            addNew(datos,val.get());
                            result.setCode(Result.OK);
                            result.setMessage("¡Genial! Se actualizaron exitosamente los datos");
                        } else {
                            result.setCode(Result.INTERNAL_SERVER_ERROR);
                            result.setMessage("¡Ups! Hubo un error por favor contacta a tu administrador");
                        }
                    } else {
                        result.setCode(Result.BAD_REQUEST);
                        result.setMessage("¡Ups! Hay uno o mas errores en tu formulario");
                    }

                    result.setData(datos);
                }else{
                    result.setCode(Result.BAD_REQUEST);
                    result.setMessage("¡Ups! No existe ese uuid de registro");
                }
            } catch (NullPointerException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! No especificaste el uuid del registro");
            } catch (NoSuchFieldException e) {
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! Uno de los campos no pertenece al registro");
            } catch (SQLIntegrityConstraintViolationException e) {
                result.setCode(Result.BAD_REQUEST);
                result.setMessage(e.getMessage());
                return result;
            }
        }else{
            result.setCode(Result.BAD_REQUEST);
            result.setMessage("¡Ups! El cuerpo del post no puede ir vacio");
        }

        return result;
    }


    private static Set<Field> findFields(Class<?> classs, Class<? extends Annotation> ann) {
        Set<Field> set = new HashSet<>();
        Class<?> c = classs;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

}
