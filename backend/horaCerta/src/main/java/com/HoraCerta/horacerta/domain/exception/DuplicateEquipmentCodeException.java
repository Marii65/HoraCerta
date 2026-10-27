package com.HoraCerta.horacerta.domain.exception;

public class DuplicateEquipmentCodeException extends RuntimeException {
    public DuplicateEquipmentCodeException(String message) {
        super(message);
    }
}
