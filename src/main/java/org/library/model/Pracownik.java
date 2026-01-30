package org.library.model;

public class Pracownik extends Uzytkownik {
    private int id_Pracownika;
    private String imie;
    private String nazwisko;
    private String login;
    private String hasloSkrot;
    private String rola;

    public Pracownik() {
        super(0, "", "", "");
    }

    public Pracownik(int id_Pracownika, String imie, String nazwisko, String login, String hasloSkrot, String rola) {
        super(id_Pracownika, login, hasloSkrot, rola);

        this.id_Pracownika = id_Pracownika;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.hasloSkrot = hasloSkrot;
        this.rola = rola;
    }

    public Pracownik(String imie, String nazwisko, String login, String hasloSkrot, String rola, int ignorowaneId) {
        super(0, login, hasloSkrot, rola);

        this.id_Pracownika = 0;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.hasloSkrot = hasloSkrot;
        this.rola = rola;
    }

    @Override
    public int getId() { return id_Pracownika; }
    public void setId(int id) { this.id_Pracownika = id; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    @Override
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getHasloSkrot() { return hasloSkrot; }
    public void setHasloSkrot(String hasloSkrot) { this.hasloSkrot = hasloSkrot; }

    @Override
    public String getRola() { return rola; }
    public void setRola(String rola) { this.rola = rola; }
}