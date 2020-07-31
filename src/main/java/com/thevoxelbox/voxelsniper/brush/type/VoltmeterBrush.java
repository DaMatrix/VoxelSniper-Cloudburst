package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
public class VoltmeterBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		volt(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		data(snipe);
	}

	private void data(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof AnaloguePowerable)) {
			return;
		}
		AnaloguePowerable analoguePowerable = (AnaloguePowerable) blockData;
		int power = analoguePowerable.getPower();
		messenger.sendMessage(TextFormat.AQUA + "Blocks until repeater needed: " + power);
	}

	private void volt(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		boolean indirect = block.isBlockIndirectlyPowered();
		boolean direct = block.isBlockPowered();
		messenger.sendMessage(TextFormat.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
		messenger.sendMessage(TextFormat.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
		messenger.sendMessage(TextFormat.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
		messenger.sendMessage(TextFormat.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
		messenger.sendMessage(TextFormat.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
		messenger.sendMessage(TextFormat.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
		messenger.sendMessage(TextFormat.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
	}
}
