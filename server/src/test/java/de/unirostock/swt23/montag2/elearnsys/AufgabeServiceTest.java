package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.exception.AufgabeNotFoundException;
import de.unirostock.swt23.montag2.elearnsys.exception.RegistrationException;
import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import de.unirostock.swt23.montag2.elearnsys.repo.AufgabeRepository;
import de.unirostock.swt23.montag2.elearnsys.service.AufgabeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AufgabeServiceTest {

    @Mock
    private AufgabeRepository aufgabeRepository;

    @InjectMocks
    private AufgabeService aufgabeService;

    @Test
    public void addAufgabeTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setName("Aufgabe 1");

        when(aufgabeRepository.existsByName(aufgabe.getName())).thenReturn(false);
        when(aufgabeRepository.save(aufgabe)).thenReturn(aufgabe);

        Aufgabe addedAufgabe = aufgabeService.addAufgabe(aufgabe);

        assertNotNull(addedAufgabe);
        assertEquals(aufgabe.getName(), addedAufgabe.getName());

        verify(aufgabeRepository, times(1)).existsByName(aufgabe.getName());
        verify(aufgabeRepository, times(1)).save(aufgabe);
    }

    @Test(expected = RegistrationException.class)
    public void addAufgabeWithExistingNameTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setName("Aufgabe 1");

        when(aufgabeRepository.existsByName(aufgabe.getName())).thenReturn(true);

        aufgabeService.addAufgabe(aufgabe);

        verify(aufgabeRepository, times(1)).existsByName(aufgabe.getName());
        verify(aufgabeRepository, never()).save(aufgabe);
    }

    @Test
    public void updateAufgabeTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setId(1L);
        aufgabe.setName("Aufgabe 1");

        when(aufgabeRepository.save(aufgabe)).thenReturn(aufgabe);

        Aufgabe updatedAufgabe = aufgabeService.updateAufgabe(aufgabe);

        assertNotNull(updatedAufgabe);
        assertEquals(aufgabe.getId(), updatedAufgabe.getId());
        assertEquals(aufgabe.getName(), updatedAufgabe.getName());

        verify(aufgabeRepository, times(1)).save(aufgabe);
    }

    @Test
    public void allAufgabenTest() {
        Aufgabe aufgabe1 = new Aufgabe();
        aufgabe1.setId(1L);
        aufgabe1.setName("Aufgabe 1");

        Aufgabe aufgabe2 = new Aufgabe();
        aufgabe2.setId(2L);
        aufgabe2.setName("Aufgabe 2");

        List<Aufgabe> aufgaben = Arrays.asList(aufgabe1, aufgabe2);

        when(aufgabeRepository.findAll()).thenReturn(aufgaben);

        List<Aufgabe> result = aufgabeService.allAufgaben();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(aufgabe1));
        assertTrue(result.contains(aufgabe2));

        verify(aufgabeRepository, times(1)).findAll();
    }

    @Test
    public void findByIdTest() {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setId(1L);
        aufgabe.setName("Aufgabe 1");

        when(aufgabeRepository.findById(1L)).thenReturn(Optional.of(aufgabe));

        Aufgabe foundAufgabe = aufgabeService.findById(1L);

        assertNotNull(foundAufgabe);
        assertEquals(aufgabe.getId(), foundAufgabe.getId());
        assertEquals(aufgabe.getName(), foundAufgabe.getName());

        verify(aufgabeRepository, times(1)).findById(1L);
    }

    @Test(expected = AufgabeNotFoundException.class)
    public void findByIdWithNonexistentAufgabeTest() {
        when(aufgabeRepository.findById(2L)).thenReturn(Optional.empty());

        aufgabeService.findById(2L);

        verify(aufgabeRepository, times(1)).findById(2L);
    }
}
