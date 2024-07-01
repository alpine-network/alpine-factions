package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import com.cryptomorin.xseries.XPotion;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

/**
 * @since 0.1.0
 */
public final class CombatEngine extends AlpineEngine {

    private static final Set<XPotion> BAD_EFFECTS = ImmutableSet.of(
            XPotion.BAD_OMEN,
            XPotion.BLINDNESS,
            XPotion.NAUSEA,
            XPotion.DARKNESS,
            XPotion.INSTANT_DAMAGE,
            XPotion.HUNGER,
            XPotion.JUMP_BOOST,
            XPotion.LEVITATION,
            XPotion.POISON,
            XPotion.SLOWNESS,
            XPotion.MINING_FATIGUE,
            XPotion.SLOW_FALLING,
            XPotion.SPEED,
            XPotion.UNLUCK,
            XPotion.WEAKNESS,
            XPotion.WITHER
    );

    CombatEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerHitPlayer(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Entity damagerEntity = event.getDamager();
        Player damager = null;

        if (damagerEntity instanceof Projectile) {
            Projectile projectile = (Projectile) damagerEntity;
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player) {
                damager = (Player) source;
            }
        }
        else if (damagerEntity instanceof Player) {
            damager = (Player) damagerEntity;
        }

        if (damager != null && shouldCancelDamage(player, damager)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCombust(EntityCombustByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity combusterEntity = event.getCombuster();
        if (!(entity instanceof Player) || !(combusterEntity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Player combuster = (Player) combusterEntity;

        if (shouldCancelDamage(player, combuster)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ProjectileSource source = potion.getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        // ensure the effect was bad
        if (potion.getEffects().stream().anyMatch(p -> BAD_EFFECTS.contains(XPotion.matchXPotion(p.getType())))) {
            return;
        }

        // check that all players can be damaged
        Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            if (!(entity instanceof Player)) {
                continue;
            }

            Player player = (Player) entity;
            if (shouldCancelDamage(player, (Player) source)) {
                iterator.remove();
            }
        }

        if (event.getAffectedEntities().isEmpty()) {
            event.setCancelled(true);
        }
    }

    private static boolean shouldCancelDamage(@NotNull Player attacker, @NotNull Player target) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Factions.get().factions();
        ClaimAccessor claims = Factions.get().claims();

        // allow overriding players to damage other players
        if (PlayerHandler.getInstance().isOverriding(target)) {
            return false;
        }

        Faction attackerFaction = factions.findOrDefault(attacker);
        Faction targetFaction = factions.findOrDefault(target);

        Faction attackerClaimFaction = claims.getFactionOrDefault(attacker.getLocation());
        Faction targetClaimFaction = claims.getFactionOrDefault(target.getLocation());

        // ensure combat is allowed in both locations
        if (!attackerClaimFaction.getFlagValueOrDefault(FactionFlags.COMBAT) || !targetClaimFaction.getFlagValueOrDefault(FactionFlags.COMBAT)) {
            config.combatDisabled.send(target,
                    "faction", FactionHelper.formatRelational(target, targetClaimFaction, false),
                    "faction_name", targetClaimFaction.getName());
            return true;
        }

        // allow player to hurt themselves (bow boosting, potion splash, etc...)
        if (attacker.equals(target)) {
            return false;
        }

        // you should always be able to hit wilderness players
        if (targetFaction.isWilderness()) {
            return false;
        }

        // can't hurt neutral players in their own land
        if (targetFaction.isRelation(attackerFaction, FactionRelation.NEUTRAL) && attackerFaction.equals(attackerClaimFaction)) {
            config.cantHurtNeutral.send(target,
                    "player", FactionHelper.formatRelational(target, attackerFaction, attacker, false),
                    "player_name", attacker.getName(),
                    "attacker", FactionHelper.formatRelational(target, targetFaction, target, false),
                    "attacker_name", target.getName());
            config.attemptedDamage.send(attacker,
                    "player", FactionHelper.formatRelational(attacker, attackerFaction, attacker, false),
                    "player_name", attacker.getName(),
                    "attacker", FactionHelper.formatRelational(attacker, targetFaction, target, false),
                    "attacker_name", target.getName());
            return true;
        }

        // can't hurt your own faction members,
        if (targetFaction.isFriendly(attackerFaction)) {
            config.cantHurtFriendly.send(target,
                    "player", FactionHelper.formatRelational(target, attackerFaction, attacker, false),
                    "player_name", attacker.getName(),
                    "attacker", FactionHelper.formatRelational(target, targetFaction, target, false),
                    "attacker_name", target.getName());
            return true;
        }

        return false;
    }
}
