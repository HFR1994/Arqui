package com.itesm.arqui.googlemaps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mapa extends Entity{

    private String inicio;
    private String destino;
    private String tiempoTot;
    private String kmTot;
    private List<Punto> puntos;

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTiempoTot() {
        return tiempoTot;
    }

    public void setTiempoTot(String tiempoTot) {
        this.tiempoTot = tiempoTot;
    }

    public String getKmTot() {
        return kmTot;
    }

    public void setKmTot(String kmTot) {
        this.kmTot = kmTot;
    }

    public List<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<Punto> puntos) {
        this.puntos = puntos;
    }

    @Override
    public Class findDomainClass() {
        return Mapa.class;
    }
}
