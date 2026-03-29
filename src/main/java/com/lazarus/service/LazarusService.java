package com.lazarus.service;

import com.lazarus.model.*;
import com.lazarus.util.DataUtils;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class LazarusService {

    private Map<String, List<Patient>> ghostMap = new HashMap<>();

    // ---------------- BUILD PATIENTS ----------------
    public void buildPatients(List<Demographic> demoList) {

        for (Demographic d : demoList) {
            Patient p = new Patient();

            p.ghostId = d.ghostId;
            p.parity = d.parity;
            p.uniqueId = d.ghostId + "_" + d.parity;

            p.name = d.name;
            p.age = d.age;
            p.ward = (d.parity == 0) ? "Ward A" : "Ward B";

            ghostMap.computeIfAbsent(d.ghostId, k -> new ArrayList<>()).add(p);
        }
    }

    // ---------------- TELEMETRY ----------------
    public void processTelemetry(List<Telemetry> list) {

        Map<String, List<Telemetry>> tMap = new HashMap<>();

        for (Telemetry t : list) {
            tMap.computeIfAbsent(t.ghostId, k -> new ArrayList<>()).add(t);
        }

        for (String ghostId : tMap.keySet()) {

            List<Telemetry> tList = tMap.get(ghostId);
            List<Patient> patients = ghostMap.get(ghostId);

            if (patients == null || patients.size() < 2) continue;

            // Sort by packet_id (time)
            tList.sort(Comparator.comparingInt(t -> t.packetId));

            int mid = tList.size() / 2;

            fillVitals(patients.get(0), tList.subList(0, mid));
            fillVitals(patients.get(1), tList.subList(mid, tList.size()));
        }
    }

   // ............. prescription ...............

// Better validation (optional improvement)
private boolean looksLikeWord(String s) {
    // contains at least one vowel + reasonable length
    return s != null && s.length() >= 4 && s.matches(".*[aeiouAEIOU].*");
}

public void processPrescriptions(List<Prescription> list) {
    System.out.println("🔥 processPrescriptions STARTED");

    if (list == null) {
        System.out.println("❌ list is null");
    } else {
        System.out.println("✅ list size = " + list.size());
    }
    // Step 1: remove duplicates per ghost_id
    Map<String, Set<String>> map = new HashMap<>();

    for (Prescription p : list) {
        String ghostId = p.ghostId.trim().toUpperCase(); // 🔥 important fix

        map.computeIfAbsent(ghostId, k -> new HashSet<>())
           .add(p.scrambledMed);
    }

    // Step 2: assign to patients
    for (String ghostId : map.keySet()) {
     
         System.out.println("Looking for ghostId: " + ghostId);
System.out.println("Exists in map? " + ghostMap.containsKey(ghostId));

        List<Patient> patients = ghostMap.get(ghostId);
        if (patients == null || patients.size() < 2) continue;

        Patient p0 = patients.get(0);
        Patient p1 = patients.get(1);

        for (String med : map.get(ghostId)) {

            // decrypt using both ages
            String d0 = DataUtils.decrypt(med, p0.age);
            String d1 = DataUtils.decrypt(med, p1.age);

            // 🔍 debug (optional)
            // System.out.println(med + " -> " + d0 + " | " + d1);

            boolean valid0 = looksLikeWord(d0);
            boolean valid1 = looksLikeWord(d1);

            // ✅ Case 1: only p0 valid
            if (valid0 && !valid1) {
                p0.scrambledMeds.add(med);
                p0.decryptedMeds.add(d0);
            }
            // ✅ Case 2: only p1 valid
            else if (!valid0 && valid1) {
                p1.scrambledMeds.add(med);
                p1.decryptedMeds.add(d1);
            }
            // ✅ Case 3: both valid OR both invalid → assign BOTH
            else {
                p0.scrambledMeds.add(med);
                p0.decryptedMeds.add(d0);

                p1.scrambledMeds.add(med);
                p1.decryptedMeds.add(d1);
            }
        }
    }
   
}

    private void fillVitals(Patient p, List<Telemetry> list) {

        List<Integer> bpm = new ArrayList<>();
        List<Integer> sp = new ArrayList<>();

        for (Telemetry t : list) {
            int b = DataUtils.decodeHex(t.heartHex);
            bpm.add(b);
            sp.add(t.spO2);
        }

        DataUtils.fillMissing(sp);

        p.bpm = bpm;
        p.spO2 = sp;

        for (int b : bpm) {
            p.alerts.add(b < 60 || b > 100);
        }
    }

    // ---------------- GETTERS ----------------
    public List<Patient> getAllPatients() {
        List<Patient> res = new ArrayList<>();
        for (List<Patient> list : ghostMap.values()) {
            res.addAll(list);
        }
        return res;
    }

    public Patient getById(String id) {
        for (List<Patient> list : ghostMap.values()) {
            for (Patient p : list) {
                if (p.uniqueId.equals(id)) return p;
            }
        }
        return null;
    }
}