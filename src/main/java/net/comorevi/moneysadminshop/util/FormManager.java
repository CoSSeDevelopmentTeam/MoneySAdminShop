package net.comorevi.moneysadminshop.util;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import net.comorevi.moneyapi.util.TAXType;
import net.comorevi.moneysadminshop.Main;
import net.comorevi.moneysadminshop.MoneySAdminShopAPI;
import ru.nukkitx.forms.elements.CustomForm;

public class FormManager {

    private Main plugin;

    public FormManager(Main plugin) {
        this.plugin = plugin;
    }

    public void sendCreateAShopWindow(Player player) {
        getCreateAShopWindow().send(player, (targetPlayer, targetForm, data) -> {
            if (data == null) return;
            FormResponseCustom response = targetForm.getResponse();
            if (response.getInputResponse(1) != null && response.getInputResponse(2) != null && response.getInputResponse(4) != null) {
                int itemId = 0;
                int itemMeta = 0;
                int itemAmount = (int) response.getSliderResponse(3);
                int itemPrice = 0;
                try {
                    itemId = Integer.parseInt(response.getInputResponse(1));
                    itemMeta = Integer.parseInt(response.getInputResponse(2));
                    itemPrice = Integer.parseInt(response.getInputResponse(4));
                } catch (NumberFormatException e) {
                    player.sendMessage(Main.PREFIX+plugin.translateString("error-shop-create"));
                    return;
                }
                if (itemId <= 0 || itemAmount <= 0 || itemPrice < 0 || itemMeta < 0) {
                    player.sendMessage(Main.PREFIX+plugin.translateString("error-shop-create2"));
                } else {
                    BlockEntitySign sign = (BlockEntitySign) player.getLevel().getBlockEntity(DataCenter.getRegisteredBlockByEditCmdQueue(player).getLocation());
                    sign.setText(TextFormat.GOLD + Item.get(itemId).getName(), "個数: " + itemAmount, "値段(手数料込): " + (int) (itemPrice * TAXType.ADMIN_SHOP), "official");
                    MoneySAdminShopAPI.getInstance().createShop(player.getName(), itemAmount, (int) (itemPrice * TAXType.ADMIN_SHOP), itemId, itemMeta, DataCenter.getRegisteredBlockByEditCmdQueue(player));
                    player.sendMessage(Main.PREFIX+plugin.translateString("shop-create"));
                }
            } else {
                player.sendMessage(Main.PREFIX+plugin.translateString("error-shop-create"));
            }
        });
    }

    public CustomForm getCreateAShopWindow() {
        CustomForm customForm = new CustomForm("作成 - AdminShop")
                .addLabel("ショップの情報を入力してください。適切な値を入力しなければ作成できません。")
                .addInput("Item ID", "1以上256以下で入力...")
                .addInput("Item META(DAMAGE)", "メタ値を入力...", String.valueOf(0))
                .addSlider("Amount", 1, 64, 1, 4)
                .addInput("Price", "0以上で入力...");
        return customForm;
    }
}
