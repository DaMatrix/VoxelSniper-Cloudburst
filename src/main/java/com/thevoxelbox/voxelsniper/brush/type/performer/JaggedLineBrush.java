package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Random;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.utils.TextFormat;
public class JaggedLineBrush extends AbstractPerformerBrush {

	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private static final int RECURSION_MIN = 1;
	private static final int RECURSION_DEFAULT = 3;
	private static final int RECURSION_MAX = 10;
	private static final int SPREAD_DEFAULT = 3;

	private Random random = new Random();
	private Vector originCoordinates;
	private Vector targetCoordinates = new Vector();
	private int recursion = RECURSION_DEFAULT;
	private int spread = SPREAD_DEFAULT;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
				messenger.sendMessage(TextFormat.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
				messenger.sendMessage(TextFormat.AQUA + "/b j s# - sets the spread (default 3, must be 1-10)");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'r') {
				Integer temp = NumericParser.parseInteger(parameter.substring(1));
				if (temp == null) {
					messenger.sendMessage(TextFormat.RED + String.format("Exception while parsing parameter: %s", parameter));
					return;
				}
				if (temp >= RECURSION_MIN && temp <= RECURSION_MAX) {
					this.recursion = temp;
					messenger.sendMessage(TextFormat.GREEN + "Recursion set to: " + this.recursion);
				} else {
					messenger.sendMessage(TextFormat.RED + "ERROR: Recursion must be " + RECURSION_MIN + "-" + RECURSION_MAX);
				}
				return;
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 's') {
				Integer spread = NumericParser.parseInteger(parameter.substring(1));
				if (spread == null) {
					messenger.sendMessage(TextFormat.RED + String.format("Exception while parsing parameter: %s", parameter));
					return;
				}
				this.spread = spread;
				messenger.sendMessage(TextFormat.GREEN + "Spread set to: " + this.spread);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		if (this.originCoordinates == null) {
			this.originCoordinates = new Vector();
		}
		Block targetBlock = getTargetBlock();
		Location targetBlockLocation = targetBlock.getLocation();
		this.originCoordinates = targetBlockLocation.toVector();
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(TextFormat.DARK_PURPLE + "First point selected.");
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		if (this.originCoordinates == null) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			Block targetBlock = getTargetBlock();
			Location targetBlockLocation = targetBlock.getLocation();
			this.targetCoordinates = targetBlockLocation.toVector();
			jaggedP(snipe);
		}
	}

	private void jaggedP(Snipe snipe) {
		Vector originClone = new Vector().
			copy(this.originCoordinates)
			.add(HALF_BLOCK_OFFSET);
		Vector targetClone = new Vector().
			copy(this.targetCoordinates)
			.add(HALF_BLOCK_OFFSET);
		Vector direction = new Vector().
			copy(targetClone)
			.subtract(originClone);
		double length = this.targetCoordinates.distance(this.originCoordinates);
		World world = getWorld();
		if (length == 0) {
			Location location = this.targetCoordinates.toLocation(world);
			this.performer.perform(location.getBlock());
		} else {
			BlockIterator iterator = new BlockIterator(world, originClone, direction, 0, NumberConversions.round(length));
			while (iterator.hasNext()) {
				Block block = iterator.next();
				for (int i = 0; i < this.recursion; i++) {
					this.performer.perform(clampY(Math.round(block.getX() + this.random.nextInt(this.spread * 2) - this.spread), Math.round(block.getY() + this.random.nextInt(this.spread * 2) - this.spread), Math.round(block.getZ() + this.random.nextInt(this.spread * 2) - this.spread)));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(TextFormat.GRAY + "Recursion set to: " + this.recursion)
			.message(TextFormat.GRAY + "Spread set to: " + this.spread)
			.send();
	}
}
