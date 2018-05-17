package com.itesm.arqui.googlemaps.service;

import com.itesm.arqui.googlemaps.config.GlobalVariables;
import com.itesm.arqui.googlemaps.dao.PueblosDao;
import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Ruta;
import com.itesm.arqui.googlemaps.pojo.DijkstraAlgorithm;
import com.itesm.arqui.googlemaps.pojo.Graph;
import com.itesm.arqui.googlemaps.pojo.Result;
import com.itesm.arqui.googlemaps.pojo.templates.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.*;

@Service
public class PueblosService extends ServiceTemplate<Pueblos> {

    @Override
    public Class<Pueblos> domainClass() {
        return Pueblos.class;
    }

    @Autowired
    public PueblosDao pueblosDao;


    @Autowired
    private GlobalVariables globalVariables;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Result getPueblos(){
        Result result = new Result();

        List<Map<String, Object>> var = pueblosDao.getPueblos();
        HashMap<String,Object> map = new HashMap<>();
        for (Map<String, Object> i : var){
            map.put((String) i.get("uuid"),i);
        }
        result.setCode(Result.OK);
        result.setMessage("¡Exito!");
        result.setData(map);

        return result;
    }

    public Result getRoute(Ruta data){
        Result result = new Result();

        if(data == null){
            result.setCode(Result.BAD_REQUEST);
            result.setMessage("¡Ups! No recibi ningun dato");
            result.setData(null);
            return result;
        }else{
            if(data.getInicio() == null || data.getDestino() == null){
                result.setCode(Result.BAD_REQUEST);
                result.setMessage("¡Ups! Debes agregar un lugar de salida y destino");
                result.setData(null);
            }else{

                Optional<Pueblos> origen = pueblosDao.getByName(data.getInicio());
                Optional<Pueblos> destino = pueblosDao.getByName(data.getDestino());

                if(origen.isPresent()){
                    if(destino.isPresent()){
                        Pueblos or = origen.get();
                        Pueblos des = destino.get();

                        globalVariables.getPueblos();

                        // Lets check from location Loc_1 to Loc_10
                        Graph graph = new Graph(globalVariables.getPueblos(), globalVariables.getPuntos());
                        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
                        dijkstra.execute(or);
                        LinkedList<Pueblos> path = dijkstra.getPath(des);

                        if(path != null && path.size() > 0){
                            HashMap<Integer, HashMap<String, String>> response = new HashMap<>();
                            int i=0;
                            for (Pueblos vertex : path) {
                                response.put(i++,vertex.toMap());
                            }
                            result.setData(response);
                        }else{
                            result.setData(null);
                        }

                        result.setCode(Result.OK);
                        result.setMessage("¡Exito! Todo salio como se debe");

                    }else{
                        result.setCode(Result.BAD_REQUEST);
                        result.setMessage("¡Ups! No existe el pueblo de destino");
                        result.setData(null);
                    }
                }else{
                    result.setCode(Result.BAD_REQUEST);
                    result.setMessage("¡Ups! No existe el pueblo de origen");
                    result.setData(null);
                }
            }
        }
        return result;
    }

}

class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}