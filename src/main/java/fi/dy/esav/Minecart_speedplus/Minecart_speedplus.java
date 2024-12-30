package fi.dy.esav.Minecart_speedplus;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Minecart_speedplus extends JavaPlugin {

	private final Minecart_speedplusVehicleListener VehicleListener = new Minecart_speedplusVehicleListener(this);

	private final Minecart_speedplusSignListener SignListener = new Minecart_speedplusSignListener(this);

	final NamespacedKey key_fly = new NamespacedKey(this, "fly");
	final NamespacedKey key_speed = new NamespacedKey(this, "speed");

	static double speedmultiplier = 1D;

	boolean result;

	double multiplier;

	public static double getSpeedMultiplier() {
		return speedmultiplier;
	}

	public boolean setSpeedMultiplier(double multiplier) {
		if ((((0.0D < multiplier) ? 1 : 0) & ((multiplier <= 4.0D) ? 1 : 0)) != 0) {
			speedmultiplier = multiplier;
			return true;
		}
		return false;
	}

	public void onEnable() {
		getLogger().info(getPluginMeta().getDisplayName() + " started.");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.VehicleListener, (Plugin)this);
		pm.registerEvents(this.SignListener, (Plugin)this);
	}

	public void onDisable() {
		getLogger().info(getPluginMeta().getDisplayName() + " stopped.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("msp")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				if (!player.hasPermission("msp.cmd")) {
					player.sendMessage("You don't have permission to do that");
					return true;
				}
			}
			try {
				this.multiplier = Double.parseDouble(args[0]);
			} catch (Exception e) {
				sender.sendMessage(NamedTextColor.YELLOW + "should be a number");
				return false;
			}
			this.result = setSpeedMultiplier(this.multiplier);
			if (this.result) {
				sender.sendMessage(NamedTextColor.YELLOW + "multiplier for new Minecarts set to: " + this.multiplier);
				return true;
			}
			sender.sendMessage(NamedTextColor.YELLOW + "can not be set to zero and must be below");
			return true;
		}
		return false;
	}
}
