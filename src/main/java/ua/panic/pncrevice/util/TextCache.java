package ua.panic.pncrevice.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TextCache {
    private final MiniMessage mm;
    private final Map<String, Component> cache = new ConcurrentHashMap<>();

    public TextCache(MiniMessage mm) {
        this.mm = mm;
    }

    public Component toComponent(String raw){
        String normalized = normalize(raw);
        Component cached = cache.get(normalized);
        if (cached != null) return cached;
        CompletableFuture.runAsync(() -> cache.computeIfAbsent(normalized, k -> mm.deserialize(k)));
        return mm.deserialize(normalized);
    }

    private String normalize(String s){
        if (s == null) return "";
        return s
                .replace("§x§", "§x")
                .replace("&x&", "&x");
    }
}
