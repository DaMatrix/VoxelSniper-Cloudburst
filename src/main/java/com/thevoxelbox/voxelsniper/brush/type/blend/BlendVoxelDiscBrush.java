package com.thevoxelbox.voxelsniper.brush.type.blend;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.cloudburstmc.server.utils.TextFormat;
public class BlendVoxelDiscBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.GOLD + "Blend Voxel Disc Parameters:");
			messenger.sendMessage(TextFormat.AQUA + "/b bvd water -- toggle include or exclude (default) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		int squareEdge = 2 * brushSize + 1;
		int largeSquareArea = MathHelper.square(squareEdge + 2);
		Map<VectorVS, Block> largeSquare = new HashMap<>(largeSquareArea);
		Block targetBlock = getTargetBlock();
		Painters.square()
			.center(targetBlock)
			.radius(brushSize + 2)
			.blockSetter(position -> {
				Block block = getBlock(position);
				largeSquare.put(position, block);
			})
			.paint();
		int smallSquareArea = MathHelper.square(squareEdge);
		Map<VectorVS, Block> smallSquare = new HashMap<>(smallSquareArea);
		Map<VectorVS, Material> smallSquareMaterials = new HashMap<>(smallSquareArea);
		Painters.square()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Block block = largeSquare.get(position);
				smallSquare.put(position, block);
				smallSquareMaterials.put(position, block.getType());
			})
			.paint();
		for (Block smallSquareBlock : smallSquare.values()) {
			VectorVS blockPosition = Vectors.of(smallSquareBlock);
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.square()
				.center(smallSquareBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(blockPosition)) {
						return;
					}
					Block block = largeSquare.get(position);
					Material material = block.getType();
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallSquareMaterials.put(blockPosition, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallSquareMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
