package io.github.humorousfool.everythingdirectors.directors;

import io.github.humorousfool.everythingdirectors.Config;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class ProjectileDirector extends Director
{
    protected List<EntityType> entityTypes;

    public ProjectileDirector(String title, String signName, String permissionName, EntityType... entityTypes)
    {
        super(title, signName, permissionName);
        this.entityTypes = Arrays.asList(entityTypes);
    }

    @EventHandler
    public void onDispense(ProjectileLaunchEvent event)
    {
        if(!entityTypes.contains(event.getEntityType()) || !(event.getEntity().getShooter() instanceof BlockProjectileSource)) return;

        BlockProjectileSource blockSource = (BlockProjectileSource) event.getEntity().getShooter();
        if(blockSource.getBlock().getType() != Material.DISPENSER) return;
        Block block = blockSource.getBlock();

        Craft c = null;
        for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(block.getWorld())) {
            if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(block.getLocation())) &&
                    CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                c = tcraft;
                break;
            }
        }
        if(c == null) return;

        Player player = DirectorManager.getDirector(c, permissionName);
        if(player == null || !player.isOnline()) return;

        Projectile projectile = event.getEntity();

        if (player.getInventory().getItemInMainHand().getType() != Config.DirectorTool)
            return;

        Vector fv = projectile.getVelocity();
        double speed = fv.length(); // store the speed to add it back in later, since all the values we will be using are "normalized", IE: have a speed of 1
        fv = fv.normalize(); // you normalize it for comparison with the new direction to see if we are trying to steer too far
        Block targetBlock = player.rayTraceBlocks(250, FluidCollisionMode.NEVER).getHitBlock();
        Vector targetVector;

        if (targetBlock == null) { // the player is looking at nothing, shoot in that general direction
            targetVector = player.getLocation().getDirection();
        } else { // shoot directly at the block the player is looking at (IE: with convergence)
            targetVector = targetBlock.getLocation().toVector().subtract(projectile.getLocation().toVector());
            targetVector = targetVector.normalize();
        }

        if (targetVector.getX() - fv.getX() > 0.5) {
            fv.setX(fv.getX() + 0.5);
        } else if (targetVector.getX() - fv.getX() < -0.5) {
            fv.setX(fv.getX() - 0.5);
        } else {
            fv.setX(targetVector.getX());
        }

        if (targetVector.getY() - fv.getY() > 0.5) {
            fv.setY(fv.getY() + 0.5);
        } else if (targetVector.getY() - fv.getY() < -0.5) {
            fv.setY(fv.getY() - 0.5);
        } else {
            fv.setY(targetVector.getY());
        }

        if (targetVector.getZ() - fv.getZ() > 0.5) {
            fv.setZ(fv.getZ() + 0.5);
        } else if (targetVector.getZ() - fv.getZ() < -0.5) {
            fv.setZ(fv.getZ() - 0.5);
        } else {
            fv.setZ(targetVector.getZ());
        }

        fv = fv.multiply(speed); // put the original speed back in, but now along a different trajectory
        projectile.setVelocity(fv);
    }
}
