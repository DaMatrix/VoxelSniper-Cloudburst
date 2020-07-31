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
public class BlendVoxelBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.GOLD + "Blend Voxel Parameters:");
			messenger.sendMessage(TextFormat.AQUA + "/b bv water -- toggle include or exclude (default) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		int cubeEdge = 2 * brushSize + 1;
		int largeCubeVolume = MathHelper.cube(cubeEdge + 2);
		Map<VectorVS, Block> largeCube = new HashMap<>(largeCubeVolume);
		Block targetBlock = getTargetBlock();
		Painters.cube()
			.center(targetBlock)
			.radius(brushSize + 2)
			.blockSetter(position -> {
				Block block = getBlock(position);
				largeCube.put(position, block);
			})
			.paint();
		int smallCubeVolume = MathHelper.cube(cubeEdge);
		Map<VectorVS, Block> smallCube = new HashMap<>(smallCubeVolume);
		Map<VectorVS, Material> smallCubeMaterials = new HashMap<>(smallCubeVolume);
		Painters.cube()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Block block = largeCube.get(position);
				smallCube.put(position, block);
				smallCubeMaterials.put(position, block.getType());
			})
			.paint();
		for (Block smallCubeBlock : smallCube.values()) {
			VectorVS blockPosition = Vectors.of(smallCubeBlock);
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.cube()
				.center(smallCubeBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(blockPosition)) {
						return;
					}
					Block block = largeCube.get(position);
					Material material = block.getType();
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallCubeMaterials.put(blockPosition, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallCubeMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
