package ar.net.ut.backend.exception.impl;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.BackendException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceAlreadyExistsException extends BackendException {

    private final ResourceType resourceType;

    public ResourceAlreadyExistsException(ResourceType resourceType, String field, String input) {
        super(
                "Resource of type " + resourceType + " with " + field + " = " + input + " already exists",
                HttpStatus.CONFLICT,
                "RESOURCE_" + resourceType + "_ALREADY_EXISTS"
        );
        this.resourceType = resourceType;
    }
}
