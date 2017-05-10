package com.vishal.application.exception;

import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by vishal.joshi on 5/10/17.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends NestedRuntimeException {
    public InternalServerError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
