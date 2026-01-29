package org.example.library.model;

import java.sql.Date;

public class Rezerwacja {
    private int id;
    private Date dataRezerwacji;
    private int czytelnikId;
    private String ksiazkaIsbn;
    private String status; // e.g., "Aktywna", "Zrealizowana", "Anulowana"

    public Rezerwacja(int id, Date dataRezerwacji, int czytelnikId, String ksiazkaIsbn, String status) {
        this.id = id;
        this.dataRezerwacji = dataRezerwacji;
        this.czytelnikId = czytelnikId;
        this.ksiazkaIsbn = ksiazkaIsbn;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Date getDataRezerwacji() {
        return dataRezerwacji;
    }

    public int getCzytelnikId() {
        return czytelnikId;
    }

    public String getKsiazkaIsbn() {
        return ksiazkaIsbn;
    }

    public String getStatus() {
        return status;
    }
}
