package com.gruutnetworks.gruutsigner.gruut;

public interface GruutConfigs {
    byte gruutConstant = 'G';
    byte mainVersion = 0x01;
    byte subVersion = 0x00;
    String ver = "1.0.20181127";
    String localChainId = "R0VOVEVTVDE="; // GENTEST1

    int GRPC_TIMEOUT = 10;
}