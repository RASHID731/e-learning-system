package com.example.javafxdemo.DTO;

import com.example.javafxdemo.model.AufgabeStatus;
import com.example.javafxdemo.model.Gruppe;
import com.example.javafxdemo.model.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "aufgaben")
public class AufgabeDTO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private int groupCount;

    @Column(nullable = false)
    private int maxNumber;

    @Column(nullable = false, length = 2000000)
    private String aufgabenStellung;

    @Column(nullable = false)
    private AufgabeStatus status;

    @Column(nullable = false)
    private Boolean freieEinschreibung;

    @OneToMany(mappedBy = "aufgabe", cascade = CascadeType.ALL)
    private List<Gruppe> gruppen;

    public AufgabeDTO(String name, LocalDate startDate, LocalDate endDate, User author, int groupCount, int maxNumber, String aufgabenStellung, AufgabeStatus status, List<Gruppe> gruppen, Boolean freieEinschreibung) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.author = author;
        this.groupCount = groupCount;
        this.maxNumber = maxNumber;
        this.aufgabenStellung = aufgabenStellung;
        this.status = status;
        this.gruppen = gruppen;
        this.freieEinschreibung = freieEinschreibung;
    }

    public AufgabeDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public String getAufgabenStellung() {
        return aufgabenStellung;
    }

    public void setAufgabenStellung(String aufgabenStellung) {
        this.aufgabenStellung = aufgabenStellung;
    }

    public AufgabeStatus getStatus() {
        return status;
    }

    public void setStatus(AufgabeStatus status) {
        this.status = status;
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public void setGruppen(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    public Boolean getFreieEinschreibung() {
        return freieEinschreibung;
    }

    public void setFreieEinschreibung(Boolean freieEinschreibung) {
        this.freieEinschreibung = freieEinschreibung;
    }
}