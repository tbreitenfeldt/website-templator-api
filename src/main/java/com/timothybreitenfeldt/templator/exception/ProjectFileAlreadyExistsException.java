package com.timothybreitenfeldt.templator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ProjectFileAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 8178501597175766763L;

    public ProjectFileAlreadyExistsException(String message) {
        super(message);
    }

}
