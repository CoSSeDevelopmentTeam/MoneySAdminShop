package net.comorevi.moneysadminshop;

import cn.nukkit.Player;
import cn.nukkit.block.Block;

import java.sql.*;
import java.util.LinkedHashMap;

public class SQLite3DataProvider {
	
	private MoneySAdminShop mainClass;
	private String path;
	
	public SQLite3DataProvider(MoneySAdminShop plugin) {
		this.mainClass = plugin;
		this.path = mainClass.getDataFolder().toString();
		connect();
	}
	
	public void removeShopBySign(int[] condition) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("delete from AdminShop where signX = " + condition[0] + " and signY = " + condition[1] + " and signZ = " + condition[2]);
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at removeShopSign");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at removeShopSign on close connection");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void registerShop(String shopOwner, int saleNum, int price, int productID, int productMeta, Block sign) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("insert into AdminShop (shopOwner, saleNum, price, productID, productMeta, signX, signY, signZ) values ('"+shopOwner+"', "+saleNum+", "+price+", "+productID+", "+productMeta+", "+sign.x+", "+sign.y+", "+sign.z+")");
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at registerShop");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at registerShop on close connection");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void updateShop(String shopOwner, int saleNum, int price, int productID, int productMeta, Block sign) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("replace into AdminShop (shopOwner, saleNum, price, productID, productMeta, signX, signY, signZ) values ('"+shopOwner+"', "+saleNum+", "+price+", "+productID+", "+productMeta+", "+sign.x+", "+sign.y+", "+sign.z+")");
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at updateShop");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at updateShop on close connection");
					e.printStackTrace();
				}
			}
		}
	}
	
	public LinkedHashMap<String, Object> getShopInfo(int[] condition) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			ResultSet rs = statement.executeQuery("select * from AdminShop where signX = " + condition[0] + " and signY = " + condition[1] + " and signZ = " + condition[2]);
			LinkedHashMap<String, Object> shopInfo = new LinkedHashMap<String, Object>(){{
				put("shopOwner", rs.getString("shopOwner"));
				put("saleNum", rs.getInt("saleNum"));
				put("price", rs.getInt("price"));
				put("productID", rs.getInt("productID"));
				put("productMeta", rs.getInt("productMeta"));
				put("signX", rs.getInt("signX"));
				put("signY", rs.getInt("signY"));
				put("signZ", rs.getInt("signZ"));
			}};
			
			return shopInfo;
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at getShopInfo");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at getShopInfo on close connection");
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public boolean isShopOwnerOrOperator(int[] condition, Player player) {
		Connection connection = null;
		try {
			if(player.isOp()) return true;
			
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			ResultSet rs = statement.executeQuery("select * from AdminShop where signX = " + condition[0] + " and signY = " + condition[1] + " and signZ = " + condition[2]);
			if(rs.getString("shopOwner").equals(player.getName())) return true;
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at isShopOwner");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at isShopOwner on close connection");
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public boolean existsShop(int[] condition) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			ResultSet rs = statement.executeQuery("select * from AdminShop where signX = " + condition[0] + " and signY = " + condition[1] + " and signZ = " + condition[2]);
			return rs.next();
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at existsShop");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage() + " : at existsShop on close connection");
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
		}catch(Exception e){
			System.err.println(e.getMessage() + " : at connect");
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.path + "/DataDB.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("create table if not exists AdminShop " +
					"(" +
					"id integer primary key autoincrement, " +
					"shopOwner text not null, " +
					"saleNum integer not null, " +
					"price integer not null, " +
					"productID integer not null, " +
					"productMeta integer not null, " +
					"signX integer not null, " +
					"signY integer not null, " +
					"signZ integer not null" +
					")"
			);
		} catch(SQLException e) {
			System.err.println(e.getMessage() + " : at createDB");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println(e.getMessage() + " : at createDB connection close");
				}
			}
		}
	}
}
