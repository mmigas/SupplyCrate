package me.mmigas.items;

import org.bukkit.enchantments.Enchantment;

import static me.mmigas.utils.Comparisons.equalsOne;
import static me.mmigas.utils.Comparisons.startsAndEnds;

public class Enchantments {
    /**
     * Private constructor to hide the implicit public one.
     */
    private Enchantments() {
        // Should be empty.
    }

    /**
     * Finds an enchantment by its name.
     *
     * @param name the name of the enchantment.
     * @return the Enchantment or null if it doesn't exist.
     */
    public static Enchantment byName(String name) {
        name = name.toLowerCase();
        if(equalsOne(name, "sharpness", "damage", "damage_all")) {
            return Enchantment.DAMAGE_ALL;
        } else if(equalsOne(name, "unbreaking", "durability")) {
            return Enchantment.DURABILITY;
        } else if(startsAndEnds(name, "fire", "aspect")) {
            return Enchantment.FIRE_ASPECT;
        } else if(equalsOne(name, "smite", "damage_undead")) {
            return Enchantment.DAMAGE_UNDEAD;
        } else if(name.equalsIgnoreCase("knockback")) {
            return Enchantment.KNOCKBACK;
        } else if(name.equalsIgnoreCase("efficiency") || startsAndEnds(name, "dig", "speed")) {
            return Enchantment.DIG_SPEED;
        } else if(equalsOne(name, "fortune", "loot_bonus_blocks")) {
            return Enchantment.LOOT_BONUS_BLOCKS;
        } else if(equalsOne(name, "loot", "loot_bonus_mobs", "looting")) {
            return Enchantment.LOOT_BONUS_MOBS;
        } else if(equalsOne(name, "protection", "protection_environmental")) {
            return Enchantment.PROTECTION_ENVIRONMENTAL;
        } else if(name.equalsIgnoreCase("thorns")) {
            return Enchantment.THORNS;
        } else if(equalsOne(name, "respiration", "oxygen")) {
            return Enchantment.OXYGEN;
        } else if(startsAndEnds(name, "aqua", "affinity") || startsAndEnds(name, "water", "worker")) {
            return Enchantment.WATER_WORKER;
        } else if(startsAndEnds(name, "depth", "strider")) {
            return Enchantment.DEPTH_STRIDER;
        } else if(startsAndEnds(name, "feather", "falling") || startsAndEnds(name, "protection", "fall")) {
            return Enchantment.PROTECTION_FALL;
        } else if(equalsOne(name, "power")) {
            return Enchantment.ARROW_DAMAGE;
        } else if(equalsOne(name, "punch")) {
            return Enchantment.ARROW_KNOCKBACK;
        }

        return null;
    }
}
