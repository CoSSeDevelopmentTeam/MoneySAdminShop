package net.comorevi.moneysadminshop;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import net.comorevi.moneysadminshop.util.SQLite3DataProvider;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class MoneySAdminShopAPI {
    private static MoneySAdminShopAPI instance = new MoneySAdminShopAPI();
    private SQLite3DataProvider dataProvider = new SQLite3DataProvider();

    private MoneySAdminShopAPI() {
        instance = this;
    }

    public static MoneySAdminShopAPI getInstance() {
        return instance;
    }

    public void createShop(String shopOwner, int saleNum, int price, int productID, int productMeta, Block sign) {
        dataProvider.addShop(shopOwner, saleNum, price, productID, productMeta, sign.getLocation());
    }

    public void removeShopBySign(Location location) {
        dataProvider.removeShopBySign(location);
    }

    public boolean isOwnerBySign(Location location, Player player) {
        if (!existsShopBySign(location)) throw new NullPointerException("There is no shop in that location.");
        return getShopDataBySign(location).get("shopOwner").equals(player.getName());
    }

    public boolean existsShopBySign(Location location) {
        try {
            return dataProvider.existsShopBySign(location);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("There is no shop in that location.");
    }

    public LinkedHashMap<String, Object> getShopDataBySign(Location location) {
        return dataProvider.getShopInfoMapBySign(location);
    }

    protected void disconnectSQL() {
        try {
            dataProvider.DisconnectSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}