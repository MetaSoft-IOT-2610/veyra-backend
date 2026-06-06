<<<<<<<< HEAD:src/main/java/com/metasoft/veyra/platform/profiles/domain/model/valueobjects/PersonName.java
package com.metasoft.veyra.platform.profiles.domain.model.valueobjects;
========
package com.metasoft.veyra.platform.shared.domain.model.valueobjects;
>>>>>>>> origin/develop:src/main/java/com/metasoft/veyra/platform/shared/domain/model/valueobjects/PersonName.java

import jakarta.persistence.Embeddable;

@Embeddable
public record PersonName(String firstName,String lastName) {
    public PersonName{
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
    }
    public String getFullName(){
        return firstName+" "+lastName;
    }

}
