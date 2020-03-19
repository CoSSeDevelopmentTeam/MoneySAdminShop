package net.comorevi.moneysadminshop.util;

import cn.nukkit.Player;
import cn.nukkit.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataCenter {
    private static Map<Player, Block> editCmdQueue = new LinkedHashMap<>();
    public static void addEditCmdQueue(Player player) {
        editCmdQueue.put(player, null);
    }

    public static void addEditCmdQueue(Player player, Block block) {
        editCmdQueue.put(player, block);
    }

    public static void removeEditCmdQueue(Player player) {
        editCmdQueue.remove(player);
    }

    public static Block getRegisteredBlockByEditCmdQueue(Player player) {
        return editCmdQueue.get(player);
    }

    public static boolean existsEditCmdQueue(Player player) {
        return editCmdQueue.containsKey(player);
    }
}
