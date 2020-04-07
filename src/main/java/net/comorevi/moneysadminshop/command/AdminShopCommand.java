package net.comorevi.moneysadminshop.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.moneysadminshop.Main;
import net.comorevi.moneysadminshop.util.DataCenter;

public class AdminShopCommand extends Command {
    public AdminShopCommand(String name) {
        super(name, "アドミンショップの編集モードを有効/無効化します。", "/ashop");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isPlayer()) {
            commandSender.sendMessage("このコマンドはゲーム内から実行できます。");
            return true;
        }

        if (!commandSender.hasPermission("moneysadminshop.command.ashop")) {
            commandSender.sendMessage("このコマンドを実行する権限がありません。");
            return false;
        }

        if (DataCenter.existsEditCmdQueue((Player) commandSender)) {
            DataCenter.removeEditCmdQueue((Player) commandSender);
            commandSender.sendMessage(Main.PREFIX + "チェストショップ編集モードを無効化しました。");
        } else {
            DataCenter.addEditCmdQueue((Player) commandSender);
            commandSender.sendMessage(Main.PREFIX + "チェストショップ編集モードを有効化しました。");
        }
        return true;
    }
}
