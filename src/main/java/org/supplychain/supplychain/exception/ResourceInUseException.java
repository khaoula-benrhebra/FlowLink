package org.supplychain.supplychain.exception;

public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String resource, Long id, String usedIn) {
        super(String.format("Cannot delete %s with ID %s. It is used in %s", resource, id, usedIn));
    }
}