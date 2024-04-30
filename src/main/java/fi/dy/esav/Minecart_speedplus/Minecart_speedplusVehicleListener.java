package fi.dy.esav.Minecart_speedplus;

import org.bukkit.Location;
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

	Block block;

	public static Minecart_speedplus plugin;

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

		final double travelled = to.toVector().distance(from.toVector());

		if (travelled > 1) {
			plugin.getLogger().warning(String.format("Moved more than 1 block since last time (%f)", travelled));
		}

		final Minecart cart = (Minecart) event.getVehicle();

		final Location location = fromBlockLocation.clone();

		// Yes, I know this potentially screws with sign order.
		// Deal with it, or figure out BlockIterator.
		final int xs = Math.min(from.getBlockX(), to.getBlockX());
		final int ys = Math.min(from.getBlockY(), to.getBlockY());
		final int zs = Math.min(from.getBlockZ(), to.getBlockZ());

		final int xl = Math.max(from.getBlockX(), to.getBlockX());
		final int yl = Math.max(from.getBlockY(), to.getBlockY());
		final int zl = Math.max(from.getBlockZ(), to.getBlockZ());

		for (int x = xs; x <= xl; x++) {
			for (int z = zs; z <= zl; z++) {
				for (int y = ys; y <= yl; y++) {
					location.setX(x);
					location.setY(y);
					location.setZ(z);

					if (location.equals(fromBlockLocation)) {
						// Could probably do some vector magic above to avoid this
						continue;
					}

					StringBuilder sb = new StringBuilder("Materials: ");
					boolean si = false;
					for (int i = 3; i >= 2; i--) {
						// y-0 == rail
						// y-1 == block rail is placed on
						location.setY(y - i);
						Block below = location.getBlock();
						sb.append(below.getType().toString());
						sb.append(", ");
						if (below.getState() instanceof Sign sign) {
							si = true;
							if (sign.getPersistentDataContainer().has(plugin.key_speed, PersistentDataType.DOUBLE)) {
								double speed = sign.getPersistentDataContainer().get(plugin.key_speed,
										PersistentDataType.DOUBLE);
								cart.setMaxSpeed(0.4D * speed);
							} else if (sign.getPersistentDataContainer().has(plugin.key_fly,
									PersistentDataType.BOOLEAN)) {
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
					if (si) {
						// plugin.getLogger().info(location.toString());
						plugin.getLogger().info(sb.toString());
					}
				}
			}
		}
	}
}
