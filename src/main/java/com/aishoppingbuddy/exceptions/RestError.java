package com.aishoppingbuddy.exceptions;

public record RestError(
    int cod,
    String message
) {}
