package com.astrospace.dto;

public final class Requests {

    private Requests() {
    }

    public record Register(String name, String email, String password) {
    }

    public record Login(String email, String password) {
    }

    public record UpdateProfile(String name, String avatar) {
    }

    public record AddComment(String text) {
    }
}
