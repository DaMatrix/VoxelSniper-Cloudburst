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
public class BlendBallBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.GOLD + "Blend Ball Parameters:");
			messenger.sendMessage(TextFormat.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		int largeSphereVolume = (int) MathHelper.sphereVolume(brushSize + 2);
		Map<VectorVS, Block> largeSphere = new HashMap<>(largeSphereVolume);
		Block targetBlock = getTargetBlock();
		Painters.sphere()
			.center(targetBlock)
			.radius(brushSize + 2)
			.blockSetter(position -> {
				Block block = getBlock(position);
				largeSphere.put(position, block);
			})
			.paint();
		int smallSphereVolume = (int) MathHelper.sphereVolume(brushSize);
		Map<VectorVS, Block> smallSphere = new HashMap<>(smallSphereVolume);
		Map<VectorVS, Material> smallSphereMaterials = new HashMap<>(smallSphereVolume);
		Painters.sphere()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Block block = largeSphere.get(position);
				smallSphere.put(position, block);
				smallSphereMaterials.put(position, block.getType());
			})
			.paint();
		for (Block smallSphereBlock : smallSphere.values()) {
			VectorVS blockPosition = Vectors.of(smallSphereBlock);
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.cube()
				.center(smallSphereBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(blockPosition)) {
						return;
					}
					Block block = largeSphere.get(position);
					Material material = block.getType();
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallSphereMaterials.put(blockPosition, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallSphereMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
