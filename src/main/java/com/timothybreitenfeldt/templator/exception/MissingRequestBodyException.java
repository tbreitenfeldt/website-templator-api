package com.timothybreitenfeldt.templator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingRequestBodyException extends RuntimeException {

    private static final long serialVersionUID = 4210586034907792794L;

    public MissingRequestBodyException(String message) {
        super(message);
    }

}
