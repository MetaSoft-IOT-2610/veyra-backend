package com.metasoft.veyra.platform.health.integration;

import com.metasoft.veyra.platform.health.application.internal.commandservices.AllergyCommandServiceImpl;
import com.metasoft.veyra.platform.health.application.internal.outboundservices.acl.ExternalNursingService;
import com.metasoft.veyra.platform.health.domain.model.aggregates.Allergy;
import com.metasoft.veyra.platform.health.domain.model.commands.RegisterAllergyCommand;
import com.metasoft.veyra.platform.health.domain.model.valueobjects.ResidentId;
import com.metasoft.veyra.platform.health.infrastructure.persistence.jpa.repositories.AllergyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HealthCommandServicesImplTest {

    // ── Allergy dependencies ──────────────────────────────────────────────────
    @Mock private AllergyRepository allergyRepository;
    @Mock private ExternalNursingService externalNursingService;
    @InjectMocks private AllergyCommandServiceImpl allergyCommandService;

    private final ResidentId residentId = new ResidentId(7L);

    // ════════════════════════════════════════════════════════════════════
    // AllergyCommandServiceImpl
    // ════════════════════════════════════════════════════════════════════


    @Test
    void shouldRegisterAllergyWhenResidentExists() {
        RegisterAllergyCommand command = new RegisterAllergyCommand(
                7L, "Urticaria", "Penicilina", "DRUG", "HIGH");

        when(externalNursingService.fetchResidentById(7L)).thenReturn(Optional.of(residentId));
        when(allergyRepository.existsByResidentIdAndAllergenName(residentId, "Penicilina"))
                .thenReturn(false);
        when(allergyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> allergyCommandService.handle(command));
        verify(allergyRepository).save(any(Allergy.class));
    }

    // ── 2. Lanza excepción si el residente no existe ──────────────────────────

    @Test
    void shouldThrowWhenResidentNotFound() {
        RegisterAllergyCommand command = new RegisterAllergyCommand(
                99L, "reacción", "Polen", "ENVIRONMENTAL", "MEDIUM");

        when(externalNursingService.fetchResidentById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> allergyCommandService.handle(command));

        assertEquals("resident id not found", ex.getMessage());
        verify(allergyRepository, never()).save(any());
    }

    // ── 3. Lanza excepción si la alergia ya está registrada ───────────────────

    @Test
    void shouldThrowWhenAllergyAlreadyExists() {
        RegisterAllergyCommand command = new RegisterAllergyCommand(
                7L, "reacción", "Penicilina", "DRUG", "HIGH");

        when(externalNursingService.fetchResidentById(7L)).thenReturn(Optional.of(residentId));
        when(allergyRepository.existsByResidentIdAndAllergenName(residentId, "Penicilina"))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> allergyCommandService.handle(command));

        assertTrue(ex.getMessage().contains("allergy already exists"));
        verify(allergyRepository, never()).save(any());
    }

}
