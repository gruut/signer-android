package com.gruutnetworks.gruutsigner.gruut;

public class Merger {
    private String uri;
    private int port;

    public Merger(String uri, int port) {
        this.uri = uri;
        this.port = port;
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