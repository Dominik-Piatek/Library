package org.example.library.model;

public class Czytelnik {
    private int id_Czytelnika;
    private String imie;
    private String nazwisko;
    private String nrTelefonu;
    private String email;
    private String hasloSkrot;

    public Czytelnik() {}

    // Konstruktor GŁÓWNY (używany przez DAO przy pobieraniu z bazy)
    public Czytelnik(int id_Czytelnika, String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot) {
        this.id_Czytelnika = id_Czytelnika;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrTelefonu = nrTelefonu;
        this.email = email;
        this.hasloSkrot = hasloSkrot;
    }

    // Konstruktor POMOCNICZY (dla Twoich okienek - LoginFrame itp.)
    // Przyjmuje dane bez ID na początku, a na końcu ignoruje stare ID pracownika
    public Czytelnik(String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot, int ignorowaneId) {
        this.id_Czytelnika = 0; // Baza sama nada ID
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrTelefonu = nrTelefonu;
        this.email = email;
        this.hasloSkrot = hasloSkrot;
    }

    // Gettery i Settery
    public int getId() { return id_Czytelnika; }
    public void setId(int id) { this.id_Czytelnika = id; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getNrTelefonu() { return nrTelefonu; }
    public void setNrTelefonu(String nrTelefonu) { this.nrTelefonu = nrTelefonu; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHasloSkrot() { return hasloSkrot; }
    public void setHasloSkrot(String hasloSkrot) { this.hasloSkrot = hasloSkrot; }
}