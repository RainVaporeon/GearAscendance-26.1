package io.github.rainvaporeon.utils;

import io.github.rainvaporeon.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class NamespaceManager {
    private static NamespaceManager instance;

    private final JavaPlugin plugin;

    static {
        instance = new NamespaceManager(EntryPoint.getInstance());
    }

    private NamespaceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static NamespacedKey create(String value) {
        return new NamespacedKey(instance.plugin, value);
    }

    public static NamespaceManager getInstance() {
        return instance;
    }
}
