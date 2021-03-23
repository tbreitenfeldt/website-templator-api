package com.timothybreitenfeldt.templator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DeletingFileException extends RuntimeException {

    public DeletingFileException(String message) {
        super(message);
    }

}
