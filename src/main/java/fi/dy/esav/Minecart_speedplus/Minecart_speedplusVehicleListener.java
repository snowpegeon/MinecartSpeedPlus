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
import org.bukkit.util.BlockIterator;
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
		final Location fromBlockLocation = from.toBlockLocation();
		fromBlockLocation.setPitch(0);
		fromBlockLocation.setYaw(0);

		final Location to = event.getTo();
		final Location toBlockLocation = to.toBlockLocation();
		toBlockLocation.setPitch(0);
		toBlockLocation.setYaw(0);

		if (fromBlockLocation.equals(toBlockLocation)) {
			// We didn't move to a new block
			return;
		}

		Block b = fromBlockLocation.getBlock();



		final double travelled = to.toVector().distance(from.toVector());

		if (travelled > 1) {
			plugin.getLogger().warning(String.format("Moved more than 1 block since last time (%f)", travelled));
		}
		plugin.getLogger().info(from.toString());
		plugin.getLogger().info(to.toString());
		plugin.getLogger().info(fromBlockLocation.toString());
		plugin.getLogger().info(toBlockLocation.toString());

		final Vector direction = from.toVector().subtract(to.toVector()).normalize();
		final int blockDistance = (int) Math.ceil(toBlockLocation.toVector().distance(fromBlockLocation.toVector()));
		plugin.getLogger().info(Integer.toString(blockDistance));
		plugin.getLogger().info(direction.toString());

		Location fcopy = from.clone().setDirection(direction);
		plugin.getLogger().info(fcopy.toString());

		int maxDistance = Math.max(Math.min(blockDistance, 20), 1);
		plugin.getLogger().info(Integer.toString(maxDistance));

		BlockIterator bi = new BlockIterator(from.getWorld(), fromBlockLocation.toVector(), direction, 0.0D, 3);

		final Minecart cart = (Minecart) event.getVehicle();
		plugin.getLogger().info(cart.getVelocity().toString());
		int j = 0;

		for (Location location = bi.next().getLocation(); bi.hasNext();) {
			if (j++ >= 20) {
				plugin.getLogger().warning("Infinite loop detected");
				break;
			}
			plugin.getLogger().info(location.toString());

			if (location.equals(fromBlockLocation)) {
				// Could probably do some vector magic above to avoid this
				continue;
			}
			StringBuilder sb = new StringBuilder("Materials: ");
			for (int i = 1; i <= 3; i++) {
				Block below = location.subtract(0, i, 0).getBlock();
				sb.append(below.getType().toString());
				sb.append(", ");
				if (below.getState() instanceof Sign sign) {
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
			plugin.getLogger().info(sb.toString());
		}
	}
}
