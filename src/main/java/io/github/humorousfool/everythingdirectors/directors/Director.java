package io.github.humorousfool.everythingdirectors.directors;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public abstract class Director implements Listener
{
    protected String signName;
    protected String permissionName;
    protected String title;

    protected Director(String title, String signName, String permissionName)
    {
        this.signName = signName;
        this.permissionName = permissionName;
        this.title = title;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if(!event.getClickedBlock().getType().name().endsWith("SIGN"))  return;

        Sign sign = (Sign) event.getClickedBlock().getState();
        if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(signName))
        {
            if (!event.getPlayer().hasPermission("everythingdirectors." + permissionName + ".direct"))
            {
                event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                return;
            }
            Craft c = null;
            for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(event.getClickedBlock().getWorld())) {
                if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(event.getClickedBlock().getLocation())) &&
                        CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                    c = tcraft;
                    break;
                }
            }
            if (c == null)
            {
                event.getPlayer().sendMessage("Director sign must be part of a piloted craft!");
                return;
            }

            event.getPlayer().sendMessage("You are now directing the " + title + " of this craft.");
            DirectorManager.setDirector(c, event.getPlayer(), permissionName);
        }
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event)
    {
        if(event.getLine(0).equalsIgnoreCase(signName) && !event.getPlayer().hasPermission("everythingdirectors." + permissionName + ".place"))
            event.setCancelled(true);
    }
}
