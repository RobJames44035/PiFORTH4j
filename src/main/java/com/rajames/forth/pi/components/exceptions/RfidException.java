package com.rajames.forth.pi.components.exceptions;

import com.rajames.forth.pi.components.internal.rfid.PcdError;

/**
 * Base exception for indicating failures within the RFID component
 */
public class RfidException extends Exception {
    public RfidException(String message) {
        super(message);
    }

    public RfidException(String message, Throwable previous) {
        super(message, previous);
    }

    public RfidException(PcdError error) {
        super(error.getDescription());
    }
}
