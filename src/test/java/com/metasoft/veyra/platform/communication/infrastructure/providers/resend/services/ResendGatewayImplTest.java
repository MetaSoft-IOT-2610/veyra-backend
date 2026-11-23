package com.metasoft.veyra.platform.communication.infrastructure.providers.resend.services;

import com.metasoft.veyra.platform.communication.domain.exceptions.CommunicationProviderNotConfiguredException;
import com.metasoft.veyra.platform.communication.domain.exceptions.ResendIntegrationException;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.valueobjects.EmailRecipients;
import com.metasoft.veyra.platform.communication.infrastructure.providers.resend.configuration.ResendSettings;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResendGatewayImplTest {

    @Mock
    private Resend resend;

    @Mock
    private Emails emails;

    private final ResendSettings resendSettings = new ResendSettings(
            "re_test_api_key",
            "noreply@veyra.com",
            "Veyra"
    );

    @Test
    void shouldSendPlainEmailWhenResendAcceptsRequest() throws ResendException {
        ResendGatewayImpl gateway = new ResendGatewayImpl(resendSettings, resend);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class))).thenReturn(new CreateEmailResponse("email_123"));

        SendPlainEmailCommand command = new SendPlainEmailCommand(
                new EmailRecipients(List.of("user@example.com")),
                "Welcome",
                "Hello from Veyra"
        );

        gateway.sendPlain(command);

        ArgumentCaptor<CreateEmailOptions> optionsCaptor = ArgumentCaptor.forClass(CreateEmailOptions.class);
        verify(emails).send(optionsCaptor.capture());
        CreateEmailOptions captured = optionsCaptor.getValue();
        assertEquals("Veyra <noreply@veyra.com>", captured.getFrom());
        assertEquals(List.of("user@example.com"), captured.getTo());
        assertEquals("Welcome", captured.getSubject());
        assertEquals("Hello from Veyra", captured.getText());
    }

    @Test
    void shouldSendHtmlEmailWhenResendAcceptsRequest() throws ResendException {
        ResendGatewayImpl gateway = new ResendGatewayImpl(resendSettings, resend);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class))).thenReturn(new CreateEmailResponse("email_123"));

        SendHtmlEmailCommand command = new SendHtmlEmailCommand(
                new EmailRecipients(List.of("user@example.com")),
                "Welcome",
                "<p>Hello from Veyra</p>",
                "Hello from Veyra"
        );

        gateway.sendHtml(command);

        ArgumentCaptor<CreateEmailOptions> optionsCaptor = ArgumentCaptor.forClass(CreateEmailOptions.class);
        verify(emails).send(optionsCaptor.capture());
        CreateEmailOptions captured = optionsCaptor.getValue();
        assertEquals("<p>Hello from Veyra</p>", captured.getHtml());
        assertEquals("Hello from Veyra", captured.getText());
    }

    @Test
    void shouldSendTemplateEmailWhenResendAcceptsRequest() throws ResendException {
        ResendGatewayImpl gateway = new ResendGatewayImpl(resendSettings, resend);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class))).thenReturn(new CreateEmailResponse("email_123"));

        SendTemplateEmailCommand command = new SendTemplateEmailCommand(
                new EmailRecipients(List.of("user@example.com")),
                "tpl_welcome",
                Map.of("firstName", "Ana")
        );

        gateway.sendTemplate(command);

        ArgumentCaptor<CreateEmailOptions> optionsCaptor = ArgumentCaptor.forClass(CreateEmailOptions.class);
        verify(emails).send(optionsCaptor.capture());
        CreateEmailOptions captured = optionsCaptor.getValue();
        assertEquals("tpl_welcome", captured.getTemplate().getId());
        assertEquals(Map.of("firstName", "Ana"), captured.getTemplate().getVariables());
    }

    @Test
    void shouldThrowWhenResendApiKeyIsMissing() {
        ResendGatewayImpl gateway = new ResendGatewayImpl(
                new ResendSettings("", "noreply@veyra.com", "Veyra"),
                resend
        );

        SendPlainEmailCommand command = new SendPlainEmailCommand(
                new EmailRecipients(List.of("user@example.com")),
                "Welcome",
                "Hello from Veyra"
        );

        assertThrows(CommunicationProviderNotConfiguredException.class, () -> gateway.sendPlain(command));
    }

    @Test
    void shouldThrowWhenResendRejectsRequest() throws ResendException {
        ResendGatewayImpl gateway = new ResendGatewayImpl(resendSettings, resend);
        when(resend.emails()).thenReturn(emails);
        when(emails.send(any(CreateEmailOptions.class)))
                .thenThrow(new ResendException("Invalid request", 400, "validation_error"));

        SendPlainEmailCommand command = new SendPlainEmailCommand(
                new EmailRecipients(List.of("user@example.com")),
                "Welcome",
                "Hello from Veyra"
        );

        assertThrows(ResendIntegrationException.class, () -> gateway.sendPlain(command));
    }
}
