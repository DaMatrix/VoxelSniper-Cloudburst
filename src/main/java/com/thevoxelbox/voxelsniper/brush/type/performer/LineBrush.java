package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.math.BlockRayTrace;
import org.cloudburstmc.server.utils.TextFormat;

import java.util.Iterator;

public class LineBrush extends AbstractPerformerBrush {

	private static final Vector3f HALF_BLOCK_OFFSET = Vector3f.from(0.5, 0.5, 0.5);
	private Vector3f originCoordinates;
	private Vector3f targetCoordinates = Vector3f.ZERO;
	private Level targetLevel;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		if (parameters[0].equalsIgnoreCase("info")) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		this.originCoordinates = targetBlock.getPosition().toFloat();
		this.targetLevel = targetBlock.getLevel();
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(TextFormat.DARK_PURPLE + "First point selected.");
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		Level world = getLevel();
		if (this.originCoordinates == null || !world.equals(this.targetLevel)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			this.targetCoordinates = targetBlock.getPosition().toFloat();
			linePowder(snipe);
		}
	}

	private void linePowder(Snipe snipe) {
		Vector3f originClone = this.originCoordinates.add(HALF_BLOCK_OFFSET);
		Vector3f targetClone = this.targetCoordinates.add(HALF_BLOCK_OFFSET);
		Vector3f direction = targetClone.sub(originClone);
		double length = this.targetCoordinates.distance(this.originCoordinates);
		if (length == 0) {
			this.performer.perform(this.targetLevel.getBlock(this.targetCoordinates));
		} else {
			Iterator<Vector3i> blockIterator = BlockRayTrace.of(originClone, direction, Math.round(length)).iterator();
			while (blockIterator.hasNext()) {
				Block currentBlock = this.targetLevel.getBlock(blockIterator.next());
				this.performer.perform(currentBlock);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
