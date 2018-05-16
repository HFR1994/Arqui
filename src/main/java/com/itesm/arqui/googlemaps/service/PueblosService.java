package com.itesm.arqui.googlemaps.service;

import com.itesm.arqui.googlemaps.dao.PueblosDao;
import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Ruta;
import com.itesm.arqui.googlemaps.pojo.Result;
import com.itesm.arqui.googlemaps.pojo.templates.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PueblosService extends ServiceTemplate<Pueblos> {

    @Override
    public Class<Pueblos> domainClass() {
        return Pueblos.class;
    }

    @Autowired
    public PueblosDao pueblosDao;

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
                        List<Map<String, Object>> obj = pueblosDao.getEndpoints(data.getInicio(), data.getDestino());

                        Pueblos or = origen.get();
                        Pueblos des = destino.get();

                        StringBuilder places= new StringBuilder();

                        for (Map<String, Object> val: obj){
                            places.append("|");
                            places.append(String.valueOf(val.get("latitud")));
                            places.append(",");
                            places.append(String.valueOf(val.get("longitud")));
                        }

                        try {
                            String origin = "https://maps.googleapis.com/maps/api/directions/json";


                            Map<String, String> parameters = new HashMap<>();
                            parameters.put("origin", or.getLatitud()+","+or.getLongitud());
                            parameters.put("destination", des.getLatitud()+","+des.getLongitud());
                            parameters.put("waypoints","optimize:true"+places.toString());
                            parameters.put("key","AIzaSyDuik0qtQYYdbdP9yenCBfFtYzrJe2rJ7I");



                            URL url = new URL(origin.concat("?").concat(ParameterStringBuilder.getParamsString(parameters)));
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setDoOutput(true);
                            con.setConnectTimeout(10000);
                            con.setReadTimeout(10000);

                            int status = con.getResponseCode();

                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuffer content = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                content.append(inputLine);
                            }
                            in.close();

                            logger.info(String.valueOf(status));
                            logger.info(content.toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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