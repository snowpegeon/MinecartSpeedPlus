package fi.dy.esav.Minecart_speedplus;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Minecart_speedplusSignListener implements Listener {

	Minecart_speedplus plugin;

	public Minecart_speedplusSignListener(Minecart_speedplus instance) {

		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		if (!e.getLine(0).equalsIgnoreCase("[msp]")) {
			return;
		}

		Boolean ok = false;

		if (e.getLine(1).equalsIgnoreCase("fly") || e.getLine(1).equalsIgnoreCase("nofly")) {
			if (!(e.getPlayer().hasPermission("msp.signs.fly"))) {
				e.line(0, Component.text("NO PERMS").color(NamedTextColor.DARK_RED));
				return;
			}

			if (e.getBlock().getState() instanceof Sign sign) {
				sign.getPersistentDataContainer().set(plugin.key_fly, PersistentDataType.BOOLEAN,
						e.getLine(1).equalsIgnoreCase("fly"));
				ok = true;
			}
		} else {
			boolean error = false;
			double speed = -1;

			try {
				speed = Double.parseDouble(e.getLine(1));
			} catch (Exception ex) {
				error = true;
			}

			if (error || 50 < speed || speed < 0) {
				e.line(1, Component.text("WRONG VALUE").color(NamedTextColor.DARK_RED));
				return;
			}

			if (!(e.getPlayer().hasPermission("msp.signs.speed"))) {
				e.line(0, Component.text("NO PERMS").color(NamedTextColor.DARK_RED));
				return;
			}

			if (e.getBlock().getState() instanceof Sign sign) {
				sign.getPersistentDataContainer().set(plugin.key_speed, PersistentDataType.DOUBLE, speed);
				ok = true;
			}
		}

		if (ok) {
			e.line(0, Component.text("[").color(NamedTextColor.DARK_GRAY)
					.append(Component.text("msp").color(NamedTextColor.DARK_GREEN))
					.append(Component.text("]").color(NamedTextColor.DARK_GRAY)));
		}
	}
}
