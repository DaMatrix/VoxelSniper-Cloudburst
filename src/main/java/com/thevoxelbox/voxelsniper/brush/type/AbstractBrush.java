package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.utils.TextFormat;
public abstract class AbstractBrush implements Brush {

	protected static final int CHUNK_SIZE = 16;

	private Block targetBlock;
	private Block lastBlock;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		player.sendMessage(TextFormat.RED + "This brush does not accept additional parameters.");
	}

	@Override
	public void perform(Snipe snipe, ToolAction action, Block targetBlock, Block lastBlock) {
		this.targetBlock = targetBlock;
		this.lastBlock = lastBlock;
		if (action == ToolAction.ARROW) {
			handleArrowAction(snipe);
		} else if (action == ToolAction.GUNPOWDER) {
			handleGunpowderAction(snipe);
		}
	}

	public Block clampY(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return clampY(x, y, z);
	}

	public Block clampY(int x, int y, int z) {
		int clampedY = y;
		World world = this.targetBlock.getWorld();
		if (clampedY < 0) {
			clampedY = 0;
		} else {
			int maxHeight = world.getMaxHeight();
			if (clampedY > maxHeight) {
				clampedY = maxHeight;
			}
		}
		return getBlock(x, clampedY, z);
	}

	public Material getBlockType(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockType(x, y, z);
	}

	public Material getBlockType(int x, int y, int z) {
		Block block = getBlock(x, y, z);
		return block.getType();
	}

	public BlockData getBlockData(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockData(x, y, z);
	}

	public BlockData getBlockData(int x, int y, int z) {
		Block block = getBlock(x, y, z);
		return block.getBlockData();
	}

	public void setBlockType(VectorVS position, Material type) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockType(x, y, z, type);
	}

	public void setBlockType(int x, int y, int z, Material type) {
		Block block = getBlock(x, y, z);
		block.setType(type);
	}

	public void setBlockData(VectorVS position, BlockData blockData) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockData(x, y, z, blockData);
	}

	public void setBlockData(int x, int y, int z, BlockData blockData) {
		Block block = getBlock(x, y, z);
		block.setBlockData(blockData);
	}

	public Block getBlock(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlock(x, y, z);
	}

	public Block getBlock(int x, int y, int z) {
		World world = getWorld();
		return world.getBlockAt(x, y, z);
	}

	public World getWorld() {
		return this.targetBlock.getWorld();
	}

	public Block getTargetBlock() {
		return this.targetBlock;
	}

	/**
	 * @return Block before target Block.
	 */
	public Block getLastBlock() {
		return this.lastBlock;
	}
}
