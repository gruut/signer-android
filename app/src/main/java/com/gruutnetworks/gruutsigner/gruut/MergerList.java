package com.gruutnetworks.gruutsigner.gruut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergerList {

    public static final List<Merger> MERGER_LIST = Collections.unmodifiableList(
            new ArrayList<Merger>() {{
                add(new Merger("Merger #1", "165.246.42.47", 43801));
                add(new Merger("Merger #2", "165.246.42.47", 43802));
                add(new Merger("Merger #3", "165.246.42.47", 43803));
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
