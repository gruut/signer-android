package com.gruutnetworks.gruutsigner.gruut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergerList {

    public static final List<Merger> MERGER_LIST = Collections.unmodifiableList(
            new ArrayList<Merger>() {{
                add(new Merger("Merger #1", "13.125.161.227", 50000));
                add(new Merger("Merger #2", "13.125.84.32", 50000));
                add(new Merger("Merger #3", "13.209.158.245", 50000));
            }}
    );

    public static Merger findPresetMergerList(String uri, int port) {
        for (Merger merger : MERGER_LIST) {
            if (merger.getUri().equals(uri) && merger.getPort() == port) {
                return merger;
            }
        }
        return null;
    }
}
