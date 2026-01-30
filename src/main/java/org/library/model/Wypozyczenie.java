package org.library.model;

import java.util.Date;

public class Wypozyczenie {
    private int id;
    private Date dataWypozyczenia;
    private Date planowanyTerminZwrotu;
    private Date faktycznaDataZwrotu;
    private double kara;
    private int czytelnikId;
    private int egzemplarzId;
    private int pracownikId;

    public Wypozyczenie(int id, Date dataWypozyczenia, Date planowanyTerminZwrotu, Date faktycznaDataZwrotu, double kara, int czytelnikId, int egzemplarzId, int pracownikId) {
        this.id = id;
        this.dataWypozyczenia = dataWypozyczenia;
        this.planowanyTerminZwrotu = planowanyTerminZwrotu;
        this.faktycznaDataZwrotu = faktycznaDataZwrotu;
        this.kara = kara;
        this.czytelnikId = czytelnikId;
        this.egzemplarzId = egzemplarzId;
        this.pracownikId = pracownikId;
    }

    public Wypozyczenie(Date dataWypozyczenia, Date planowanyTerminZwrotu, int czytelnikId, int egzemplarzId, int pracownikId) {
        this(0, dataWypozyczenia, planowanyTerminZwrotu, null, 0.0, czytelnikId, egzemplarzId, pracownikId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDataWypozyczenia() { return dataWypozyczenia; }
    public Date getPlanowanyTerminZwrotu() { return planowanyTerminZwrotu; }
    public Date getFaktycznaDataZwrotu() { return faktycznaDataZwrotu; }
    public void setFaktycznaDataZwrotu(Date faktycznaDataZwrotu) { this.faktycznaDataZwrotu = faktycznaDataZwrotu; }
    public double getKara() { return kara; }
    public void setKara(double kara) { this.kara = kara; }
    public int getCzytelnikId() { return czytelnikId; }
    public int getEgzemplarzId() { return egzemplarzId; }
    public int getPracownikId() { return pracownikId; }
}
