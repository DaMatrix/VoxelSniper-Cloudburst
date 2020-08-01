package com.thevoxelbox.voxelsniper.brush.type.stamp;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public abstract class AbstractStampBrush extends AbstractBrush {

	protected Set<StampBrushBlockWrapper> clone = new HashSet<>();
	protected Set<StampBrushBlockWrapper> fall = new HashSet<>();
	protected Set<StampBrushBlockWrapper> drop = new HashSet<>();
	protected Set<StampBrushBlockWrapper> solid = new HashSet<>();
	protected Undo undo;
	protected boolean sorted;
	protected StampType stamp = StampType.DEFAULT;

	@Override
	public void handleArrowAction(Snipe snipe) {
		switch (this.stamp) {
			case DEFAULT:
				stamp(snipe);
				break;
			case NO_AIR:
				stampNoAir(snipe);
				break;
			case FILL:
				stampFill(snipe);
				break;
			default:
				SnipeMessenger messenger = snipe.createMessenger();
				messenger.sendMessage(TextFormat.DARK_RED + "Error while stamping! Report");
				break;
		}
	}

	public void reSort() {
		this.sorted = false;
	}

	protected boolean falling(Identifier material) {
		return MaterialSets.FALLING.contains(material);
	}

	protected boolean fallsOff(Identifier material) {
		MaterialSet fallsOff = MaterialSet.builder()
			.add(BlockTypes.SAPLING)
			.add(BlockTypes.BAMBOO_SAPLING)
			.add(BlockTypes.WOODEN_DOOR)
			.add(BlockTypes.IRON_DOOR)
			.add(BlockTypes.SPRUCE_DOOR)
			.add(BlockTypes.BIRCH_DOOR)
			.add(BlockTypes.JUNGLE_DOOR)
			.add(BlockTypes.ACACIA_DOOR)
			.add(BlockTypes.DARK_OAK_DOOR)
			.add(BlockTypes.RAIL)
			.add(BlockTypes.ACTIVATOR_RAIL)
			.add(BlockTypes.DETECTOR_RAIL)
			.add(BlockTypes.GOLDEN_RAIL)
			.add(BlockTypes.ACACIA_BUTTON)
			.add(BlockTypes.BIRCH_BUTTON)
			.add(BlockTypes.DARK_OAK_BUTTON)
			.add(BlockTypes.JUNGLE_BUTTON)
			.add(BlockTypes.SPRUCE_BUTTON)
			.add(BlockTypes.STONE_BUTTON)
			.add(BlockTypes.WOODEN_BUTTON)
			.with(MaterialSets.SIGNS)
			.with(MaterialSets.PRESSURE_PLATES)
			.with(MaterialSets.FLOWERS)
			.with(MaterialSets.MUSHROOMS)
			.with(MaterialSets.TORCHES)
			.with(MaterialSets.REDSTONE_TORCHES)
			.add(BlockTypes.FIRE)
			.add(BlockTypes.REDSTONE_WIRE)
			.add(BlockTypes.WHEAT)
			.add(BlockTypes.LADDER)
			.add(BlockTypes.LEVER)
			.add(BlockTypes.SNOW)
			.add(BlockTypes.REEDS)
			.add(BlockTypes.POWERED_REPEATER)
			.add(BlockTypes.UNPOWERED_REPEATER)
			.add(BlockTypes.POWERED_COMPARATOR)
			.add(BlockTypes.UNPOWERED_COMPARATOR)
			.build();
		return fallsOff.contains(material);
	}

	protected void setBlock(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		this.undo.put(block);
		block.set(blockWrapper.getBlockData());
	}

	protected void setBlockFill(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		if (Materials.isEmpty(block.getState().getType())) {
			this.undo.put(block);
			block.set(blockWrapper.getBlockData());
		}
	}

	protected void stamp(Snipe snipe) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockState blockData = block.getBlockData();
				Identifier material = blockData.getType();
				if (this.fallsOff(material)) {
					this.fall.add(block);
				} else if (this.falling(material)) {
					this.drop.add(block);
				} else {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	protected void stampFill(Snipe snipe) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockState blockData = block.getBlockData();
				Identifier material = blockData.getType();
				if (fallsOff(material)) {
					this.fall.add(block);
				} else if (falling(material)) {
					this.drop.add(block);
				} else if (!Materials.isEmpty(material)) {
					this.solid.add(block);
					this.setBlockFill(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
			this.sorted = true;
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	protected void stampNoAir(Snipe snipe) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockState blockData = block.getBlockData();
				Identifier material = blockData.getType();
				if (this.fallsOff(material)) {
					this.fall.add(block);
				} else if (this.falling(material)) {
					this.drop.add(block);
				} else if (!Materials.isEmpty(material)) {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	public StampType getStamp() {
		return this.stamp;
	}

	public void setStamp(StampType stamp) {
		this.stamp = stamp;
	}
}
