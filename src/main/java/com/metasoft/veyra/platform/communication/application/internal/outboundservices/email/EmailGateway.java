package com.metasoft.veyra.platform.communication.application.internal.outboundservices.email;

import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;

public interface EmailGateway {
    void sendPlain(SendPlainEmailCommand command);

    void sendHtml(SendHtmlEmailCommand command);

    void sendTemplate(SendTemplateEmailCommand command);
}
