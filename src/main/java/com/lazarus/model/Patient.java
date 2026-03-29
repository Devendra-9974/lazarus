package com.lazarus.model;

import java.util.*;

public class Patient {
    public String uniqueId;
    public String ghostId;
    public int parity;

    public String name;
    public int age;
    public String ward;

    public List<Integer> bpm = new ArrayList<>();
    public List<Integer> spO2 = new ArrayList<>();
    public List<Boolean> alerts = new ArrayList<>();

    public List<String> scrambledMeds = new ArrayList<>();
public List<String> decryptedMeds = new ArrayList<>();
}