package fi.dy.esav.Minecart_speedplus;

import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

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

		if (e.getLine(1).equalsIgnoreCase("fly") || e.getLine(1).equalsIgnoreCase("nofly")) {
			if (!(e.getPlayer().hasPermission("msp.signs.fly"))) {
				e.setLine(0, "NO PERMS");
				return;
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
				e.setLine(1, "WRONG VALUE");
				return;
			}

			if (!(e.getPlayer().hasPermission("msp.signs.speed"))) {
				e.setLine(0, "NO PERMS");
				return;
			}
		}
		e.line(0,
				Component.text("[").color(NamedTextColor.WHITE)
						.append(Component.text("msp").color(NamedTextColor.GREEN))
						.append(Component.text("]").color(NamedTextColor.WHITE)));
	}
}
