package com.gruutnetworks.gruutsigner.gruut;

public class Merger {
    private String name;
    private String uri;
    private int port;

    public Merger(String name, String uri, int port) {
        this.name = name;
        this.uri = uri;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}