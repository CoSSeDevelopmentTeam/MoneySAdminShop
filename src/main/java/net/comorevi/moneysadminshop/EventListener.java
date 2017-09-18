package net.comorevi.moneysadminshop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public class EventListener implements Listener {

    private MoneySAdminShop plugin;

    public EventListener(MoneySAdminShop plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	Block block = event.getBlock();
    	if(isSign(block)) {
    		String[] line = getSignText(block);
    		Player player = event.getPlayer();
    		if(line[0].equals("[" + MoneySAdminShop.shopTitle + "]")) {
    			if(player.isOp()) {
    				player.sendMessage(TextValues.INFO + plugin.translateString("shop-removed"));
    			} else {
    				event.setCancelled();
    				player.sendMessage(TextValues.ALERT + plugin.translateString("player-isNotOP"));
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
    	Player player = event.getPlayer();
    	String[] line = event.getLines();
    	if(line[0].equals("ashop")) {
    		if(player.isOp()) {
    			String[] line1 = line.toString().split(":");
	    		if(isNumber(line1[0]) || isNumber(line1[1]) || isNumber(line[2]) || isNumber(line[3])) {
	    			String itemname;
	    			if(line1[1].isEmpty()) {
	    				Item item = Item.get(Integer.parseInt(line1[0]));
	    				itemname = item.getName();
	    			} else {
	    				Item item = Item.get(Integer.parseInt(line1[0]), Integer.parseInt(line1[1]));
	    				itemname = item.getName();
	    			}
	    			event.setLine(0, "[" + TextFormat.AQUA + MoneySAdminShop.shopTitle + "]");
	    			event.setLine(1, itemname + ", " + line1[0] + ":" + line1[1]);
	    			event.setLine(2, "取引量: " + line[3]);
	    			event.setLine(3, "値段: " + line[2]);
	    			player.sendMessage(TextValues.INFO + plugin.translateString("shop-create"));
	    		} else {
	    			player.sendMessage(TextValues.WARNING + plugin.translateString("error-all"));
	    		}
    		} else {
    			player.sendMessage(TextValues.INFO + plugin.translateString("player-isNotOP"));
    		}
    	} else if(line[0].equals("[" + MoneySAdminShop.shopTitle + "]")) {
    		event.setCancelled();
    		player.sendMessage(TextValues.ALERT + plugin.translateString("error-all"));
    	}
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	String username = player.getName();
    	Block block= event.getBlock();
    	if(isSign(block)) {
    		String[] line = getSignText(block);
    		if(line[0].equals("[" + MoneySAdminShop.shopTitle + "]")) {
    			String[] line3 = line[3].split(": ");
    			int price = Integer.parseInt(line3[1]);
    			int money = MoneySAdminShop.money.getMoney(player);
    			if(price < money) {
    				String[] line1text = line[1].split(", ");
    				String[] line1 = line1text[1].split(":");
    				int id = Integer.parseInt(line1[0]);
    				int meta = Integer.parseInt(line1[1]);
    				String[] line2 = line[2].split(": ");
    				int count = Integer.parseInt(line2[1]);
    				if(player.getInventory().canAddItem(Item.get(id, meta, count))) {
    					player.getInventory().addItem(Item.get(id, meta, count));
    					MoneySAdminShop.money.reduceMoney(username, price);
    					player.sendMessage(TextValues.INFO + plugin.translateString("shop-buy", line1text[0], String.valueOf(count), String.valueOf(price), MoneySAdminShop.money.getMoneyUnit()));
    				} else {
    					player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy1"));
    				}
    			} else {
    				player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy2"));
    			}
    		}
    	}
    }
    
    public String[] getSignText(Block block) {
    	if(block instanceof BlockSignPost) {
    		BlockEntitySign sign = (BlockEntitySign) block.getLevel().getBlockEntity(block);
    		return sign.getText();
    	}    	
		return null;
    }
    
    public boolean isSign(Block block) {
    	switch(block.getId()) {
    		case Block.WALL_SIGN:
    		case Block.SIGN_POST:
    			return true;
    		default:
    			return false;
    	}
    }
    
    public boolean isNumber(String num) {
        String regex = "/^[0-9]+:[0-9]+$/";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(num);
        return m.find();
    }

}
