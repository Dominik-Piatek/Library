package org.library.model;

public class Egzemplarz {
    private int id;
    private String kodKreskowy;
    private int lokalizacjaRegal;
    private int lokalizacjaPolka;
    private String statusWypozyczenia;
    private String ksiazkaIsbn;

    public Egzemplarz(int id, String kodKreskowy, int lokalizacjaRegal, int lokalizacjaPolka, String statusWypozyczenia, String ksiazkaIsbn) {
        this.id = id;
        this.kodKreskowy = kodKreskowy;
        this.lokalizacjaRegal = lokalizacjaRegal;
        this.lokalizacjaPolka = lokalizacjaPolka;
        this.statusWypozyczenia = statusWypozyczenia;
        this.ksiazkaIsbn = ksiazkaIsbn;
    }

    public Egzemplarz(String kodKreskowy, int lokalizacjaRegal, int lokalizacjaPolka, String statusWypozyczenia, String ksiazkaIsbn) {
        this(0, kodKreskowy, lokalizacjaRegal, lokalizacjaPolka, statusWypozyczenia, ksiazkaIsbn);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getKodKreskowy() { return kodKreskowy; }
    public void setKodKreskowy(String kodKreskowy) { this.kodKreskowy = kodKreskowy; }

    public int getLokalizacjaRegal() { return lokalizacjaRegal; }
    public void setLokalizacjaRegal(int lokalizacjaRegal) { this.lokalizacjaRegal = lokalizacjaRegal; }

    public int getLokalizacjaPolka() { return lokalizacjaPolka; }
    public void setLokalizacjaPolka(int lokalizacjaPolka) { this.lokalizacjaPolka = lokalizacjaPolka; }

    public String getStatusWypozyczenia() { return statusWypozyczenia; }
    public void setStatusWypozyczenia(String status) { this.statusWypozyczenia = status; }
    
    public String getKsiazkaIsbn() { return ksiazkaIsbn; }
    public void setKsiazkaIsbn(String ksiazkaIsbn) { this.ksiazkaIsbn = ksiazkaIsbn; }
}