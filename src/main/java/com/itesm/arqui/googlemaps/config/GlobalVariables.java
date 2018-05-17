package com.itesm.arqui.googlemaps.config;

import com.itesm.arqui.googlemaps.dao.PueblosDao;
import com.itesm.arqui.googlemaps.dao.PuntoDao;
import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Punto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GlobalVariables {

    public ArrayList<Punto> puntos;
    public ArrayList<Pueblos> pueblos;

    public GlobalVariables() {
        puntos=new ArrayList<>();
        pueblos=new ArrayList<>();
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PuntoDao puntoDao;

    @Autowired
    PueblosDao pueblosDao;

    public ArrayList<Punto> getPuntos() {

        HashSet<String> set = new HashSet<>();

        if(puntos == null || puntos.isEmpty()) {
            ArrayList<Pueblos> lugares = getPueblos();
            for (Pueblos pueb : lugares) {
                for(int i = lugares.indexOf(pueb); i<111; i++){
                    Pueblos p2 = pueblos.get(i);

                    Optional<Punto> tmp = puntoDao.getByPueblos(pueb, p2);

                    if(tmp.isPresent()){

                        Punto tmp1 = tmp.get();

                        if(!set.contains(tmp.get().toString())){
                            puntos.add(tmp1);
                            set.add(tmp1.toString());
                        }

                        if(!set.contains(tmp1.getDestino() + " - " + tmp1.getInicio())){
                            Punto tmp2 = new Punto();
                            tmp2.setDestino(tmp1.getInicio());
                            tmp2.setInicio(tmp1.getDestino());
                            tmp2.setKm(tmp1.getKm());
                            tmp2.setTiempo(tmp1.getTiempo());
                            puntos.add(tmp2);
                            set.add(tmp2.toString());
                        }
                    }
                }
            }

            return puntos;
        }else{
            return puntos;
        }
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        puntos = puntos;
    }

    public ArrayList<Pueblos> getPueblos(){
        if(pueblos == null || pueblos.isEmpty()){
            List<Map<String, Object>> pueb = pueblosDao.getPueblos();

            for(Map<String, Object> pueblo: pueb){
                Pueblos p1 = new Pueblos();
                p1.setNombre(String.valueOf(pueblo.get("nombre")));
                p1.setLatitud(Double.parseDouble(String.valueOf(pueblo.get("latitud"))));
                p1.setLongitud(Double.parseDouble(String.valueOf(pueblo.get("longitud"))));
                pueblos.add(p1);
            }

            return pueblos;
        }else{
            return pueblos;
        }
    }

    public void setPueblos(ArrayList<Pueblos> pueblos) {
        pueblos = pueblos;
    }
}

