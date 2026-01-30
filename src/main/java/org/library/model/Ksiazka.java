package org.library.model;

public class Ksiazka {
    private String isbn;
    private String tytul;
    private String autor;
    private String gatunek;
    private int rokWydania;
    private String dziedzina;


    public Ksiazka(String isbn, String tytul, String autor, String gatunek, int rokWydania, String dziedzina) {
        this.isbn = isbn;
        this.tytul = tytul;
        this.autor = autor;
        this.gatunek = gatunek;
        this.rokWydania = rokWydania;
        this.dziedzina = dziedzina;

    }

    public String getIsbn() { return isbn; }
    public String getTytul() { return tytul; }
    public String getAutor() { return autor; }
    public String getGatunek() { return gatunek; }
    public int getRokWydania() { return rokWydania; }
    public String getDziedzina() { return dziedzina; }

}