package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTraits;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;

public class SnowConeBrush extends AbstractBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		String firstParameter = parameters[0];
		if (firstParameter.equalsIgnoreCase("info")) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GOLD + "Snow Cone Parameters:");
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getState().getType() == BlockTypes.SNOW) {
			addSnow(snipe, targetBlock);
		} else {
			Block blockAbove = targetBlock.up();
			Identifier type = blockAbove.getState().getType();
			if (Materials.isEmpty(type)) {
				addSnow(snipe, blockAbove);
			} else {
				SnipeMessenger messenger = snipe.createMessenger();
				messenger.sendMessage(TextFormat.RED + "Error: Center block neither snow nor air.");
			}
		}
	}

	private void addSnow(Snipe snipe, Block targetBlock) {
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		int brushSize = Materials.isEmpty(getBlockType(blockPositionX, blockPositionY, blockPositionZ)) ? 0 : blockDataToSnowLayers(clampY(blockPositionX, blockPositionY, blockPositionZ).getState()) + 1;
		int brushSizeDoubled = 2 * brushSize;
		Identifier[][] snowCone = new Identifier[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold block IDs
		BlockState[][] snowConeData = new BlockState[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold data values for snowCone
		int[][] yOffset = new int[brushSizeDoubled + 1][brushSizeDoubled + 1];
		// prime the arrays
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int z = 0; z <= brushSizeDoubled; z++) {
				boolean flag = true;
				for (int i = 0; i < 10; i++) { // overlay
					if (flag) {
						if ((Materials.isEmpty(getBlockType(blockPositionX - brushSize + x, blockPositionY - i, blockPositionZ - brushSize + z)) || getBlockType(blockPositionX - brushSize + x, blockPositionY - i, blockPositionZ - brushSize + z) == BlockTypes.SNOW_LAYER) && !Materials.isEmpty(getBlockType(blockPositionX - brushSize + x, blockPositionY - i - 1, blockPositionZ - brushSize + z)) && getBlockType(blockPositionX - brushSize + x, blockPositionY - i - 1, blockPositionZ - brushSize + z) != BlockTypes.SNOW_LAYER) {
							flag = false;
							yOffset[x][z] = i;
						}
					}
				}
				snowCone[x][z] = getBlockType(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z);
				snowConeData[x][z] = clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).getState();
			}
		}
		// figure out new snowheights
		for (int x = 0; x <= brushSizeDoubled; x++) {
			double xSquared = Math.pow(x - brushSize, 2);
			for (int z = 0; z <= 2 * brushSize; z++) {
				double zSquared = Math.pow(z - brushSize, 2);
				double dist = Math.pow(xSquared + zSquared, 0.5); // distance from center of array
				int snowData = brushSize - (int) Math.ceil(dist);
				if (snowData >= 0) { // no funny business
					// Increase snowtile size, if smaller than target
					if (snowData == 0) {
						if (Materials.isEmpty(snowCone[x][z])) {
							snowCone[x][z] = BlockTypes.SNOW_LAYER;
							snowConeData[x][z] = BlockState.get(BlockTypes.SNOW_LAYER);
						}
					} else if (snowData == 7) { // Turn largest snowtile into snowblock
						if (snowCone[x][z] == BlockTypes.SNOW_LAYER) {
							snowCone[x][z] = BlockTypes.SNOW;
							snowConeData[x][z] = BlockState.get(BlockTypes.SNOW);
						}
					} else {
						if (snowData > blockDataToSnowLayers(snowConeData[x][z])) {
							if (Materials.isEmpty(snowCone[x][z])) {
								snowConeData[x][z] = setSnowLayers(snowConeData[x][z], snowData);
								snowCone[x][z] = BlockTypes.SNOW_LAYER;
							} else if (snowCone[x][z] == BlockTypes.SNOW_LAYER) {
								snowConeData[x][z] = setSnowLayers(snowConeData[x][z], snowData);
							}
						} else if (yOffset[x][z] > 0 && snowCone[x][z] == BlockTypes.SNOW_LAYER) {
							snowConeData[x][z] = setSnowLayers(snowConeData[x][z], blockDataToSnowLayers(snowConeData[x][z]) + 1);
							if (blockDataToSnowLayers(snowConeData[x][z]) == 7) {
								snowConeData[x][z] = BlockState.get(BlockTypes.SNOW_LAYER);
								snowCone[x][z] = BlockTypes.SNOW;
							}
						}
					}
				}
			}
		}
		Undo undo = new Undo();
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int z = 0; z <= brushSizeDoubled; z++) {
				if (getBlockType(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z) != snowCone[x][z] || !clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).getState()
					.equals(snowConeData[x][z])) {
					undo.put(clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z));
				}
				setBlockType(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z, snowCone[x][z]);
				clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).set(snowConeData[x][z]);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private int blockDataToSnowLayers(BlockState blockData) {
		//TODO: snow layers
		// return blockData.getTrait(BlockTraits.SNOW_LAYERS);
		return 0;
	}

	private BlockState setSnowLayers(BlockState blockData, int layers) {
		//return blockData.withTrait(BlockTraits.SNOW_LAYERS, layers);
		return blockData;
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
