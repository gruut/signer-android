package com.gruutnetworks.gruutsigner.gruut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergerList {

    public static final List<Merger> MERGER_LIST = Collections.unmodifiableList(
            new ArrayList<Merger>() {{
                //TEST local merger information //TEST-MERGER-ID-1TEST-MERGER-ID-1 // TEST-MERGER-ID-2TEST-MERGER-ID-2 // TEST-MERGER-ID-3TEST-MERGER-ID-3
                add(new Merger("6fxZPb2whbwVGUGSSwFW1FnmtqMaXptz8wq7A3dgLyG4","Merger #1", "10.10.10.117", 43243));
                add(new Merger("6fxZPb2whbwVGUGSSwFW1GLPPWKKwQzHZwfrEnsiELc1","Merger #2", "10.10.10.117", 50051));
                add(new Merger("6fxZPb2whbwVGUGSSwFW1GsztBH5M15azwWbKY7k7hwx","Merger #3", "10.10.10.117", 50052));
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
