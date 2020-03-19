package net.comorevi.moneysadminshop.util;

import cn.nukkit.level.Location;

import java.sql.*;
import java.util.LinkedHashMap;

public class SQLite3DataProvider {

	private Connection connection = null;

	public SQLite3DataProvider() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			this.connection = DriverManager.getConnection("jdbc:sqlite:./plugins/MoneySAdminShop/DataDB.db");
			String sql = "create table if not exists AdminShop " +
					"(" +
					"id integer primary key autoincrement, " +
					"shopOwner text not null, " +
					"saleNum integer not null, " +
					"price integer not null, " +
					"productID integer not null, " +
					"productMeta integer not null, " +
					"signX integer not null, " +
					"signY integer not null, " +
					"signZ integer not null, " +
					"level text not null" +
					")";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setQueryTimeout(30);
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean existsShopBySign(Location location) throws SQLException {
		PreparedStatement statement = null;
		try {
			String sql = "select * from AdminShop where signX = ? and signY = ? and signZ = ? and level = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, location.getFloorX());
			statement.setInt(2, location.getFloorY());
			statement.setInt(3, location.getFloorZ());
			statement.setString(4, location.getLevel().getName());
			statement.setQueryTimeout(30);

			return statement.executeQuery().next();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) statement.close();
		}
		return false;
	}

	public void addShop(String owner, int amount, int price, int itemId, int itemMeta, Location signLocation) {
		try {
			String sql = "insert into AdminShop (shopOwner, saleNum, price, productID, productMeta, signX, signY, signZ, chestX, chestY, chestZ , level) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, owner);
			statement.setInt(2, amount);
			statement.setInt(3, price);
			statement.setInt(4, itemId);
			statement.setInt(5, itemMeta);
			statement.setInt(6, signLocation.getFloorX());
			statement.setInt(7, signLocation.getFloorY());
			statement.setInt(8, signLocation.getFloorZ());
			statement.setString(9, signLocation.getLevel().getName());
			statement.setQueryTimeout(30);

			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeShopBySign(Location location) {
		try {
			if (!existsShopBySign(location)) throw new NullPointerException("There is no shop in that location.");
			String sql = "delete from AdminShop where signX = ? and signY = ? and signZ = ? and level = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, location.getFloorX());
			statement.setInt(2, location.getFloorY());
			statement.setInt(3, location.getFloorZ());
			statement.setString(4, location.getLevel().getName());
			statement.setQueryTimeout(30);

			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public LinkedHashMap<String, Object> getShopInfoMapBySign(Location location) {
		try {
			if (!existsShopBySign(location)) throw new NullPointerException("There is no shop in that location.");
			String sql = "select * from AdminShop where signX = ? and signY = ? and signZ = ? and level = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, location.getFloorX());
			statement.setInt(2, location.getFloorY());
			statement.setInt(3, location.getFloorZ());
			statement.setString(4, location.getLevel().getName());
			statement.setQueryTimeout(30);

			ResultSet rs = statement.executeQuery();
			LinkedHashMap<String, Object> shopInfo = new LinkedHashMap<String, Object>(){{
				put("id", rs.getInt("id"));
				put("shopOwner", rs.getString("shopOwner"));
				put("saleNum", rs.getInt("saleNum"));
				put("price", rs.getInt("price"));
				put("productID", rs.getInt("productID"));
				put("productMeta", rs.getInt("productMeta"));
				put("signX", rs.getInt("signX"));
				put("signY", rs.getInt("signY"));
				put("signZ", rs.getInt("signZ"));
			}};

			statement.close();
			rs.close();
			return shopInfo;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		throw new NullPointerException("There is no shop in that location.");
	}

	public void DisconnectSQL() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
