package org.example.library.model;

// WAŻNE: Dodano "extends Uzytkownik"
public class Czytelnik extends Uzytkownik {
    private int id_Czytelnika;
    private String imie;
    private String nazwisko;
    private String nrTelefonu;
    private String email;
    private String hasloSkrot;

    public Czytelnik() {
        // Pusty konstruktor dla bezpieczeństwa
        super(0, "", "", "Czytelnik");
    }

    // Konstruktor GŁÓWNY (używany przez DAO)
    public Czytelnik(int id_Czytelnika, String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot) {
        // Przekazujemy dane do klasy nadrzędnej (Uzytkownik):
        // ID, Login (tu Email), Hasło, Rola ("Czytelnik")
        super(id_Czytelnika, email, hasloSkrot, "Czytelnik");

        this.id_Czytelnika = id_Czytelnika;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrTelefonu = nrTelefonu;
        this.email = email;
        this.hasloSkrot = hasloSkrot;
    }

    // Konstruktor POMOCNICZY (dla okienek)
    public Czytelnik(String imie, String nazwisko, String nrTelefonu, String email, String hasloSkrot, int ignorowaneId) {
        super(0, email, hasloSkrot, "Czytelnik");

        this.id_Czytelnika = 0;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrTelefonu = nrTelefonu;
        this.email = email;
        this.hasloSkrot = hasloSkrot;
    }

    // Gettery i Settery
    @Override
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