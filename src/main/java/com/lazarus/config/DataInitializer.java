package com.lazarus.config;

import com.lazarus.service.LazarusService;
import com.lazarus.util.CsvLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final LazarusService service;
    private final CsvLoader loader;

    public DataInitializer(LazarusService service, CsvLoader loader) {
        this.service = service;
        this.loader = loader;
    }
     
    

@PostConstruct
public void init() {
    try {
        var demo = loader.loadDemographics();
        var tele = loader.loadTelemetry();

        System.out.println("DEMO SIZE: " + demo.size());
        System.out.println("TELE SIZE: " + tele.size());

        service.buildPatients(demo);
        service.processTelemetry(tele);

        // 🔥 ADD DEBUG HERE
        var pres = loader.loadPrescriptions();
        System.out.println("🔥 PRES SIZE: " + pres.size());

        service.processPrescriptions(pres);

        System.out.println("PATIENTS: " + service.getAllPatients().size());

       

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}