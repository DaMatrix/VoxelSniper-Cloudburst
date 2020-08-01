package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Iterator;
import java.util.Random;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.math.BlockRayTrace;
import org.cloudburstmc.server.utils.TextFormat;

public class JaggedLineBrush extends AbstractPerformerBrush {

	private static final Vector3f HALF_BLOCK_OFFSET = Vector3f.from(0.5, 0.5, 0.5);
	private static final int RECURSION_MIN = 1;
	private static final int RECURSION_DEFAULT = 3;
	private static final int RECURSION_MAX = 10;
	private static final int SPREAD_DEFAULT = 3;

	private Random random = new Random();
	private Vector3f originCoordinates;
	private Vector3f targetCoordinates = Vector3f.ZERO;
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
			this.originCoordinates = Vector3f.ZERO;
		}
		Block targetBlock = getTargetBlock();
		this.originCoordinates = targetBlock.getPosition().toFloat();
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
			this.targetCoordinates = targetBlock.getPosition().toFloat();
			jaggedP(snipe);
		}
	}

	private void jaggedP(Snipe snipe) {
		Vector3f originClone = this.originCoordinates.add(HALF_BLOCK_OFFSET);
		Vector3f targetClone = this.targetCoordinates.add(HALF_BLOCK_OFFSET);
		Vector3f direction = targetClone.sub(originClone);
		double length = this.targetCoordinates.distance(this.originCoordinates);
		Level world = getLevel();
		if (length == 0) {
			this.performer.perform(world.getBlock(this.targetCoordinates));
		} else {
			Iterator<Vector3i> iterator = BlockRayTrace.of(originClone, direction, Math.round(length)).iterator();
			while (iterator.hasNext()) {
				Block block = world.getBlock(iterator.next());
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
