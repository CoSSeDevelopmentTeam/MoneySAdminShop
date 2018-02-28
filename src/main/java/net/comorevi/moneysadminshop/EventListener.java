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
    		try {
				String[] line = getSignText(block);
				Player player = event.getPlayer();
				if(line[0].equals("[" + TextFormat.AQUA + MoneySAdminShop.shopTitle + TextFormat.BLACK + "]")) {
					if(player.isOp()) {
						player.sendMessage(TextValues.INFO + plugin.translateString("shop-removed"));
					} else {
						event.setCancelled();
						player.sendMessage(TextValues.ALERT + plugin.translateString("﻿error-shop-remove"));
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
   
			}
    	}
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
    	Player player = event.getPlayer();
    	try {
			if(event.getLine(0).equals("ashop") && !event.getLine(1).equals("") && !event.getLine(2).equals("") && !event.getLine(3).equals("")) {
				String[] line = event.getLines();
				if(player.isOp()) {
					event.setLine(0, "[" + TextFormat.AQUA + MoneySAdminShop.shopTitle + TextFormat.BLACK + "]");
					String[] line1 = line[1].split(":");
					//if(isNumber(line1[0]) || isNumber(line1[1]) || isNumber(line[2]) || isNumber(line[3])) {
					String itemname = null;
					if(line1.length == 1) {
						Item item = Item.get(Integer.parseInt(line[1]));
						itemname = item.getName();
						event.setLine(1, itemname);
						event.setLine(3, "取引量: " + line[3] + ",  " + "値段: " + line[2]);
						event.setLine(2, line1[0] + ":" + 0);
					} else {
						Item item = Item.get(Integer.parseInt(line1[0]), Integer.parseInt(line1[1]));
						itemname = item.getName();
						event.setLine(1, itemname);
						event.setLine(3, "取引量: " + line[3] + ",  " + "値段: " + line[2]);
						event.setLine(2, line1[0] + ":" + line1[1]);
					}
					player.sendMessage(TextValues.INFO + plugin.translateString("shop-create"));
				} else {
					player.sendMessage(TextValues.WARNING + plugin.translateString("error-all"));
				}
			}/*else if(event.getLine(0).equals("[" + MoneySAdminShop.shopTitle + "]")) {
    			event.setCancelled();
    			player.sendMessage(TextValues.ALERT + plugin.translateString("error-all"));
    		}*/
		} catch(NumberFormatException e) {
    		return;
		} catch(ArrayIndexOutOfBoundsException e) {
    		//なんにもしません
		} catch(IllegalArgumentException e) {
    		//なんにもしません
		}
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	String username = player.getName();
    	Block block= event.getBlock();
    	if(isSign(block)) {
    		String[] line = getSignText(block);
    		if(line[0].equals("[" + TextFormat.AQUA + MoneySAdminShop.shopTitle + TextFormat.BLACK + "]")) {
    			try {
    				String[] line3 = line[3].split(", ");
	    			String[] line3price = line3[1].split(": ");
	    			int price = Integer.parseInt(line3price[1]);
	    			int money = plugin.getMoneySAPI().getMoney(player);
	    			if(price < money) {
	    				String[] line2 = line[2].split(":");
	    				int id = Integer.parseInt(line2[0]);
	    				int meta = Integer.parseInt(line2[1]);
	    				String[] line3count = line3[0].split(": ");
	    				int count = Integer.parseInt(line3count[1]);
	    				if(player.getInventory().canAddItem(Item.get(id, meta, count))) {
	    					player.getInventory().addItem(Item.get(id, meta, count));
	    					plugin.getMoneySAPI().reduceMoney(username, price);
	    					player.sendMessage(TextValues.INFO + plugin.translateString("shop-buy", line[1], String.valueOf(count), String.valueOf(price), plugin.getMoneySAPI().getMoneyUnit()));
	    				} else {
	    					player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy1"));
	    				}
	    			} else {
	    				player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy2"));
	    			}
    			} catch(ArrayIndexOutOfBoundsException e) {
    				
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
