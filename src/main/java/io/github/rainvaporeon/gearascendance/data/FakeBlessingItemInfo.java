package io.github.rainvaporeon.gearascendance.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rainvaporeon.gearascendance.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public record FakeBlessingItemInfo(
        int tier,
        double successProbability,
        Enchantment attune
) implements Attachable {
    public static final FakeBlessingItemInfo NONE = new FakeBlessingItemInfo(
            0, 0, null
    );

    @Override
    public String toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("tier", tier);
        jo.addProperty("chance", successProbability);
        if (attune == null) {
            jo.addProperty("attune", "null");
        } else {
            jo.addProperty("attune", String.valueOf(attune.getKeyOrNull()));
        }
        return jo.toString();
    }

    public static FakeBlessingItemInfo fromItemStack(ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) return NONE;
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        return FakeBlessingItemInfo.fromJson(
                meta.getPersistentDataContainer().get(
                        EntryPoint.getFakeBlessingItemInfo(),
                        PersistentDataType.STRING
                )
        );
    }

    public static FakeBlessingItemInfo fromJson(String json) {
        if (json == null || json.isBlank()) return NONE;
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        int tier = jo.get("tier").getAsInt();
        double chance = jo.get("chance").getAsDouble();
        String tag = jo.get("attune").getAsString();
        if ("null".equals(tag) || tag.isBlank()) {
            return new FakeBlessingItemInfo(tier, chance, null);
        } else {
            return new FakeBlessingItemInfo(tier, chance, Registry.ENCHANTMENT.get(NamespacedKey.fromString(tag)));
        }

    }

    @Override
    public NamespacedKey key() {
        return EntryPoint.getFakeBlessingItemInfo();
    }
}
