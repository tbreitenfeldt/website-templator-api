package com.timothybreitenfeldt.templator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ProjectAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 8178501597175766762L;

    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

}
