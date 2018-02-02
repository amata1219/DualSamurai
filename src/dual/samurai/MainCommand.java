package dual.samurai;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCommand implements TabExecutor{

	private DualSamurai plugin = DualSamurai.getDualSamurai();
	private CustomConfig config = DualSamurai.getCustomConfig();

	public MainCommand(DualSamurai plugin){
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender paramCommandSender, Command paramCommand, String paramString,
			String[] paramArrayOfString) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 0){
			if(Language.isJp(plugin.getLanguage())){
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- DualSamurai -");
				sender.sendMessage(ChatColor.WHITE + "Spigot バージョン: " + plugin.getServer().getBukkitVersion());
				sender.sendMessage(ChatColor.WHITE + "Plugin バージョン: " + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.WHITE + "duals コマンド一覧:  /duals commands");
				sender.sendMessage(ChatColor.GRAY + "Developed by amata1219(Twitter: @amata1219)");
			}else{
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- DualSamurai -");
				sender.sendMessage(ChatColor.WHITE + "Spigot version: " + plugin.getServer().getBukkitVersion());
				sender.sendMessage(ChatColor.WHITE + "Plugin version: " + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.WHITE + "List of duals commands:  /duals commands");
				sender.sendMessage(ChatColor.GRAY + "Developed by amata1219(Twitter: @amata1219)");
			}
		}else if(args[0].equalsIgnoreCase("commands")){
			if(Language.isJp(plugin.getLanguage())){
				sender.sendMessage(ChatColor.AQUA + "/duals");
				sender.sendMessage(ChatColor.WHITE + "- 本プラグインの詳細を表示します。");
				sender.sendMessage(ChatColor.AQUA + "/duals commands");
				sender.sendMessage(ChatColor.WHITE + "- 本プラグインのコマンド一覧を表示します。");
				sender.sendMessage(ChatColor.AQUA + "/duals [true/false]");
				sender.sendMessage(ChatColor.WHITE + "- 二刀流機能を使用するか選択します。true で 有効、false で 無効になります。");
				sender.sendMessage(ChatColor.AQUA + "/duals reload");
				sender.sendMessage(ChatColor.WHITE + "- コンフィグをリロードします。");
				sender.sendMessage(ChatColor.AQUA + "/duals name");
				sender.sendMessage(ChatColor.WHITE + "- メインハンドに持っているアイテムの名前を表示します。");
			}else{
				sender.sendMessage(ChatColor.AQUA + "/duals");
				sender.sendMessage(ChatColor.WHITE + "- Display plugin info.");
				sender.sendMessage(ChatColor.AQUA + "/duals commands");
				sender.sendMessage(ChatColor.WHITE + "- Display list of duals commands.");
				sender.sendMessage(ChatColor.AQUA + "/duals [true/false]");
				sender.sendMessage(ChatColor.WHITE + "- Reload config.");
				sender.sendMessage(ChatColor.AQUA + "/duals reload");
				sender.sendMessage(ChatColor.WHITE + "- Display name of held item in main hand.");
			}
		}else if(args[0].equalsIgnoreCase("true")||args[0].equalsIgnoreCase("false")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				UUID uuid = p.getUniqueId();
				boolean b = args[0].equalsIgnoreCase("true");
				if(b != plugin.isEnable(uuid)){
					plugin.setEnable(uuid, b);
					if(b)plugin.addPlayer(p.getName());
					else plugin.removePlayer(p.getName());
					if(Language.isJp(plugin.getLanguage()))p.sendMessage(ChatColor.AQUA + "二刀流を" + (b ? "有効" : "無効") + "にしました。");
					else p.sendMessage(ChatColor.AQUA + "Dual Wielding is " + (b ? "enable" : "invalid") + ".");
				}else{
					if(Language.isJp(plugin.getLanguage()))p.sendMessage(ChatColor.RED + "既に" + (b ? "有効" : "無効") + "になっています。");
					else p.sendMessage(ChatColor.RED + "Already " + (b ? "enabled" : "invalided") + ".");
				}
			}
		}else if(args[0].equalsIgnoreCase("reload")){
			if(!sender.hasPermission("dual.samurai.reload"))return true;
			config.reloadConfig();
			if(Language.isJp(plugin.getLanguage()))sender.sendMessage(ChatColor.AQUA + "コンフィグをリロードしました。");
			else sender.sendMessage(ChatColor.AQUA + "Reloaded config.");
		}else if(args[0].equalsIgnoreCase("name")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.hasPermission("dual.samurai.name"))return true;
				ItemStack item = p.getInventory().getItemInMainHand();
				if(item != null){
					if(Language.isJp(plugin.getLanguage()))p.sendMessage(ChatColor.AQUA + "アイテム名: " + item.getType().toString());
					else p.sendMessage(ChatColor.AQUA + "Item Name: " + item.getType().toString());
				}
			}
		}
		return true;
	}

}
