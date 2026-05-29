package com.metasoft.veyra.platform.nursing.application.internal.commandservices;
import com.metasoft.veyra.platform.nursing.application.internal.outboundservices.acl.ExternalIamService;
import com.metasoft.veyra.platform.nursing.domain.model.aggregates.Relative;
import com.metasoft.veyra.platform.nursing.domain.model.commands.AssignUserToRelativeCommand;
import com.metasoft.veyra.platform.nursing.domain.model.commands.CreateRelativeCommand;
import com.metasoft.veyra.platform.nursing.domain.services.RelativeCommandService;
import com.metasoft.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.RelativeRepository;
import com.metasoft.veyra.platform.shared.domain.model.valueobjects.EmailAddress;
import org.springframework.stereotype.Service;
@Service
public class RelativeCommandServiceImpl implements RelativeCommandService {
    private final RelativeRepository relativeRepository;
 private  final ExternalIamService externalIamService;
    public RelativeCommandServiceImpl(RelativeRepository relativeRepository, ExternalIamService externalIamService) {
        this.relativeRepository = relativeRepository;
        this.externalIamService = externalIamService;
    }
    @Override
    public Long handle(CreateRelativeCommand command) {
    if (relativeRepository.findByEmailAddress(new EmailAddress(command.email())).isPresent()) {
        throw new IllegalArgumentException("Relative with email " + command.email() + " already exists.");
    }
    try {
        var relative = new Relative(command.email(),command.firstname(),command.lastname());
        relativeRepository.save(relative);
        return relative.getId();
    }catch (Exception e){
        throw new RuntimeException("Failed to create relative: " + e.getMessage(), e);
    }
    }
    @Override
    public Long handle(AssignUserToRelativeCommand command) {
       var relative = relativeRepository.findByEmailAddress(new EmailAddress(command.email())).orElseThrow(() -> new IllegalArgumentException("Relative with email " + command.email() + " not found."));
           relative.linkToUser(command.userId());
           relativeRepository.save(relative);
           return relative.getId();
    }

}