package net.comorevi.moneysadminshop;

import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public class EventListener implements Listener {

    private MoneySAdminShop plugin;
    private SQLite3DataProvider sqLite3DataProvider;

    public EventListener(MoneySAdminShop plugin, SQLite3DataProvider sql) {
        this.plugin = plugin;
        this.sqLite3DataProvider = sql;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		switch(block.getId()) {
			case Block.SIGN_POST:
			case Block.WALL_SIGN:
				Object[] signCondition = {(int) block.x, (int) block.y, (int) block.z , block.getLevel().getName()};
				if(sqLite3DataProvider.existsShop(signCondition)) {
					if(sqLite3DataProvider.isShopOwnerOrOperator(signCondition, player)) {
						sqLite3DataProvider.removeShopBySign(signCondition);
						player.sendMessage(TextValues.INFO + plugin.translateString("shop-removed"));
					} else {
						player.sendMessage(TextValues.ALERT + plugin.translateString("﻿error-shop-remove"));
						event.setCancelled();
					}
				}
				break;
		}
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
    	try {
			if(event.getLine(0).equals("ashop") && !event.getLine(1).equals("") && !event.getLine(2).equals("") && !event.getLine(3).equals("")) {
				Player player = event.getPlayer();
				if(player.isOp()) {
					String shopOwner = player.getName();
					String[] productData = event.getLine(3).split(":");
					int saleNum = 0;
					int price = 0;
					int priceIncludeCommission = 0;
					int pID = 0;
					int pMeta = 0;
					try {
						saleNum = Integer.parseInt(event.getLine(1));
						price = Integer.parseInt(event.getLine(2));
						priceIncludeCommission = (int) (price * 0.05);
						pID = Integer.parseInt(productData[0]);
						pMeta = Integer.parseInt(productData[1]);
					} catch (NullPointerException e) {
						pMeta = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
						pMeta = 0;
					} catch (NumberFormatException e) {
						event.setCancelled();
						player.sendMessage("システム>> 不適切なデータが入力されました");
					}
					
					Block sign = event.getBlock();
					
					if (!event.getLine(0).equals("ashop")) return;
					if (saleNum <= 0) return;
					if (price < 0) return;
					if (pID == 0) return;
					
					String productName = Block.get(pID, pMeta).getName();
					event.setLine(0, "[" + MoneySAdminShop.shopTitle + "]");
					event.setLine(1, "数量/amount:" + saleNum);
					event.setLine(2, "値段/price:" + priceIncludeCommission);
					event.setLine(3, productName);
					
					Object[] signCondition = {(int) event.getBlock().x, (int) event.getBlock().y, (int) event.getBlock().z, event.getBlock().getLevel().getName()};
					if(sqLite3DataProvider.existsShop(signCondition)) {
						sqLite3DataProvider.updateShop(shopOwner, saleNum, priceIncludeCommission, pID, pMeta, sign);
					} else {
						sqLite3DataProvider.registerShop(shopOwner, saleNum, priceIncludeCommission, pID, pMeta, sign);
					}
					player.sendMessage(TextValues.INFO + plugin.translateString("shop-create"));
				} else {
					player.sendMessage(TextValues.WARNING + plugin.translateString("error-all"));
				}
			}
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
		Block block = event.getBlock();
		switch(block.getId()) {
			case Block.SIGN_POST:
			case Block.WALL_SIGN:
				Object[] signCondition = {(int) block.x, (int) block.y, (int) block.z, block.getLevel().getName()};
				if(sqLite3DataProvider.existsShop(signCondition)) {
					LinkedHashMap<String, Object> shopSignInfo = sqLite3DataProvider.getShopInfo(signCondition);
					
					int buyermoney = plugin.getMoneySAPI().getMoney(player.getName());
					if((int) shopSignInfo.get("price") < buyermoney) {
						player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy2"));
						return;
					}
					
					int pID = (int) shopSignInfo.get("productID");
					int pMeta = (int) shopSignInfo.get("productMeta");
					
					Item shopItem = Item.get(pID, pMeta, (int) shopSignInfo.get("saleNum"));
					if(player.getInventory().canAddItem(shopItem)) {
						player.getInventory().addItem(shopItem);
					} else {
						player.sendMessage(TextValues.INFO + plugin.translateString("error-shop-buy1"));
					}
					
					plugin.getMoneySAPI().reduceMoney(username, (int) shopSignInfo.get("price"));
					
					player.sendMessage(TextValues.INFO + plugin.translateString("shop-buy", shopItem.getName().toString(), shopSignInfo.get("saleNum").toString(), shopSignInfo.get("price").toString(), plugin.getMoneySAPI().getMoneyUnit()));
				}
				break;
		}
    }

}
