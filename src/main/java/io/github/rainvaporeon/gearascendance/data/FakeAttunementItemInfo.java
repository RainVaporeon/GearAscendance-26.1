package io.github.rainvaporeon.gearascendance.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rainvaporeon.gearascendance.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

public record FakeAttunementItemInfo(
        int tier,
        // int pity,
        boolean blessed,
        Enchantment target,
        double successProbability
) implements Attachable {
    public static final FakeAttunementItemInfo NONE = new FakeAttunementItemInfo(
            0, /* 0, */ false, null, 0
    );

    @Override
    public NamespacedKey key() {
        return EntryPoint.getGeneratedAttunementFakeItemKey();
    }

    public String toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("tier", tier);
        // jo.addProperty("pity", pity);
        jo.addProperty("blessed", blessed);
        if (target == null) {
            jo.addProperty("target", "null");
        } else {
            jo.addProperty("target", String.valueOf(target.getKeyOrNull()));
        }
        jo.addProperty("chance", successProbability);
        return jo.toString();
    }

    public static FakeAttunementItemInfo fromJson(String json) {
        if (json == null || json.isBlank()) return NONE;
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        int tier = jo.get("tier").getAsInt();
        // int pity = jo.get("pity").getAsInt();
        boolean blessed = jo.get("blessed").getAsBoolean();
        String target = jo.get("target").getAsString();
        double chance = jo.get("chance").getAsDouble();

        if ("null".equals(target) || target.isBlank()) {
            return new FakeAttunementItemInfo(tier, /* pity, */ blessed,null, chance);
        } else {
            return new FakeAttunementItemInfo(tier, /* pity, */ blessed, Registry.ENCHANTMENT.get(NamespacedKey.fromString(target)), chance);
        }
    }
}
