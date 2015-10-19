package io.github.cosmicdrift.cosmicdrift.dataio;

import java.util.Collections;
import java.util.List;

public class CompoundHeadedList<Head, Body> {

    public final Head head;
    public final List<Body> body;

    public CompoundHeadedList(Head head, List<Body> body) {
        this.head = head;
        this.body = Collections.unmodifiableList(body);
    }
}
