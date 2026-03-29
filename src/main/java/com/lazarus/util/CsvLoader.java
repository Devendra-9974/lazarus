package com.lazarus.util;

import com.lazarus.model.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class CsvLoader {

    public List<Demographic> loadDemographics() throws Exception {

        List<Demographic> list = new ArrayList<>();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("patient_demographics.csv").getInputStream()
                )
        );

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            String[] p = line.split(",");

            Demographic d = new Demographic();
            d.ghostId = p[1];
            d.parity = Integer.parseInt(p[2]);
            d.name = p[3];
            d.age = Integer.parseInt(p[4]);

            list.add(d);
        }

        return list;
    }

    public List<Telemetry> loadTelemetry() throws Exception {

        List<Telemetry> list = new ArrayList<>();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("telemetry_logs.csv").getInputStream()
                )
        );

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            String[] p = line.split(",");

            Telemetry t = new Telemetry();
            t.packetId = Integer.parseInt(p[0]);
            t.ghostId = p[1];
            t.roomId = Integer.parseInt(p[2]);
            t.heartHex = p[3];

            if (p.length > 4 && !p[4].isEmpty()) {
                t.spO2 = Integer.parseInt(p[4]);
            } else {
                t.spO2 = null;
            }

            list.add(t);
        }

        return list;
    }

public List<Prescription> loadPrescriptions() throws Exception {

    List<Prescription> list = new ArrayList<>();

    BufferedReader br = new BufferedReader(
            new InputStreamReader(
                    new ClassPathResource("prescription_audit.csv").getInputStream()
            )
    );

    String line;

    br.readLine(); // skip header

    while ((line = br.readLine()) != null) {

        String[] p = line.split(",", -1); // 🔥 important

        if (p.length < 3) continue;

        try {
            Prescription pr = new Prescription();

            // 🔥 IGNORE rxId completely (not needed)
            pr.ghostId = p[1].trim().toUpperCase();
            pr.scrambledMed = p[2].trim();

            if (pr.scrambledMed.isEmpty()) continue;

            list.add(pr);

        } catch (Exception e) {
            System.out.println("Skipping bad row: " + line);
        }
    }

    System.out.println("Loaded prescriptions: " + list.size());

    return list;
}
}