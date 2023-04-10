package org.clokyy.combatlogborder;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Wood;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public final class CombatLogBorder extends JavaPlugin implements Listener {

    private ICombatLogX combatLogx;
    private WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "ENABLING COMBAT LOG");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Created and Updated by Clokyy (BigScaryMan#2495)");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Version 1.1");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
        combatLogx = (ICombatLogX) getServer().getPluginManager().getPlugin("CombatLogX");
        worldGuardPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void InCombat(PlayerTagEvent event){
        Player player = event.getPlayer();
        player.setMetadata("inCombat", new FixedMetadataValue(this, true));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        ProtectedRegion SpawnRegion = regionManager.getRegion("Spawn");
        ProtectedRegion WoodPvpDenyRegion = regionManager.getRegion("wood_pvp_deny");
        if(SpawnRegion != null && SpawnRegion.contains(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())) && player.hasMetadata("inCombat")){
            if(!player.hasMetadata("comatTeleported")){
                
                Location minLoc = BukkitAdapter.adapt(player.getWorld(), SpawnRegion.getMinimumPoint());

                Location safeLoc = minLoc.clone().add(ThreadLocalRandom.current().nextDouble(-5, 5), 0, ThreadLocalRandom.current().nextDouble(-5, 5));
                
                event.setTo(safeLoc);

                player.sendMessage(ChatColor.RED + "You are not allowed to enter SPAWN while combatlogged");
                player.setMetadata("combatTeleported", new FixedMetadataValue(this, true));
            }

        }else if(WoodPvpDenyRegion != null && WoodPvpDenyRegion.contains(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())) && player.hasMetadata("inCombat")){
            Location minLoc = BukkitAdapter.adapt(player.getWorld(), WoodPvpDenyRegion.getMinimumPoint());

            Location safeLoc = minLoc.clone().add(ThreadLocalRandom.current().nextDouble(-5, 5), 0, ThreadLocalRandom.current().nextDouble(-5, 5));

            event.setTo(safeLoc);

            player.sendMessage(ChatColor.RED + "You are not allowed to enter HERE while combatlogged");
            player.setMetadata("combatTeleported", new FixedMetadataValue(this, true));
        }else{
//            player.removeMetadata("");
        }
    }


    @EventHandler
    public void NotInCombat(PlayerUntagEvent event){
        Player player = event.getPlayer();
        player.removeMetadata("inCombat", this);
    }
}
