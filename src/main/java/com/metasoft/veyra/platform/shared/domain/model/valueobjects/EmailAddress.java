<<<<<<<< HEAD:src/main/java/com/metasoft/veyra/platform/profiles/domain/model/valueobjects/EmailAddress.java
package com.metasoft.veyra.platform.profiles.domain.model.valueobjects;
========
package com.metasoft.veyra.platform.shared.domain.model.valueobjects;
>>>>>>>> origin/develop:src/main/java/com/metasoft/veyra/platform/shared/domain/model/valueobjects/EmailAddress.java

import jakarta.validation.constraints.Email;

public record EmailAddress( @Email String emailAddress ) {
    public  EmailAddress{
       if (emailAddress==null|| emailAddress.isBlank()){
              throw new IllegalArgumentException("Email address cannot be null or blank");
       }
    }
}
