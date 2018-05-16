package com.itesm.arqui.googlemaps.pojo;

import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Punto;

import java.util.List;

public class Graph {
    private final List<Pueblos> vertexes;
    private final List<Punto> edges;

    public Graph(List<Pueblos> vertexes, List<Punto> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Pueblos> getPueblos() {
        return vertexes;
    }

    public List<Punto> getPuntos() {
        return edges;
    }



}
