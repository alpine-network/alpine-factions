package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.api.accessor.Accessors;
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
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/12/2024
 */
public final class CombatEngine extends AlpineEngine {

    private static final Set<XPotion> BAD_EFFECTS = ImmutableSet.of(
            XPotion.BAD_OMEN,
            XPotion.BLINDNESS,
            XPotion.CONFUSION,
            XPotion.DARKNESS,
            XPotion.HARM,
            XPotion.HUNGER,
            XPotion.INCREASE_DAMAGE,
            XPotion.JUMP,
            XPotion.LEVITATION,
            XPotion.POISON,
            XPotion.SLOW,
            XPotion.SLOW_DIGGING,
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

        if (damager != null && this.shouldCancelDamage(player, damager)) {
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

        if (this.shouldCancelDamage(player, combuster)) {
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
            if (this.shouldCancelDamage(player, (Player) source)) {
                iterator.remove();
            }
        }

        if (event.getAffectedEntities().isEmpty()) {
            event.setCancelled(true);
        }
    }

    private boolean shouldCancelDamage(@NotNull Player player, @NotNull Player damager) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        ClaimAccessor claims = Accessors.claims();

        // allow overriding players to damage other players
        if (PlayerHandler.getInstance().isOverriding(damager)) {
            return false;
        }

        Faction playerFaction = factions.findOrDefault(player);
        Faction damagerFaction = factions.findOrDefault(damager);

        Faction playerClaimFaction = claims.getFactionOrDefault(player.getLocation());
        Faction damagerClaimFaction = claims.getFactionOrDefault(damager.getLocation());

        // ensure combat is allowed in both locations
        if (!playerClaimFaction.getFlagValueOrDefault(FactionFlags.COMBAT) || !damagerClaimFaction.getFlagValueOrDefault(FactionFlags.COMBAT)) {
            config.combatDisabled.send(damager,
                    "faction", FactionHelper.formatRelational(damager, damagerClaimFaction, false),
                    "faction_name", damagerClaimFaction.getName());
            return true;
        }

        // allow player to hurt themselves (bow boosting, potion splash, etc...)
        if (player.equals(damager)) {
            return false;
        }

        // can't hurt neutral players in their own land
        if (damagerFaction.isRelation(playerFaction, FactionRelation.NEUTRAL) && playerFaction.equals(playerClaimFaction)) {
            config.cantHurtNeutral.send(damager,
                    "player", FactionHelper.formatRelational(damager, playerFaction, player, false),
                    "player_name", player.getName(),
                    "attacker", FactionHelper.formatRelational(damager, damagerFaction, damager, false),
                    "attacker_name", damager.getName());
            config.attemptedDamage.send(player,
                    "player", FactionHelper.formatRelational(player, playerFaction, player, false),
                    "player_name", player.getName(),
                    "attacker", FactionHelper.formatRelational(player, damagerFaction, damager, false),
                    "attacker_name", damager.getName());
            return true;
        }

        // can't hurt your own faction members,
        if (damagerFaction.isFriendly(playerFaction)) {
            config.cantHurtFriendly.send(damager,
                    "player", FactionHelper.formatRelational(damager, playerFaction, player, false),
                    "player_name", player.getName(),
                    "attacker", FactionHelper.formatRelational(damager, damagerFaction, damager, false),
                    "attacker_name", damager.getName());
            return true;
        }

        return false;
    }
}
