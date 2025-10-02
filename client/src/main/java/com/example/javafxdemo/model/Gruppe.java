package com.example.javafxdemo.model;

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
    @JsonIgnore
    private Aufgabe aufgabe;

    private String note;

    @Column(nullable = false)
    private AbgabeStatus status;

    @Lob
    @JsonIgnore
    @Column(length = 2000000)
    private String loesung;

    // Constructor for populating data
    public Gruppe(Long id, int number, int anzahl, int maxAnzahl, List<User> mitglieder, String note, AbgabeStatus status) {
        this.id = id;
        this.number = number;
        this.anzahl = anzahl;
        this.maxAnzahl = maxAnzahl;
        this.mitglieder = mitglieder;
        this.note = note;
        this.status = status;
    }

    // Constructor for creating newAufgabe
    public Gruppe(int number, int anzahl, int maxAnzahl, List<User> mitglieder, String note, AbgabeStatus status, String loesung) {
        this.number = number;
        this.anzahl = anzahl;
        this.maxAnzahl = maxAnzahl;
        this.mitglieder = mitglieder;
        this.note = note;
        this.status = status;
        this.loesung = loesung;
    }

    public Gruppe() {}

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
}
