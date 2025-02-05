package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.RelationHelper;
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

    CombatEngine(@NotNull AlpinePlugin plugin) {
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
        if (potion.getEffects().stream().noneMatch(p -> BAD_EFFECTS.contains(XPotion.matchXPotion(p.getType())))) {
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

    private static boolean shouldCancelDamage(@NotNull Player hurtPlayer, @NotNull Player attackingPlayer) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.get().factions();
        ClaimAccessor claims = Factions.get().claims();

        // allow overriding players to damage other players
        if (PlayerHandler.getInstance().isOverriding(attackingPlayer)) {
            return false;
        }

        Faction hurtFaction = factions.findOrDefault(hurtPlayer);
        Faction attackingFaction = factions.findOrDefault(attackingPlayer);

        Faction hurtFactionAt = claims.getFactionOrDefault(hurtPlayer.getLocation());
        Faction attackingFactionAt = claims.getFactionOrDefault(attackingPlayer.getLocation());

        // ensure combat is allowed in both locations
        if (!hurtFactionAt.getFlagValueOrDefault(FactionFlags.COMBAT) || !attackingFactionAt.getFlagValueOrDefault(FactionFlags.COMBAT)) {
            config.combatDisabled.rateLimitedSend(attackingPlayer,
                    "faction", RelationHelper.formatLiteralFactionName(attackingPlayer, attackingFactionAt),
                    "faction_name", attackingFactionAt.getName());
            return true;
        }

        // allow player to hurt themselves (bow boosting, potion splash, etc...)
        if (hurtPlayer.equals(attackingPlayer)) {
            return false;
        }

        // you should always be able to hit wilderness players
        if (hurtFaction.isWilderness()) {
            return false;
        }

        // can't hurt neutral players in their own land
        if (attackingFaction.isRelation(hurtFaction, FactionRelation.NEUTRAL) && hurtFaction.equals(hurtFactionAt)) {
            config.cantHurtNeutral.rateLimitedSend(attackingPlayer,
                    "player", RelationHelper.formatLiteralPlayerName(attackingPlayer, hurtPlayer),
                    "player_name", hurtPlayer.getName(),
                    "attacker", RelationHelper.formatLiteralPlayerName(attackingPlayer, attackingPlayer),
                    "attacker_name", attackingPlayer.getName());
            config.attemptedDamage.rateLimitedSend(hurtPlayer,
                    "player", RelationHelper.formatLiteralPlayerName(hurtPlayer, hurtPlayer),
                    "player_name", hurtPlayer.getName(),
                    "attacker", RelationHelper.formatLiteralPlayerName(hurtPlayer, attackingPlayer),
                    "attacker_name", attackingPlayer.getName());
            return true;
        }
        FPlayer state = Factions.get().players().get(hurtPlayer);
        FPlayer targetState = Factions.get().players().get(attackingPlayer);

        // Allow friendly fire if toggled
        if (state.isFriendlyFire() && targetState.isFriendlyFire()) {
            return false;
        }

        // can't hurt your own faction members
        if (attackingFaction.isFriendly(hurtFaction)) {
            config.cantHurtFriendly.rateLimitedSend(attackingPlayer,
                    "player", RelationHelper.formatLiteralPlayerName(attackingPlayer, hurtPlayer),
                    "player_name", hurtPlayer.getName(),
                    "attacker", RelationHelper.formatLiteralPlayerName(attackingPlayer, attackingPlayer),
                    "attacker_name", attackingPlayer.getName());
            return true;
        }

        return false;
    }
}
