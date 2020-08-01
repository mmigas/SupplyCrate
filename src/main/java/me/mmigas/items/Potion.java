package me.mmigas.items;

import org.bukkit.potion.PotionEffectType;

import static me.mmigas.utils.Comparisons.equalsOne;
import static me.mmigas.utils.Comparisons.startsAndEnds;

public class Potion {

    private Potion() {

    }

    public static PotionEffectType byName(String name) {
        name = name.toLowerCase();
        if (equalsOne(name, "speed", "swiftness")) {
            return PotionEffectType.SPEED;
        } else if (equalsOne(name, "slow", "slowness")) {
            return PotionEffectType.SLOW;
        } else if (equalsOne(name, "fast_diggin")) {
            return PotionEffectType.FAST_DIGGING;
        } else if (equalsOne(name, "slow_diggin")) {
            return PotionEffectType.SLOW_DIGGING;
        } else if (equalsOne(name, "strength", "damage") || startsAndEnds(name, "increase", "damage")) {
            return PotionEffectType.INCREASE_DAMAGE;
        } else if (equalsOne(name, "heal", "healing") || startsAndEnds(name, "instant", "healing") || startsAndEnds(name, "instant", "heal")) {
            return PotionEffectType.HEAL;
        } else if (equalsOne(name, "harm", "harming") || startsAndEnds(name, "instant", "damage")) {
            return PotionEffectType.HARM;
        } else if (equalsOne(name, "leaping", "jump")) {
            return PotionEffectType.JUMP;
        } else if (equalsOne(name, "confusion")) {
            return PotionEffectType.CONFUSION;
        } else if (equalsOne(name, "regen", "regeneration")) {
            return PotionEffectType.REGENERATION;
        } else if (equalsOne(name, "resistance") || startsAndEnds(name, "damage", "resistance")) {
            return PotionEffectType.DAMAGE_RESISTANCE;
        } else if (equalsOne(name, "fire") || startsAndEnds(name, "fire", "resistance")) {
            return PotionEffectType.FIRE_RESISTANCE;
        } else if (equalsOne(name, "breathing") || startsAndEnds(name, "water", "breathing")) {
            return PotionEffectType.WATER_BREATHING;
        } else if (equalsOne(name, "invisibility")) {
            return PotionEffectType.INVISIBILITY;
        } else if (equalsOne(name, "blindness")) {
            return PotionEffectType.BLINDNESS;
        } else if (startsAndEnds(name, "night", "vision")) {
            return PotionEffectType.NIGHT_VISION;
        } else if (equalsOne(name, "hunger")) {
            return PotionEffectType.HUNGER;
        } else if (equalsOne(name, "weakness")) {
            return PotionEffectType.WEAKNESS;
        } else if (equalsOne(name, "poison")) {
            return PotionEffectType.POISON;
        } else if (equalsOne(name, "wither")) {
            return PotionEffectType.WITHER;
        } else if (startsAndEnds(name, "health", "boost")) {
            return PotionEffectType.HEALTH_BOOST;
        } else if (equalsOne(name, "absorption")) {
            return PotionEffectType.ABSORPTION;
        } else if (equalsOne(name, "saturarion")) {
            return PotionEffectType.SATURATION;
        } else if (equalsOne(name, "glowing")) {
            return PotionEffectType.GLOWING;
        } else if (equalsOne(name, "luck")) {
            return PotionEffectType.LUCK;
        } else if (equalsOne(name, "unluck")) {
            return PotionEffectType.UNLUCK;
        } else if (startsAndEnds(name, "slow", "falling") || startsAndEnds(name, "feather", "falling")) {
            return PotionEffectType.SLOW_FALLING;
        } else if (equalsOne(name, "conduit_power")) {
            return PotionEffectType.CONDUIT_POWER;
        } else if (equalsOne(name, "dolphins_grace")) {
            return PotionEffectType.DOLPHINS_GRACE;
        } else if (equalsOne(name, "bad_omen")) {
            return PotionEffectType.BAD_OMEN;
        } else if (equalsOne(name, "hero_of_the_village", "hero")) {
            return PotionEffectType.HERO_OF_THE_VILLAGE;
        }
        return null;
    }
}