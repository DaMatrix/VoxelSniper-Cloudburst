package com.thevoxelbox.voxelsniper.brush.type;

import java.util.ArrayList;
import java.util.List;

import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import net.daporkchop.lib.math.primitive.PMath;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;

public class DomeBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		generateDome(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		generateDome(snipe, lastBlock);
	}

	private void generateDome(Snipe snipe, Block block) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int voxelHeight = toolkitProperties.getVoxelHeight();
		if (voxelHeight == 0) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage("VoxelHeight must not be 0.");
			return;
		}
		int absoluteHeight = Math.abs(voxelHeight);
		boolean negative = voxelHeight < 0;
		List<Vector3f> changeablePositions = new ArrayList<>();
		Undo undo = new Undo();
		int brushSize = toolkitProperties.getBrushSize();
		int brushSizeTimesVoxelHeight = brushSize * absoluteHeight;
		double stepScale = (brushSize * brushSize + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 5.0;
		double stepSize = 1.0 / stepScale;
		for (double u = 0; u <= Math.PI / 2; u += stepSize) {
			double y = absoluteHeight * Math.sin(u);
			for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize) {
				double x = brushSize * Math.cos(u) * Math.cos(stepV);
				double z = brushSize * Math.cos(u) * Math.sin(stepV);
				double targetBlockX = block.getX() + 0.5;
				double targetBlockZ = block.getZ() + 0.5;
				int targetY = PMath.floorI(block.getY() + (negative ? -y : y));
				int currentBlockXAdd = PMath.floorI(targetBlockX + x);
				int currentBlockZAdd = PMath.floorI(targetBlockZ + z);
				int currentBlockXSubtract = PMath.floorI(targetBlockX - x);
				int currentBlockZSubtract = PMath.floorI(targetBlockZ - z);
				changeablePositions.add(Vector3f.from(currentBlockXAdd, targetY, currentBlockZAdd));
				changeablePositions.add(Vector3f.from(currentBlockXSubtract, targetY, currentBlockZAdd));
				changeablePositions.add(Vector3f.from(currentBlockXAdd, targetY, currentBlockZSubtract));
				changeablePositions.add(Vector3f.from(currentBlockXSubtract, targetY, currentBlockZSubtract));
			}
		}
		Level world = getLevel();
		for (Vector3f vector : changeablePositions) {
			Block currentTargetBlock = world.getBlock(vector);
			BlockState currentTargetBlockBlockData = currentTargetBlock.getState();
			BlockState snipeBlockData = toolkitProperties.getBlockData();
			if (!currentTargetBlockBlockData.equals(snipeBlockData)) {
				undo.put(currentTargetBlock);
				currentTargetBlock.set(snipeBlockData);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendBlockTypeMessage();
		messenger.sendVoxelHeightMessage();
	}
}
