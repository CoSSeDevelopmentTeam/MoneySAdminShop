/**
 * MoneySAdminShop
 *
 *
 * CosmoSunriseServerPluginEditorsTeam
 *
 * HP: http://info.comorevi.net
 * GitHub: https://github.com/CoSSeDevelopmentTeam
 *
 *
 * [Java版]
 * @author popkechupki
 *
 * 機能版 アップデート履歴
 *
 * - 1.0.0
 *    基本的な機能を追加。ほぼ動作するように。
 *  - 1.0.1
 *    看板の破壊やショップの作成時にエラーが出ていた問題を修正
 * - 1.2.0
 *    手数料概念の追加とショップデータをsqlite3での保管管理に変更
 *  - 1.2.1
 *    手数料の数値が一桁間違っていたのを修正
 *  - 1.2.2
 *    ワールドごとにショップを管理できるよう変更
 *  - 1.2.3
 *    MoneySAPIv3.1.2対応とpom.xmlの更新
 *  - 1.2.4
 *    SQLite3DataProviderの書き直しに伴う変更とGUI化
 *  - 1.2.5
 *    データーベース名を修正
 * - 1.3.0
 *   MoneySAPI v4.0.0対応
 *
 */

package net.comorevi.moneysadminshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import net.comorevi.moneysadminshop.command.AdminShopCommand;
import net.comorevi.moneysadminshop.util.TextValues;

public class Main extends PluginBase {
	private Config translateFile;
    private Map<String, Object> configData = new HashMap<String, Object>();
    private Map<String, Object> pluginData = new HashMap<String, Object>();
    private Config conf;
    
    public static String shopTitle;
	
	@Override
	public void onEnable() {
		this.getDataFolder().mkdir();
		this.initHelpFile();
		this.initMessageConfig();
		this.initMoneySAdminShopConfig();

		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getServer().getCommandMap().register("ashop", new AdminShopCommand("ashop"));
	}

	@Override
    public void onDisable() {
	    MoneySAdminShopAPI.getInstance().disconnectSQL();
    }

	/////////////
	// Utility //
	/////////////
	public void helpMessage(final CommandSender sender){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getDataFolder().toString() + "/Help.txt")), "UTF-8"));
                    String txt;
                    boolean op = (boolean) sender.isOp();
                    boolean send = true;
                    while(true){
                        txt = br.readLine();
                        if(txt == null)break;
                        if(txt.startsWith("##"))continue;
                        if(txt.equals("::op")){
                            send = false;
                            continue;
                        }
                        if(op)send = true;
                        if(txt.equals("::all")){
                            send = true;
                            continue;
                        }
                        if(send) sender.sendMessage(txt);
                    }
                    br.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return;
            }
        });
        th.start();
    }

    public String translateString(String key, String... args){
        if(configData != null || !configData.isEmpty()){
            String src = (String) configData.get(key);
            if(src == null || src.equals("")) return TextValues.ALERT + (String) configData.get("error-notFoundKey");
            for(int i=0;i < args.length;i++){
                src = src.replace("{%" + i + "}", args[i]);
            }
            return src;
        }
        return null;
    }

    public String parseMessage(String message) {
        return "";
    }
    
    private void initMessageConfig(){
        if(!new File(getDataFolder().toString() + "/Message.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Message.yml"), true);//trueで追加書き込み,falseで上書き
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                Utils.writeFile(new File(getDataFolder().toString() + "/Message.yml"), this.getClass().getClassLoader().getResourceAsStream("Message.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.translateFile = new Config(new File(getDataFolder().toString() + "/Message.yml"), Config.YAML);
        this.translateFile.load(getDataFolder().toString() + "/Message.yml");
        this.configData = this.translateFile.getAll();
        return;
    }

    private void initMoneySAdminShopConfig(){
        if(!new File(getDataFolder().toString() + "/Config.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Config.yml"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
            this.conf.load(getDataFolder().toString() + "/Config.yml");
            this.conf.set("shopTitle", "CoSSe Shop");
            this.conf.save();
        }

        this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
        this.conf.load(getDataFolder().toString() + "/Config.yml");
        this.pluginData = this.conf.getAll();

        /*コンフィグからデータを取得*/
        shopTitle = (String) pluginData.get("shopTitle");

        return;
    }
    
    public void initHelpFile(){
    	if(!new File(getDataFolder().toString() + "/Help.txt").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Help.txt"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                
                Utils.writeFile(new File(getDataFolder().toString() + "/Help.txt"), this.getClass().getClassLoader().getResourceAsStream("Help.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	return;
    }
	
}