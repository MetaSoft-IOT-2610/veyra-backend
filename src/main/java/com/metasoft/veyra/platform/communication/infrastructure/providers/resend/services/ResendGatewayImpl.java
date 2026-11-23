package com.metasoft.veyra.platform.communication.infrastructure.providers.resend.services;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.email.EmailGateway;
import com.metasoft.veyra.platform.communication.domain.exceptions.CommunicationProviderNotConfiguredException;
import com.metasoft.veyra.platform.communication.domain.exceptions.ResendIntegrationException;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;
import com.metasoft.veyra.platform.communication.infrastructure.providers.resend.ResendMailBuilder;
import com.metasoft.veyra.platform.communication.infrastructure.providers.resend.configuration.ResendSettings;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.resend.enabled", havingValue = "true")
public class ResendGatewayImpl implements EmailGateway {

    private final ResendSettings resendSettings;
    private final Resend resend;

    public ResendGatewayImpl(ResendSettings resendSettings, Resend resend) {
        this.resendSettings = resendSettings;
        this.resend = resend;
    }

    @Override
    public void sendPlain(SendPlainEmailCommand command) {
        validateConfiguration();
        CreateEmailOptions options = ResendMailBuilder.buildPlainMail(resendSettings, command);
        sendMail(options, "plain", command.recipients().values().size(), command.subject());
    }

    @Override
    public void sendHtml(SendHtmlEmailCommand command) {
        validateConfiguration();
        CreateEmailOptions options = ResendMailBuilder.buildHtmlMail(resendSettings, command);
        sendMail(options, "html", command.recipients().values().size(), command.subject());
    }

    @Override
    public void sendTemplate(SendTemplateEmailCommand command) {
        validateConfiguration();
        CreateEmailOptions options = ResendMailBuilder.buildTemplateMail(resendSettings, command);
        sendMail(options, "template", command.recipients().values().size(), command.templateId());
    }

    private void validateConfiguration() {
        if (resendSettings.apiKey() == null || resendSettings.apiKey().isBlank()) {
            throw new CommunicationProviderNotConfiguredException("Resend");
        }
        if (resendSettings.fromEmail() == null || resendSettings.fromEmail().isBlank()) {
            throw new CommunicationProviderNotConfiguredException("Resend");
        }
    }

    private void sendMail(CreateEmailOptions options, String emailType, int recipientCount, String reference) {
        try {
            CreateEmailResponse response = resend.emails().send(options);
            String emailId = response != null ? response.getId() : null;
            log.info("Resend email accepted (type={}, recipientCount={}, reference={}, emailId={})",
                    emailType, recipientCount, reference, emailId);
        } catch (ResendException exception) {
            log.error("Resend email failed (type={}, recipientCount={}, reference={}, statusCode={})",
                    emailType, recipientCount, reference, exception.getStatusCode());
            throw new ResendIntegrationException("Failed to send email via Resend: " + exception.getMessage(), exception);
        }
    }
}
