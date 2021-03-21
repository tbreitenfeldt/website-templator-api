package com.timothybreitenfeldt.templator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ProjectAlreadyExistsException extends RuntimeException {

    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

}
