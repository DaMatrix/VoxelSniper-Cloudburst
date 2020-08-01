package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;
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
		Level world = this.targetBlock.getLevel();
		if (clampedY < 0) {
			clampedY = 0;
		} else {
			int maxHeight = 256;
			if (clampedY > maxHeight) {
				clampedY = maxHeight;
			}
		}
		return getBlock(x, clampedY, z);
	}

	public Identifier getBlockType(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockType(x, y, z);
	}

	public Identifier getBlockType(int x, int y, int z) {
		Block block = getBlock(x, y, z);
		return block.getState().getType();
	}

	public BlockState getBlockData(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockData(x, y, z);
	}

	public BlockState getBlockData(int x, int y, int z) {
		Block block = getBlock(x, y, z);
		return block.getState();
	}

	public void setBlockType(VectorVS position, Identifier type) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockType(x, y, z, type);
	}

	public void setBlockType(int x, int y, int z, Identifier type) {
		Block block = getBlock(x, y, z);
		block.set(BlockState.get(type));
	}

	public void setBlockData(VectorVS position, BlockState blockData) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockData(x, y, z, blockData);
	}

	public void setBlockData(int x, int y, int z, BlockState blockData) {
		Block block = getBlock(x, y, z);
		block.set(blockData);
	}

	public Block getBlock(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlock(x, y, z);
	}

	public Block getBlock(int x, int y, int z) {
		Level world = getLevel();
		return world.getBlock(x, y, z);
	}

	public Level getLevel() {
		return this.targetBlock.getLevel();
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
