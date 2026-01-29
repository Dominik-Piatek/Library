package org.example.library.model;

public class Czytelnik {
    private int id;
    private String imie;
    private String nazwisko;
    private String nrTelefonu;
    private String email;
    private String hasloSkrot;
    private int pracownikId;

    public Czytelnik(int id, String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot, int pracownikId) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrTelefonu = nrTelefonu;
        this.email = email;
        this.hasloSkrot = hasloSkrot;
        this.pracownikId = pracownikId;
    }

    public Czytelnik(String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot, int pracownikId) {
        this(0, imie, nazwisko, nrTelefonu, email, hasloSkrot, pracownikId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getNrTelefonu() { return nrTelefonu; }
    public String getEmail() { return email; }
    public String getHasloSkrot() { return hasloSkrot; }
    public int getPracownikId() { return pracownikId; }
}
