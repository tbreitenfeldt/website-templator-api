package com.timothybreitenfeldt.templator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidArgumentException extends RuntimeException {

    private static final long serialVersionUID = 8967716169727441575L;

    public InvalidArgumentException(String message) {
        super(message);
    }

}
