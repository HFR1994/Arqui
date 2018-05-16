package com.itesm.arqui.googlemaps.pojo.types;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class Primitive {
    private final static Set<Class<?>> DECIMALS;
    private final static Set<Class<?>> INTEGERS;
    private final static Set<Class<?>> BOOLEANS;
    private final static Set<Class<?>> DATES;

    static {
        Set<Class<?>> s = new HashSet<>();
        s.add(float.class);
        s.add(double.class);
        s.add(Float.class);
        s.add(Double.class);
        DECIMALS = s;
    }

    static {
        Set<Class<?>> s = new HashSet<>();
        s.add(short.class);
        s.add(int.class);
        s.add(long.class);
        s.add(Short.class);
        s.add(Integer.class);
        s.add(Long.class);
        INTEGERS = s;
    }

    static {
        Set<Class<?>> s = new HashSet<>();
        s.add(boolean.class);
        s.add(Boolean.class);
        BOOLEANS = s;
    }

    static {
        Set<Class<?>> s = new HashSet<>();
        s.add(java.util.Date.class);
        s.add(java.sql.Date.class);
        DATES = s;
    }
    
    public static String getType(Class<?> type){

        if(float.class.equals(type) || Float.class.equals(type)){
            return "decimal(11, 8)";
        }else if(double.class.equals(type) || double.class.equals(type)){
            return "decimal(11, 8)";
        }else if(short.class.equals(type) || Short.class.equals(type)){
            return "smallint";
        }else if(int.class.equals(type) || Integer.class.equals(type)){
            return "integer";
        }else if(long.class.equals(type) || Long.class.equals(type)){
            return "bigint";
        }else if(boolean.class.equals(type) || Boolean.class.equals(type)){
            return "boolean";
        }else if(java.util.Date.class.equals(type) || java.sql.Date.class.equals(type)){
            return "date";
        }else if(java.sql.Timestamp.class.equals(type)){
            return "datetime";
        }else{
            return "varchar(255)";
        }
    }

    public static String getDBName(String val){
        return StringUtils.lowerCase(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(val),
                '_'
        ));
    }


}
