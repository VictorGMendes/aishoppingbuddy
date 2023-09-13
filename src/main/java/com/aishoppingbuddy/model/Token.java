package com.aishoppingbuddy.model;

public record Token(
        String token,
        String type,
        String prefix
) {
}
