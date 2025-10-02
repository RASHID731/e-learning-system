package de.unirostock.swt23.montag2.elearnsys.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "gruppen")
public class Gruppe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private int anzahl;

    @Column(nullable = false)
    private int maxAnzahl;

    @ManyToMany
    private List<User> mitglieder;

    @ManyToOne
    @JoinColumn(name = "aufgabe_id")
    @JsonIgnore // Ignore the parent field during JSON serialization
    private Aufgabe aufgabe;

    private String note;

    @Column(nullable = false)
    private AbgabeStatus status;

    @Lob
    @JsonIgnore
    @Column(length = 2000000)
    private String loesung;

    public Gruppe() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public int getMaxAnzahl() {
        return maxAnzahl;
    }

    public void setMaxAnzahl(int maxAnzahl) {
        this.maxAnzahl = maxAnzahl;
    }

    public List<User> getMitglieder() {
        return mitglieder;
    }

    public void setMitglieder(List<User> mitglieder) {
        this.mitglieder = mitglieder;
    }

    public Aufgabe getAufgabe() {
        return aufgabe;
    }

    public void setAufgabe(Aufgabe aufgabe) {
        this.aufgabe = aufgabe;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AbgabeStatus getStatus() {
        return status;
    }

    public void setStatus(AbgabeStatus status) {
        this.status = status;
    }

    public String getLoesung() {
        return loesung;
    }

    public void setLoesung(String loesung) {
        this.loesung = loesung;
    }
}
