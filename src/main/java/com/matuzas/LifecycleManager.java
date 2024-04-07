package com.matuzas;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LifecycleManager {
    private static final List<Lifecycle> LIFECYCLES = new ArrayList<>();

    public void add(Lifecycle lifecycle) {
        LIFECYCLES.add(lifecycle);
    }

    public void startAll() throws Exception {
        for (var lifecycle : LIFECYCLES) {
            lifecycle.start();
        }
    }

    public void stopAll() {
        // Reverse stopping
        for (int i = LIFECYCLES.size() - 1; i >= 0; i--) {
            try {
                LIFECYCLES.get(i).stop();
            } catch (Exception ex) {
                log.error("Error during lifecycle stop: {}", ex.getMessage());
            }
        }
    }
}
