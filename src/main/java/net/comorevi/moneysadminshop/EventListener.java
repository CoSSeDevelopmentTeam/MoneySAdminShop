package net.comorevi.moneysadminshop;

import java.util.Arrays;
import java.util.LinkedHashMap;

import FormAPI.api.FormAPI;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import net.comorevi.moneysadminshop.util.DataCenter;
import net.comorevi.moneysadminshop.util.TextValues;

public class EventListener implements Listener {

    private Main plugin;
    private FormAPI formAPI = new FormAPI();

    public EventListener(Main plugin) {
        this.plugin = plugin;
        formAPI.add("create-ashop", getCreateAShopWindow());
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		switch(block.getId()) {
			case Block.SIGN_POST:
			case Block.WALL_SIGN:
				if(MoneySAdminShopAPI.getInstance().existsShopBySign(block.getLocation())) {
					if(MoneySAdminShopAPI.getInstance().isOwnerBySign(block.getLocation(), player)) {
						MoneySAdminShopAPI.getInstance().removeShopBySign(block.getLocation());
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
    public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String username = player.getName();
		Block block = event.getBlock();
		if (DataCenter.existsEditCmdQueue(player)) {
			switch (block.getId()) {
				case Block.SIGN_POST:
				case Block.WALL_SIGN:
					if (plugin.getServer().getLevelByName(block.level.getName()).getBlockEntity(block.getLocation()) instanceof BlockEntitySign) {
						BlockEntitySign sign = (BlockEntitySign) plugin.getServer().getLevelByName(block.level.getName()).getBlockEntity(block.getLocation());
						if (sign.getText()[0].equals("ashop")) {
							player.showFormWindow(formAPI.get("create-ashop"), formAPI.getId("create-ashop"));
							DataCenter.addEditCmdQueue(player, block);
						}
					}
					break;
			}
		} else {
			switch(block.getId()) {
				case Block.SIGN_POST:
				case Block.WALL_SIGN:
					if(MoneySAdminShopAPI.getInstance().existsShopBySign(block.getLocation())) {
						LinkedHashMap<String, Object> shopSignInfo = MoneySAdminShopAPI.getInstance().getShopDataBySign(block.getLocation());

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

						player.sendMessage(TextValues.INFO + plugin.translateString("shop-buy", shopItem.getName(), shopSignInfo.get("saleNum").toString(), shopSignInfo.get("price").toString(), plugin.getMoneySAPI().getMoneyUnit()));
					}
					break;
			}
		}
    }

    @EventHandler
	public void onFormResponded(PlayerFormRespondedEvent event) {
		if (event.getFormID() == formAPI.getId("create-ashop")) {
			if (event.wasClosed()) return;
			FormResponseCustom responseCustom = (FormResponseCustom) event.getResponse();
			if (responseCustom.getInputResponse(1) != null && responseCustom.getInputResponse(2) != null && responseCustom.getInputResponse(4) != null) {
				int itemId = 0;
				int itemMeta = 0;
				int itemAmount = (int) responseCustom.getSliderResponse(3);
				int itemPrice = 0;
				try {
					itemId = Integer.parseInt(responseCustom.getInputResponse(1));
					itemMeta = Integer.parseInt(responseCustom.getInputResponse(2));
					itemPrice = Integer.parseInt(responseCustom.getInputResponse(4));
				} catch (NumberFormatException e) {
					event.getPlayer().sendMessage(TextValues.ALERT+plugin.translateString("error-shop-create"));
					return;
				}
				if (itemId <= 0 || itemAmount <= 0 || itemPrice < 0 || itemMeta < 0) {
					event.getPlayer().sendMessage(TextValues.ALERT+plugin.translateString("error-shop-create2"));
				} else {
					BlockEntitySign sign = (BlockEntitySign) event.getPlayer().getLevel().getBlockEntity(DataCenter.getRegisteredBlockByEditCmdQueue(event.getPlayer()).getLocation());
					sign.setText(TextFormat.GOLD + Item.get(itemId).getName(), "個数: " + itemAmount, "値段(手数料込): " + (int) (itemPrice * Main.COMMISTION_RATIO), "official");
					MoneySAdminShopAPI.getInstance().createShop(event.getPlayer().getName(), itemAmount, (int) (itemPrice * Main.COMMISTION_RATIO), itemId, itemMeta, DataCenter.getRegisteredBlockByEditCmdQueue(event.getPlayer()));
					event.getPlayer().sendMessage(TextValues.INFO+plugin.translateString("shop-create"));
				}
			} else {
				event.getPlayer().sendMessage(TextValues.ALERT+plugin.translateString("error-shop-create"));
			}
		}
	}

	private FormWindowCustom getCreateAShopWindow() {
		Element[] elements = {
				new ElementLabel("ショップの情報を入力してください。適切な値を入力しなければ作成できません。"),
				new ElementInput("Item ID", "1以上256以下で入力..."),
				new ElementInput("Item META(DAMAGE)", "メタ値を入力...", String.valueOf(0)),
				new ElementSlider("Amount", 1, 64, 1, 4),
				new ElementInput("Price", "0以上で入力...")
		};
		return new FormWindowCustom("Create - OfficialShop", Arrays.asList(elements));
	}

}
