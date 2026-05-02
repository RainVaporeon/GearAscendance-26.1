package io.github.rainvaporeon.gearascendance;

import io.github.rainvaporeon.gearascendance.commands.GiveTemplateCommand;
import io.github.rainvaporeon.gearascendance.commands.UpdateItemCommand;
import io.github.rainvaporeon.gearascendance.handler.*;
import io.github.rainvaporeon.gearascendance.recipe.CraftingRecipeHandler;
import io.github.rainvaporeon.gearascendance.utils.NamespaceManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EntryPoint extends JavaPlugin {

    private static NamespacedKey CUSTOM_DATA;
    private static NamespacedKey ASCENDANCE_INFO;
    private static NamespacedKey FAKE_ITEM_INFO;
    private static NamespacedKey FAKE_ATTUNEMENT_ITEM_INFO;
    private static NamespacedKey FAKE_BLESSING_ITEM_INFO;
    private static JavaPlugin INSTANCE = null;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;

        CUSTOM_DATA = NamespaceManager.create("smithing_info");
        ASCENDANCE_INFO = NamespaceManager.create("ascendance_info");
        FAKE_ITEM_INFO = NamespaceManager.create("ascendance_prepare");
        FAKE_ATTUNEMENT_ITEM_INFO = NamespaceManager.create("ascendance_attune_fake");
        FAKE_BLESSING_ITEM_INFO = NamespaceManager.create("ascendance_blessing_fake");

        GiveTemplateCommand cmd = new GiveTemplateCommand();
        this.registerCommandExecutor("ascendance", cmd);
        this.registerCommandExecutor("ascendance_updateitem", new UpdateItemCommand());
        this.registerEvents(
                new GrindstoneHandler(),
                new SmithCompletionHandler(),
                new SmithingHandler(),
                new TemplateUpgradeHandler(),
                new TemplateBlessingHandler(),
                new CraftingRecipeHandler()
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void registerEvents(Listener... listeners) {
        for (Listener l : listeners) this.getServer().getPluginManager().registerEvents(l, this);
    }

    private void registerCommandExecutor(String name, CommandExecutor exec) {
        PluginCommand c = this.getCommand(name);
        if (c == null) {
            this.getLogger().warning(
                    String.format("Could not find command %s registered. Is it present in plugin.yml?", name)
            );
            return;
        }
        c.setExecutor(exec);
    }

    public static boolean isActive() {
        return INSTANCE.isEnabled();
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

    public static NamespacedKey getAscendanceTemplateInfoKey() {
        return CUSTOM_DATA;
    }

    public static NamespacedKey getItemAscendanceInfoKey() {
        return ASCENDANCE_INFO;
    }

    public static NamespacedKey getGeneratedAscendanceFakeItemKey() {
        return FAKE_ITEM_INFO;
    }

    public static NamespacedKey getGeneratedAttunementFakeItemKey() {
        return FAKE_ATTUNEMENT_ITEM_INFO;
    }

    public static NamespacedKey getFakeBlessingItemInfo() {
        return FAKE_BLESSING_ITEM_INFO;
    }
}
