package io.github.humorousfool.everythingdirectors.directors;

import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DirectorManager
{
    private static final HashMap<Craft, Player> directors = new HashMap<>();
    private static final HashMap<Player, String> types = new HashMap<>();

    public static Player getDirector(Craft craft, String type)
    {
        if(directors.containsKey(craft))
        {
            Player player = directors.get(craft);
            if(types.containsKey(player) && types.get(player).equals(type))
                return player;
        }

        return null;
    }

    public static void setDirector(Craft craft, Player player, String type)
    {
        if(directors.containsKey(craft))
        {
            directors.replace(craft, player);
        }
        else
        {
            directors.put(craft, player);
        }

        if(types.containsKey(player))
        {
            types.replace(player, type);
        }
        else
        {
            types.put(player, type);
        }
    }
}
