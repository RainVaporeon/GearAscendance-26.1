package io.github.rainvaporeon;

import io.github.rainvaporeon.commands.GiveTemplateCommand;
import io.github.rainvaporeon.handler.GrindstoneHandler;
import io.github.rainvaporeon.handler.SmithCompletionHandler;
import io.github.rainvaporeon.handler.SmithingHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class EntryPoint extends JavaPlugin {

    private static NamespacedKey CUSTOM_DATA;
    private static NamespacedKey ASCENDANCE_INFO;
    private static NamespacedKey FAKE_ITEM_INFO;
    private static JavaPlugin INSTANCE = null;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
        CUSTOM_DATA = new NamespacedKey(this, "smithing_info");
        ASCENDANCE_INFO = new NamespacedKey(this, "ascendance_info");
        FAKE_ITEM_INFO = new NamespacedKey(this, "ascendance_prepare");

        this.getCommand("ascendance").setExecutor(new GiveTemplateCommand());
        this.getServer().getPluginManager().registerEvents(
                new GrindstoneHandler(), this
        );
        this.getServer().getPluginManager().registerEvents(
                new SmithCompletionHandler(), this
        );
        this.getServer().getPluginManager().registerEvents(
                new SmithingHandler(), this
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static boolean isActive() {
        return INSTANCE.isEnabled();
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

    public static NamespacedKey getSmithingInfoKey() {
        return CUSTOM_DATA;
    }

    public static NamespacedKey getAscendanceInfoKey() {
        return ASCENDANCE_INFO;
    }

    public static NamespacedKey getFakeItemInfo() {
        return FAKE_ITEM_INFO;
    }
}
