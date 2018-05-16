package com.itesm.arqui.googlemaps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Punto extends Entity{

    private Pueblos inicio;
    private Pueblos destino;
    private Double tiempo;
    private Double km;

    public Pueblos getInicio() {
        return inicio;
    }

    public void setInicio(Pueblos inicio) {
        this.inicio = inicio;
    }

    public Pueblos getDestino() {
        return destino;
    }

    public void setDestino(Pueblos destino) {
        this.destino = destino;
    }

    public Double getTiempo() {
        return tiempo;
    }

    public void setTiempo(Double tiempo) {
        this.tiempo = tiempo;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    @Override
    public Class findDomainClass() {
        return Punto.class;
    }
}
