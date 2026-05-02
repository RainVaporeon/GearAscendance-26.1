package io.github.rainvaporeon.gearascendance.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rainvaporeon.gearascendance.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public record FakeAscendanceItemInfo(
        int nextTier,
        List<Enchantment> upgradeCandidates,
        Enchantment attunement,
        boolean blessed,
        double successProbability
) implements Attachable {
    public static final FakeAscendanceItemInfo NONE = new FakeAscendanceItemInfo(
            -1,
            List.of(),
            null,
            false,
            0
    );

    @Override
    public NamespacedKey key() {
        return EntryPoint.getGeneratedAscendanceFakeItemKey();
    }

    public String toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("nextTier", nextTier);
        jo.addProperty("blessed", blessed);
        jo.addProperty("chance", successProbability);
        if (attunement == null) {
            jo.addProperty("attune", "null");
        } else {
            jo.addProperty("attune", String.valueOf(attunement.getKeyOrNull()));
        }
        JsonArray ja = new JsonArray();
        upgradeCandidates.forEach(en -> ja.add(String.valueOf(en.getKeyOrNull())));
        jo.add("candidates", ja);
        return jo.toString();
    }

    public static FakeAscendanceItemInfo fromJson(String json) {
        if (json == null) return NONE;
        try {
            JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
            List<Enchantment> enchantments = new ArrayList<>();
            String attunement = payload.get("attune").getAsString();
            Enchantment attune = attunement.equals("null") ? null : Registry.ENCHANTMENT.get(NamespacedKey.fromString(payload.get("attune").getAsString()));
            payload.getAsJsonArray("candidates").forEach(je -> {
                if (je.getAsString().equals("null") || je.getAsString().isBlank()) return;
                enchantments.add(Registry.ENCHANTMENT.get(NamespacedKey.fromString(je.getAsString())));
            });
            return new FakeAscendanceItemInfo(
                    payload.get("nextTier").getAsInt(),
                    enchantments,
                    attune,
                    payload.get("blessed").getAsBoolean(),
                    payload.get("chance").getAsDouble()
            );
        } catch (RuntimeException e) {
            return NONE;
        }
    }
}
