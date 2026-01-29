package org.example.library.model;

public abstract class Uzytkownik {
    protected int id;
    protected String login; // W przypadku czytelnika tu trafi e-mail
    protected String haslo;
    protected String rola;

    public Uzytkownik(int id, String login, String haslo, String rola) {
        this.id = id;
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getHaslo() { return haslo; }
    public String getRola() { return rola; }
}