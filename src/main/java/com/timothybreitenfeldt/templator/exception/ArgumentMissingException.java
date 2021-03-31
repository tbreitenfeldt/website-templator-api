package com.timothybreitenfeldt.templator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ArgumentMissingException extends RuntimeException {

    private static final long serialVersionUID = 3213795166765408742L;

    public ArgumentMissingException(String message) {
        super(message);
    }

}
