package org.example.library.model;

public class Pracownik {
    private int id;
    private String imie;
    private String nazwisko;
    private String login;
    private String hasloSkrot;
    private String rola;
    private int idAdministratora;

    public Pracownik(int id, String imie, String nazwisko, String login, String hasloSkrot, String rola, int idAdministratora) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.hasloSkrot = hasloSkrot;
        this.rola = rola;
        this.idAdministratora = idAdministratora;
    }

    public Pracownik(String imie, String nazwisko, String login, String hasloSkrot, String rola, int idAdministratora) {
        this(0, imie, nazwisko, login, hasloSkrot, rola, idAdministratora);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getLogin() { return login; }
    public String getHasloSkrot() { return hasloSkrot; }
    public String getRola() { return rola; }
    public int getIdAdministratora() { return idAdministratora; }
}
