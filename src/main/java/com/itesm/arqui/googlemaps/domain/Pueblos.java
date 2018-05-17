package com.itesm.arqui.googlemaps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pueblos extends Entity{

    private String nombre;
    private Double latitud;
    private Double longitud;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    @Override
    public Class findDomainClass() {
        return Pueblos.class;
    }

    @Override
    public String toString() {
        return "{" +
                "  'nombre'='" + nombre + '\'' +
                ", 'latitud'=" + latitud +
                ", 'longitud'=" + longitud +
                '}';
    }

    public HashMap<String, String> toMap(){
        HashMap<String,String> obj = new HashMap<>();
        obj.put("nombre",nombre);
        obj.put("latitud", String.valueOf(latitud));
        obj.put("longitud", String.valueOf(longitud));
        return obj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((latitud == null && longitud == null) ? 0 : latitud.hashCode()+longitud.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pueblos other = (Pueblos) obj;
        Double mine = latitud+longitud;
        Double their = other.latitud+other.longitud;
        if (latitud == null && longitud == null) {
            if (other.latitud == null && other.longitud == null)
                return false;
        } else if (!mine.equals(their))
            return false;
        return true;
    }
}
