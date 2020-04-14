package net.comorevi.moneysadminshop;

import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import net.comorevi.cphone.presenter.SharingData;
import net.comorevi.moneyapi.MoneySAPI;
import net.comorevi.moneysadminshop.util.DataCenter;
import net.comorevi.moneysadminshop.util.FormManager;

public class EventListener implements Listener {

    private Main plugin;
    private FormManager form;
    private Boolean isCPhoneLoaded;

    public EventListener(Main plugin) {
        this.plugin = plugin;
        this.form = new FormManager(plugin);
        if (plugin.getServer().getPluginManager().getPlugin("CPhone") != null) {
        	isCPhoneLoaded = true;
		} else {
        	isCPhoneLoaded = false;
		}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		switch(block.getId()) {
			case Block.SIGN_POST:
			case Block.WALL_SIGN:
				if(MoneySAdminShopAPI.getInstance().existsShopBySign(block.getLocation())) {
					if(player.isOp()) {
						MoneySAdminShopAPI.getInstance().removeShopBySign(block.getLocation());
						player.sendMessage(Main.PREFIX + plugin.translateString("shop-removed"));
					} else {
						player.sendMessage(Main.PREFIX + plugin.translateString("﻿error-shop-remove"));
						event.setCancelled();
					}
				}
				break;
		}
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String username = player.getName();
		Block block = event.getBlock();
		if (DataCenter.existsEditCmdQueue(player)) {
			switch (block.getId()) {
				case Block.SIGN_POST:
				case Block.WALL_SIGN:
					event.setCancelled();
					if (isCPhoneLoaded && player.getInventory().getItemInHand().getId() == SharingData.triggerItemId) {
						player.sendMessage(Main.PREFIX + plugin.translateString("cancel-cphone-trigger"));
						return;
					}
					if (player.getLevel().getBlockEntity(block.getLocation()) instanceof BlockEntitySign) {
						BlockEntitySign sign = (BlockEntitySign) player.getLevel().getBlockEntity(block.getLocation());
						if (sign.getText()[0].equals("ashop")) {
							DataCenter.addEditCmdQueue(player, block);
							form.sendCreateAShopWindow(player);
						}
					}
					break;
			}
		} else {
			switch(block.getId()) {
				case Block.SIGN_POST:
				case Block.WALL_SIGN:
					if(MoneySAdminShopAPI.getInstance().existsShopBySign(block.getLocation())) {
						event.setCancelled();
						LinkedHashMap<String, Object> shopSignInfo = MoneySAdminShopAPI.getInstance().getShopDataBySign(block.getLocation());

						if((int) shopSignInfo.get("price") > MoneySAPI.getInstance().getMoney(player)) {
							player.sendMessage(Main.PREFIX + plugin.translateString("error-shop-buy2"));
							return;
						}

						int pID = (int) shopSignInfo.get("productID");
						int pMeta = (int) shopSignInfo.get("productMeta");

						Item shopItem = Item.get(pID, pMeta, (int) shopSignInfo.get("saleNum"));
						if(player.getInventory().canAddItem(shopItem)) {
							player.getInventory().addItem(shopItem);
						} else {
							player.sendMessage(Main.PREFIX + plugin.translateString("error-shop-buy1"));
						}

						MoneySAPI.getInstance().reduceMoney(username, (int) shopSignInfo.get("price"));

						player.sendMessage(Main.PREFIX + plugin.translateString("shop-buy", shopItem.getName(), shopSignInfo.get("saleNum").toString(), shopSignInfo.get("price").toString(), MoneySAPI.UNIT));
					}
					break;
			}
		}
    }
}
