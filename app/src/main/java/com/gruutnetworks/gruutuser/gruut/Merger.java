package com.gruutnetworks.gruutuser.gruut;

public class Merger {
    private String name;
    private String uri;
    private int port;
    private String b58Id;

    public Merger(String b58Id, String name, String uri, int port) {
        this.name = name;
        this.uri = uri;
        this.port = port;
        this.b58Id = b58Id;
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

    public String getB58Id(){
        return this.b58Id;
    }

    public void setB58Id(String b58Id) {
        this.b58Id = b58Id;
    }

}