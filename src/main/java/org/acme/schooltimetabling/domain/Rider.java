package org.acme.schooltimetabling.domain;

public class Rider {

    private String name;

    public Rider(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

}
