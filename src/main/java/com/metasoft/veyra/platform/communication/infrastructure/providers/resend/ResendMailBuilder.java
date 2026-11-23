package com.metasoft.veyra.platform.communication.infrastructure.providers.resend;

import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;
import com.metasoft.veyra.platform.communication.infrastructure.providers.resend.configuration.ResendSettings;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.Template;

import java.util.List;

public final class ResendMailBuilder {

    private ResendMailBuilder() {
    }

    public static CreateEmailOptions buildPlainMail(ResendSettings settings, SendPlainEmailCommand command) {
        return baseBuilder(settings, command.recipients().values())
                .subject(command.subject())
                .text(command.plainContent())
                .build();
    }

    public static CreateEmailOptions buildHtmlMail(ResendSettings settings, SendHtmlEmailCommand command) {
        CreateEmailOptions.Builder builder = baseBuilder(settings, command.recipients().values())
                .subject(command.subject())
                .html(command.htmlContent());

        if (command.plainContent() != null && !command.plainContent().isBlank()) {
            builder.text(command.plainContent());
        }

        return builder.build();
    }

    public static CreateEmailOptions buildTemplateMail(ResendSettings settings, SendTemplateEmailCommand command) {
        Template.Builder templateBuilder = Template.builder().id(command.templateId());
        if (!command.dynamicTemplateData().isEmpty()) {
            templateBuilder.variables(command.dynamicTemplateData());
        }

        return baseBuilder(settings, command.recipients().values())
                .template(templateBuilder.build())
                .build();
    }

    private static CreateEmailOptions.Builder baseBuilder(ResendSettings settings, List<String> recipients) {
        return CreateEmailOptions.builder()
                .from(formatFromAddress(settings))
                .to(recipients);
    }

    private static String formatFromAddress(ResendSettings settings) {
        if (settings.fromName() != null && !settings.fromName().isBlank()) {
            return settings.fromName() + " <" + settings.fromEmail() + ">";
        }
        return settings.fromEmail();
    }
}
