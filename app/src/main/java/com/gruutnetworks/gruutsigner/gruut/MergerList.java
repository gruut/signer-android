package com.gruutnetworks.gruutsigner.gruut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergerList {

    public static final List<Merger> MERGER_LIST = Collections.unmodifiableList(
            new ArrayList<Merger>() {{
                add(new Merger("10.10.10.112", 9090));
            }}
    );
}
