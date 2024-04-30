package fi.dy.esav.Minecart_speedplus;

import java.util.logging.Logger;
import org.bukkit.Tag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class Minecart_speedplusVehicleListener implements Listener {
	int[] xmodifier = { -1, 0, 1 };
	int[] ymodifier = { -2, -1, 0, 1, 2 };
	int[] zmodifier = { -1, 0, 1 };

	int cartx, carty, cartz;
	int blockx, blocky, blockz;

	Block block;
	int blockid;

	double line1;

	public static Minecart_speedplus plugin;
	Logger log = Logger.getLogger("Minecraft");

	boolean error;

	Vector flyingmod = new Vector(10, 0.01, 10);
	Vector noflyingmod = new Vector(1, 1, 1);

	public Minecart_speedplusVehicleListener(Minecart_speedplus instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleCreate(VehicleCreateEvent event) {
		if (event.getVehicle() instanceof Minecart) {

			Minecart cart = (Minecart) event.getVehicle();
			cart.setMaxSpeed(0.4D * Minecart_speedplus.getSpeedMultiplier());

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleMove(VehicleMoveEvent event) {
		if (!(event.getVehicle() instanceof Minecart)) {
			// Not a Minecart
			return;
		}

		final Location from = event.getFrom();
		final Location to = event.getTo();
		final Location fromBlock = from.toBlockLocation();
		final Location toBlock = to.toBlockLocation();

		if (fromBlock.equals(toBlock)) {
			// We didn't move to a new block
			return;
		}

		final double travelled = to.toVector().distance(from.toVector());
		if (travelled > 1) {
			plugin.getLogger().warning(String.format("Moved more than 1 block since last time (%f)", travelled));
		}

		final Minecart cart = (Minecart) event.getVehicle();
		final int cartx = toBlock.getBlockX();
		final int carty = toBlock.getBlockY();
		final int cartz = toBlock.getBlockZ();
		for (int xmod : xmodifier) {
			for (int ymod : ymodifier) {
				for (int zmod : zmodifier) {
					final Block block = cart.getWorld().getBlockAt(cartx + xmod, carty + ymod, cartz + zmod);

					if (block.getState() instanceof Sign sign) {
						if (sign.getPersistentDataContainer().has(plugin.key_speed, PersistentDataType.DOUBLE)) {
							double speed = sign.getPersistentDataContainer().get(plugin.key_speed,
									PersistentDataType.DOUBLE);
							cart.setMaxSpeed(0.4D * speed);
						} else if (sign.getPersistentDataContainer().has(plugin.key_fly, PersistentDataType.BOOLEAN)) {
							Boolean fly = sign.getPersistentDataContainer().get(plugin.key_fly,
									PersistentDataType.BOOLEAN);
							if (fly) {
								cart.setFlyingVelocityMod(flyingmod);
							} else {
								cart.setFlyingVelocityMod(noflyingmod);
							}
						}
					}
				}
			}
		}
	}
}
