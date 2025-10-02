package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.model.AbgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import de.unirostock.swt23.montag2.elearnsys.model.AufgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.Gruppe;
import de.unirostock.swt23.montag2.elearnsys.repo.GruppenRepository;
import de.unirostock.swt23.montag2.elearnsys.service.GruppenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GruppenServiceTest {

    @Mock
    private GruppenRepository gruppenRepository;

    @InjectMocks
    private GruppenService gruppenService;

    @Test
    public void addGruppeTest() {
        Gruppe gruppe = new Gruppe();

        when(gruppenRepository.save(gruppe)).thenReturn(gruppe);

        Gruppe addedGruppe = gruppenService.addGruppe(gruppe);

        assertNotNull(addedGruppe);
        assertSame(gruppe, addedGruppe);

        verify(gruppenRepository, times(1)).save(gruppe);
    }

    @Test
    public void findByIdTest() {
        Gruppe gruppe = new Gruppe();
        gruppe.setId(1L);

        when(gruppenRepository.findById(1L)).thenReturn(Optional.of(gruppe));

        Gruppe foundGruppe = gruppenService.findById(1L);

        assertNotNull(foundGruppe);
        assertEquals(gruppe.getId(), foundGruppe.getId());

        verify(gruppenRepository, times(1)).findById(1L);
    }

    @Test
    public void updateStatusBeforeTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setStartDate(LocalDate.now().plusDays(1)); // Set the start date to tomorrow
        aufgabe.setEndDate(LocalDate.now().plusDays(2)); // Set the end date to the day after tomorrow
        aufgabe.setStatus(AufgabeStatus.BETWEEN); // Set the initial status to BETWEEN

        gruppenService.updateStatus(aufgabe);

        assertEquals(AufgabeStatus.BEFORE, aufgabe.getStatus());
    }

    @Test
    public void updateStatusAfterTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setStartDate(LocalDate.now().minusDays(2)); // Set the start date to two days ago
        aufgabe.setEndDate(LocalDate.now().minusDays(1)); // Set the end date to yesterday
        aufgabe.setStatus(AufgabeStatus.BETWEEN); // Set the initial status to BETWEEN

        Gruppe gruppe1 = new Gruppe();
        gruppe1.setLoesung("Solution 1");
        Gruppe gruppe2 = new Gruppe();
        gruppe2.setLoesung("Solution 2");

        aufgabe.setGruppen(Arrays.asList(gruppe1, gruppe2)); // Set the list of Gruppen

        gruppenService.updateStatus(aufgabe);

        assertEquals(AufgabeStatus.AFTER, aufgabe.getStatus());
        assertEquals(AbgabeStatus.ABGEGEBEN, gruppe1.getStatus());
        assertEquals(AbgabeStatus.ABGEGEBEN, gruppe2.getStatus());

        verify(gruppenRepository, times(2)).save(any(Gruppe.class));
    }

    @Test
    public void updateStatusBetweenTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setStartDate(LocalDate.now().minusDays(1)); // Set the start date to yesterday
        aufgabe.setEndDate(LocalDate.now().plusDays(1)); // Set the end date to tomorrow
        aufgabe.setStatus(AufgabeStatus.BETWEEN); // Set the initial status to BETWEEN

        gruppenService.updateStatus(aufgabe);

        assertEquals(AufgabeStatus.BETWEEN, aufgabe.getStatus());
    }
}

