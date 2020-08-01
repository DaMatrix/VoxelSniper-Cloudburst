package com.thevoxelbox.voxelsniper.brush.type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public class ErodeBrush extends AbstractBrush {

	private static final List<VectorVS> FACES_TO_CHECK = Arrays.asList(new VectorVS(0, 0, 1), new VectorVS(0, 0, -1), new VectorVS(0, 1, 0), new VectorVS(0, -1, 0), new VectorVS(1, 0, 0), new VectorVS(-1, 0, 0));

	private ErosionPreset currentPreset = new ErosionPreset(0, 1, 0, 1);

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			Preset preset = Preset.getPreset(parameter);
			if (preset != null) {
				try {
					this.currentPreset = preset.getPreset();
					messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Brush preset set to " + preset.getName());
					return;
				} catch (IllegalArgumentException exception) {
					messenger.sendMessage(TextFormat.LIGHT_PURPLE + "No such preset.");
					return;
				}
			}
			ErosionPreset currentPresetBackup = this.currentPreset;
			if (!parameter.isEmpty() && parameter.charAt(0) == 'f') {
				String fillFacesString = parameter.replace("f", "");
				Integer fillFaces = NumericParser.parseInteger(fillFacesString);
				if (fillFaces != null) {
					this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), fillFaces, this.currentPreset.getFillRecursion());
				}
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'e') {
				String erosionFacesString = parameter.replace("e", "");
				Integer erosionFaces = NumericParser.parseInteger(erosionFacesString);
				if (erosionFaces != null) {
					this.currentPreset = new ErosionPreset(erosionFaces, this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
				}
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'F') {
				String fillRecursionString = parameter.replace("F", "");
				Integer fillRecursion = NumericParser.parseInteger(fillRecursionString);
				if (fillRecursion != null) {
					this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), fillRecursion);
				}
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'E') {
				String erosionRecursionString = parameter.replace("E", "");
				Integer erosionRecursion = NumericParser.parseInteger(erosionRecursionString);
				if (erosionRecursion != null) {
					this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), erosionRecursion, this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
				}
			}
			if (!this.currentPreset.equals(currentPresetBackup)) {
				if (this.currentPreset.getErosionFaces() != currentPresetBackup.getErosionFaces()) {
					messenger.sendMessage(TextFormat.AQUA + "Erosion faces set to: " + TextFormat.WHITE + this.currentPreset.getErosionFaces());
				}
				if (this.currentPreset.getFillFaces() != currentPresetBackup.getFillFaces()) {
					messenger.sendMessage(TextFormat.AQUA + "Fill faces set to: " + TextFormat.WHITE + this.currentPreset.getFillFaces());
				}
				if (this.currentPreset.getErosionRecursion() != currentPresetBackup.getErosionRecursion()) {
					messenger.sendMessage(TextFormat.AQUA + "Erosion recursions set to: " + TextFormat.WHITE + this.currentPreset.getErosionRecursion());
				}
				if (this.currentPreset.getFillRecursion() != currentPresetBackup.getFillRecursion()) {
					messenger.sendMessage(TextFormat.AQUA + "Fill recursions set to: " + TextFormat.WHITE + this.currentPreset.getFillRecursion());
				}
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		erosion(snipe, this.currentPreset);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		erosion(snipe, this.currentPreset.getInverted());
	}

	private void erosion(Snipe snipe, ErosionPreset erosionPreset) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		Block targetBlock = getTargetBlock();
		Level targetBlockLevel = targetBlock.getLevel();
		BlockChangeTracker blockChangeTracker = new BlockChangeTracker(targetBlockLevel);
		Vector3f targetBlockVector = targetBlock.getPosition().toFloat();
		for (int i = 0; i < erosionPreset.getErosionRecursion(); ++i) {
			erosionIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
		}
		for (int i = 0; i < erosionPreset.getFillRecursion(); ++i) {
			fillIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
		}
		Undo undo = new Undo();
		for (BlockWrapper blockWrapper : blockChangeTracker.getAll()) {
			Block block = blockWrapper.getBlock();
			if (block != null) {
				BlockState blockData = blockWrapper.getBlockData();
				undo.put(block);
				block.set(blockData);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void fillIteration(ToolkitProperties toolkitProperties, ErosionPreset erosionPreset, BlockChangeTracker blockChangeTracker, Vector3f targetBlockVector) {
		int currentIteration = blockChangeTracker.nextIteration();
		Block targetBlock = getTargetBlock();
		int brushSize = toolkitProperties.getBrushSize();
		for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
			for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
				for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
					Vector3f currentPosition = Vector3f.from(x, y, z);
					if (currentPosition.distance(targetBlockVector) <= brushSize) {
						BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
						if (!(currentBlock.isEmpty() || currentBlock.isLiquid())) {
							continue;
						}
						int count = 0;
						Map<BlockWrapper, Integer> blockCount = new HashMap<>();
						for (VectorVS vector : FACES_TO_CHECK) {
							Vector3f relativePosition = Vectors.toBukkit(Vectors.of(currentPosition).plus(vector));
							BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);
							if (!(relativeBlock.isEmpty() || relativeBlock.isLiquid())) {
								count++;
								BlockWrapper typeBlock = new BlockWrapper(null, relativeBlock.getBlockData());
								if (blockCount.containsKey(typeBlock)) {
									blockCount.put(typeBlock, blockCount.get(typeBlock) + 1);
								} else {
									blockCount.put(typeBlock, 1);
								}
							}
						}
						BlockWrapper currentBlockWrapper = new BlockWrapper(null, BlockState.AIR);
						int amount = 0;
						for (BlockWrapper wrapper : blockCount.keySet()) {
							Integer currentCount = blockCount.get(wrapper);
							if (amount <= currentCount) {
								currentBlockWrapper = wrapper;
								amount = currentCount;
							}
						}
						if (count >= erosionPreset.getFillFaces()) {
							blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), currentBlockWrapper.getBlockData()), currentIteration);
						}
					}
				}
			}
		}
	}

	private void erosionIteration(ToolkitProperties toolkitProperties, ErosionPreset erosionPreset, BlockChangeTracker blockChangeTracker, Vector3f targetBlockVector) {
		int currentIteration = blockChangeTracker.nextIteration();
		Block targetBlock = this.getTargetBlock();
		int brushSize = toolkitProperties.getBrushSize();
		for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
			for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
				for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
					Vector3f currentPosition = Vector3f.from(x, y, z);
					if (currentPosition.distance(targetBlockVector) <= brushSize) {
						BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
						if (currentBlock.isEmpty() || currentBlock.isLiquid()) {
							continue;
						}
						int count = (int) FACES_TO_CHECK.stream()
							.map(vector -> Vectors.of(currentPosition).plus(vector))
							.map(Vectors::toBukkit)
							.map(relativePosition -> blockChangeTracker.get(relativePosition, currentIteration))
							.filter(relativeBlock -> relativeBlock.isEmpty() || relativeBlock.isLiquid())
							.count();
						if (count >= erosionPreset.getErosionFaces()) {
							blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), BlockState.AIR), currentIteration);
						}
					}
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendMessage(TextFormat.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
		messenger.sendMessage(TextFormat.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
		messenger.sendMessage(TextFormat.DARK_BLUE + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
		messenger.sendMessage(TextFormat.DARK_GREEN + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
	}

	private enum Preset {

		MELT("melt", new ErosionPreset(2, 1, 5, 1)),
		FILL("fill", new ErosionPreset(5, 1, 2, 1)),
		SMOOTH("smooth", new ErosionPreset(3, 1, 3, 1)),
		LIFT("lift", new ErosionPreset(6, 0, 1, 1)),
		FLOAT_CLEAN("floatclean", new ErosionPreset(6, 1, 6, 1));

		private String name;
		private ErosionPreset preset;

		Preset(String name, ErosionPreset preset) {
			this.name = name;
			this.preset = preset;
		}

		@Nullable
		public static Preset getPreset(String name) {
			return Arrays.stream(values())
				.filter(preset -> preset.name.equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
		}

		public String getName() {
			return this.name;
		}

		public ErosionPreset getPreset() {
			return this.preset;
		}
	}

	private static final class BlockChangeTracker {

		private Map<Integer, Map<Vector3f, BlockWrapper>> blockChanges;
		private Map<Vector3f, BlockWrapper> flatChanges;
		private Level world;
		private int nextIterationId;

		private BlockChangeTracker(Level world) {
			this.blockChanges = new HashMap<>();
			this.flatChanges = new HashMap<>();
			this.world = world;
		}

		public BlockWrapper get(Vector3f position, int iteration) {
			for (int i = iteration - 1; i >= 0; --i) {
				if (this.blockChanges.containsKey(i) && this.blockChanges.get(i)
					.containsKey(position)) {
					return this.blockChanges.get(i)
						.get(position);
				}
			}
			return new BlockWrapper(this.world.getBlock(position));
		}

		public Collection<BlockWrapper> getAll() {
			return this.flatChanges.values();
		}

		public int nextIteration() {
			int nextIterationId = this.nextIterationId;
			this.nextIterationId++;
			return nextIterationId;
		}

		public void put(Vector3f position, BlockWrapper changedBlock, int iteration) {
			if (!this.blockChanges.containsKey(iteration)) {
				this.blockChanges.put(iteration, new HashMap<>());
			}
			this.blockChanges.get(iteration)
				.put(position, changedBlock);
			this.flatChanges.put(position, changedBlock);
		}
	}

	private static final class BlockWrapper {

		@Nullable
		private Block block;
		private BlockState blockData;

		private BlockWrapper(Block block) {
			this(block, block.getState());
		}

		private BlockWrapper(@Nullable Block block, BlockState blockData) {
			this.block = block;
			this.blockData = blockData;
		}

		@Nullable
		public Block getBlock() {
			return this.block;
		}

		public BlockState getBlockData() {
			return this.blockData;
		}

		public boolean isEmpty() {
			Identifier material = this.blockData.getType();
			return Materials.isEmpty(material);
		}

		public boolean isLiquid() {
			Identifier material = this.blockData.getType();
			return material == BlockTypes.WATER || material == BlockTypes.LAVA;
		}
	}

	private static final class ErosionPreset implements Serializable {

		private static final long serialVersionUID = 8997952776355430411L;

		private final int erosionFaces;
		private final int erosionRecursion;
		private final int fillFaces;
		private final int fillRecursion;

		private ErosionPreset(int erosionFaces, int erosionRecursion, int fillFaces, int fillRecursion) {
			this.erosionFaces = erosionFaces;
			this.erosionRecursion = erosionRecursion;
			this.fillFaces = fillFaces;
			this.fillRecursion = fillRecursion;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.erosionFaces, this.erosionRecursion, this.fillFaces, this.fillRecursion);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ErosionPreset) {
				ErosionPreset other = (ErosionPreset) obj;
				return this.erosionFaces == other.erosionFaces && this.erosionRecursion == other.erosionRecursion && this.fillFaces == other.fillFaces && this.fillRecursion == other.fillRecursion;
			}
			return false;
		}

		/**
		 * @return the erosionFaces
		 */
		public int getErosionFaces() {
			return this.erosionFaces;
		}

		/**
		 * @return the erosionRecursion
		 */
		public int getErosionRecursion() {
			return this.erosionRecursion;
		}

		/**
		 * @return the fillFaces
		 */
		public int getFillFaces() {
			return this.fillFaces;
		}

		/**
		 * @return the fillRecursion
		 */
		public int getFillRecursion() {
			return this.fillRecursion;
		}

		public ErosionPreset getInverted() {
			return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
		}
	}
}
