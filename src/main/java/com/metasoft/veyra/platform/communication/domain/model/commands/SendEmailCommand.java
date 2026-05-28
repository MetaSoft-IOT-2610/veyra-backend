package com.metasoft.veyra.platform.communication.domain.model.commands;

public record SendEmailCommand(
        String to,
        String subject,
        String content
) {
    public SendEmailCommand {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Email recipient cannot be null or blank");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Email subject cannot be null or blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Email content cannot be null or blank");
        }
    }
}
