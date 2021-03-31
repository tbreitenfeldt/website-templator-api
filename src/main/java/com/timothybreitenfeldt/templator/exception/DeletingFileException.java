package com.timothybreitenfeldt.templator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DeletingFileException extends RuntimeException {

    private static final long serialVersionUID = -6947378196925013620L;

    public DeletingFileException(String message) {
        super(message);
    }

}
