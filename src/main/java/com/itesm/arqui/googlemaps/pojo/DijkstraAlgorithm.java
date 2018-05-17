package com.itesm.arqui.googlemaps.pojo;

import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Punto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DijkstraAlgorithm {

    private final List<Pueblos> nodes;
    private final List<Punto> puntos;
    private Set<Pueblos> settledNodes;
    private Set<Pueblos> unSettledNodes;
    private Map<Pueblos, Pueblos> predecessors;
    private Map<Pueblos, Double> distance;
    private HashMap<String,Pueblos> datos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<Pueblos>(graph.getPueblos());
        this.puntos = new ArrayList<Punto>(graph.getPuntos());
        this.datos = graph.getDatos();
    }

    public void execute(Pueblos source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0.0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Pueblos node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Pueblos node) {
        List<Pueblos> adjacentNodes = getNeighbors(node);
        for (Pueblos target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private Double getDistance(Pueblos node, Pueblos target) {
        for (Punto Punto : puntos) {
            if (datos.get(Punto.getInicio()).equals(node) && datos.get(Punto.getDestino()).equals(target) || datos.get(Punto.getDestino()).equals(node) && datos.get(Punto.getInicio()).equals(target)) {
                return Punto.getKm();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Pueblos> getNeighbors(Pueblos node) {
        List<Pueblos> neighbors = new ArrayList<Pueblos>();

        for (Punto Punto : puntos) {
            if(!Punto.getInicio().equals(Punto.getDestino())) {
                Pueblos inicio = datos.get(Punto.getInicio());
                Pueblos fin = datos.get(Punto.getDestino());

                if (inicio.equals(node) && !isSettled(fin)) {
                    neighbors.add(fin);
                } else if (fin.equals(node) && !isSettled(inicio)) {
                    neighbors.add(inicio);
                }
            }
        }

        return neighbors;
    }

    private Pueblos getMinimum(Set<Pueblos> Puebloses) {
        Pueblos minimum = null;
        for (Pueblos Pueblos : Puebloses) {
            if (minimum == null) {
                minimum = Pueblos;
            } else {
                if (getShortestDistance(Pueblos) < getShortestDistance(minimum)) {
                    minimum = Pueblos;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Pueblos Pueblos) {
        return settledNodes.contains(Pueblos);
    }

    private Double getShortestDistance(Pueblos destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Pueblos> getPath(Pueblos target) {
        LinkedList<Pueblos> path = new LinkedList<Pueblos>();
        Pueblos step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

}
