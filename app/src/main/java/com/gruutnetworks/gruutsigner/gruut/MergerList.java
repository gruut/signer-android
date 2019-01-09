package com.gruutnetworks.gruutsigner.gruut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergerList {

    public static final List<Merger> MERGER_LIST = Collections.unmodifiableList(
            new ArrayList<Merger>() {{
                add(new Merger("13.125.161.227", 50000));
                add(new Merger("13.125.84.32", 50000));
                add(new Merger("13.209.158.245", 50000));
            }}
    );
}
