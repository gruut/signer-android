package com.gruutnetworks.gruutsigner.gruut;

public interface GruutConfigs {
    byte gruutConstant = 'P';
    byte version = 0x01;
    //temp id;
    String localChainId = "LCHAINID";
    String worldId = "WORLD-ID";

    int GRPC_TIMEOUT = 10;
    int AUTO_REFRESH_TIMEOUT = 10 * 1000;
}