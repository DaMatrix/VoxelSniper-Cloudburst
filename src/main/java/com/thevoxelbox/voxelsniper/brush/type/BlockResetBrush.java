package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.utils.Identifier;

public class BlockResetBrush extends AbstractBrush {

	private static final MaterialSet DENIED_UPDATES = MaterialSet.builder()
		.add(BlockTypes.WOODEN_DOOR)
		.add(BlockTypes.IRON_DOOR)
		.add(BlockTypes.SPRUCE_DOOR)
		.add(BlockTypes.BIRCH_DOOR)
		.add(BlockTypes.JUNGLE_DOOR)
		.add(BlockTypes.ACACIA_DOOR)
		.add(BlockTypes.DARK_OAK_DOOR)
		.with(MaterialSets.SIGNS)
		.with(MaterialSets.CHESTS)
		.with(MaterialSets.REDSTONE_TORCHES)
		.with(MaterialSets.FENCE_GATES)
		.add(BlockTypes.FURNACE)
		.add(BlockTypes.REDSTONE_WIRE)
		.add(BlockTypes.UNPOWERED_REPEATER)
		.add(BlockTypes.POWERED_REPEATER)
		.build();

	@Override
	public void handleArrowAction(Snipe snipe) {
		applyBrush(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		applyBrush(snipe);
	}

	private void applyBrush(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = -brushSize; z <= brushSize; z++) {
			for (int x = -brushSize; x <= brushSize; x++) {
				for (int y = -brushSize; y <= brushSize; y++) {
					Level world = getLevel();
					Block targetBlock = getTargetBlock();
					Block block = world.getBlock(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
					Identifier blockType = block.getState().getType();
					if (!DENIED_UPDATES.contains(blockType)) {
						block.set(BlockState.get(blockType), true);
					}
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
