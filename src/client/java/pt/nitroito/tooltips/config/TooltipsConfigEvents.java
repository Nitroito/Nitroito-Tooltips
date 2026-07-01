package pt.nitroito.tooltips.config;

import java.util.List;
import java.util.ArrayList;

public final class TooltipsConfigEvents {
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    public static void register(Runnable listener) {
        LISTENERS.add(listener);
    }

    public static void fire() {
        for (Runnable r:LISTENERS) {
            r.run();
        }
    }
}
