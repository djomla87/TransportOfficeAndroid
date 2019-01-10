package com.example.mt.menitest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mladen.todorovic on 20.09.2017..
 */

public class Task implements Serializable {


    private int IdTask;
    private String SerijskiBroj;
    private String Vozilo;
    private String Istovar;
    private String Roba;
    private String Status;
    private String DatumAzuriranja;
    private String Utovar;
    private String UvoznaSpedicija;
    private String IzvoznaSpedicija;
    private String Uvoznik;
    private String Izvoznik;
    private String Napomena;
    private String RefBroj;
    private String Pregledano;

    public Task(int idTask, String serijskiBroj, String vozilo, String istovar, String roba, String status, String datumAzuriranja, String utovar, String uvoznaSpedicija, String izvoznaSpedicija, String uvoznik, String izvoznik, String napomena, String refBroj, String pregledano) {
        IdTask = idTask;
        SerijskiBroj = serijskiBroj;
        Vozilo = vozilo;
        Istovar = istovar;
        Roba = roba;
        Status = status;
        DatumAzuriranja = datumAzuriranja;
        Utovar = utovar;
        UvoznaSpedicija = uvoznaSpedicija;
        IzvoznaSpedicija = izvoznaSpedicija;
        Uvoznik = uvoznik;
        Izvoznik = izvoznik;
        Napomena = napomena;
        RefBroj = refBroj;
        Pregledano = pregledano;
    }

    public List<Task> getAllTask() {
        return new ArrayList<Task>();
    }


    public int getIdTask() {
        return IdTask;
    }

    public void setIdTask(int idTask) {
        IdTask = idTask;
    }
    public String getSerijskiBroj() {
        return SerijskiBroj;
    }

    public void setSerijskiBroj(String serijskiBroj) {
        SerijskiBroj = serijskiBroj;
    }

    public String getVozilo() {
        return Vozilo;
    }

    public void setVozilo(String vozilo) {
        Vozilo = vozilo;
    }

    public String getIstovar() {
        return Istovar;
    }

    public void setIstovar(String istovar) {
        Istovar = istovar;
    }

    public String getRoba() {
        return Roba;
    }

    public void setRoba(String roba) {
        Roba = roba;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDatumAzuriranja() {
        return DatumAzuriranja;
    }

    public void setDatumAzuriranja(String datumAzuriranja) {
        DatumAzuriranja = datumAzuriranja;
    }

    public String getUtovar() {
        return Utovar;
    }

    public void setUtovar(String utovar) {
        Utovar = utovar;
    }

    public String getUvoznaSpedicija() {
        return UvoznaSpedicija;
    }

    public void setUvoznaSpedicija(String uvoznaSpedicija) {
        UvoznaSpedicija = uvoznaSpedicija;
    }

    public String getIzvoznaSpedicija() {
        return IzvoznaSpedicija;
    }

    public void setIzvoznaSpedicija(String izvoznaSpedicija) {
        IzvoznaSpedicija = izvoznaSpedicija;
    }

    public String getUvoznik() {
        return Uvoznik;
    }

    public void setUvoznik(String uvoznik) {
        Uvoznik = uvoznik;
    }

    public String getIzvoznik() {
        return Izvoznik;
    }

    public void setIzvoznik(String izvoznik) {
        Izvoznik = izvoznik;
    }

    public String getNapomena() {
        return Napomena;
    }

    public void setNapomena(String napomena) {
        Napomena = napomena;
    }

    public String getRefBroj() {
        return RefBroj;
    }

    public void setRefBroj(String refBroj) {
        RefBroj = refBroj;
    }

    public String getPregledano() {
        return Pregledano;
    }

    public void setPregledano(String pregledano) {
        Pregledano = pregledano;
    }
}
