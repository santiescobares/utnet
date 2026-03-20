package ar.net.ut.backend.exception.impl;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.BackendException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends BackendException {

    private final ResourceType resourceType;

    public ResourceNotFoundException(ResourceType resourceType, String field, String input) {
        super(
                "Resource of type " + resourceType + " with " + field + " = " + input + " not found",
                HttpStatus.NOT_FOUND,
                "RESOURCE_" + resourceType + "_NOT_FOUND"
        );
        this.resourceType = resourceType;
    }
}
