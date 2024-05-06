package fi.dy.esav.Minecart_speedplus;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Minecart_speedplusSignListener implements Listener {

	Minecart_speedplus plugin;
	private static String stripFormatting(Component input) {
		return PlainTextComponentSerializer.plainText().serialize(input);
	}

	public Minecart_speedplusSignListener(Minecart_speedplus instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		String line0 = stripFormatting(e.line(0));
		if (!line0.equalsIgnoreCase("[msp]")) {
			return;
		}

		Boolean ok = false;
		String line1 = stripFormatting(e.line(1));

		if (line1.equalsIgnoreCase("fly") || line1.equalsIgnoreCase("nofly")) {
			if (!(e.getPlayer().hasPermission("msp.signs.fly"))) {
				e.line(0, Component.text("NO PERMS").color(NamedTextColor.DARK_RED));
				return;
			}

			if (e.getBlock().getState() instanceof Sign sign) {
				sign.getPersistentDataContainer().set(plugin.key_fly, PersistentDataType.BOOLEAN,
					line1.equalsIgnoreCase("fly"));
				sign.update();
				ok = true;
			}
		} else {
			if (!(e.getPlayer().hasPermission("msp.signs.speed"))) {
				e.line(0, Component.text("NO PERMS").color(NamedTextColor.DARK_RED));
				return;
			}

			boolean error = false;
			double speed = -1;

			try {
				speed = Double.parseDouble(line1);
			} catch (Exception ex) {
				error = true;
			}
			if (error || Double.isNaN(speed)) {
				e.line(1, Component.text("E: NaN").color(NamedTextColor.DARK_RED));
				return;
			}

			if (100 < speed) {
				e.line(1, Component.text("E: <=100").color(NamedTextColor.DARK_RED));
				return;
			} else if (speed <= 0) {
				e.line(1, Component.text("E: >0").color(NamedTextColor.DARK_RED));
				return;
			}

			e.line(1, Component.text(line1).color(NamedTextColor.BLACK));

			if (e.getBlock().getState() instanceof Sign sign) {
				sign.getPersistentDataContainer().set(plugin.key_speed, PersistentDataType.DOUBLE, speed);
				sign.update();
				ok = true;
			}
		}

		if (ok) {
			e.line(0, Component.text("[").color(NamedTextColor.DARK_GRAY)
					.append(Component.text("msp").color(NamedTextColor.DARK_GREEN))
					.append(Component.text("]").color(NamedTextColor.DARK_GRAY)));
		} else {
			e.line(0, Component.text("[msp]").color(NamedTextColor.DARK_GRAY));
		}
	}
}
