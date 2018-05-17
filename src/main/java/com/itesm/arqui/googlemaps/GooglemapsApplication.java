package com.itesm.arqui.googlemaps;

import com.itesm.arqui.googlemaps.config.GlobalVariables;
import com.itesm.arqui.googlemaps.domain.Pueblos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class GooglemapsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GooglemapsApplication.class, args);
    }
}
