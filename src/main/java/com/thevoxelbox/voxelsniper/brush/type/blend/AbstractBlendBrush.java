package com.thevoxelbox.voxelsniper.brush.type.blend;

import java.util.Map;
import java.util.Map.Entry;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public abstract class AbstractBlendBrush extends AbstractBrush {

	private boolean airExcluded = true;
	private boolean waterExcluded = true;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("water")) {
				this.waterExcluded = !this.waterExcluded;
				messenger.sendMessage(TextFormat.AQUA + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"));
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		this.airExcluded = false;
		blend(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		this.airExcluded = true;
		blend(snipe);
	}

	public abstract void blend(Snipe snipe);

	protected void setBlocks(Map<VectorVS, Identifier> materials, Undo undo) {
		for (Entry<VectorVS, Identifier> entry : materials.entrySet()) {
			VectorVS position = entry.getKey();
			Identifier material = entry.getValue();
			if (checkExclusions(material)) {
				Identifier currentBlockType = getBlockType(position);
				if (currentBlockType != material) {
					Block clamped = clampY(position);
					undo.put(clamped);
				}
				setBlockType(position, material);
			}
		}
	}

	protected CommonMaterial findCommonMaterial(Map<Identifier, Integer> materialsFrequencies) {
		CommonMaterial commonMaterial = new CommonMaterial();
		for (Entry<Identifier, Integer> entry : materialsFrequencies.entrySet()) {
			Identifier material = entry.getKey();
			int frequency = entry.getValue();
			if (frequency > commonMaterial.getFrequency() && checkExclusions(material)) {
				commonMaterial.setMaterial(material);
				commonMaterial.setFrequency(frequency);
			}
		}
		return commonMaterial;
	}

	private boolean checkExclusions(Identifier material) {
		return (!this.airExcluded || !Materials.isEmpty(material)) && (!this.waterExcluded || material != BlockTypes.WATER);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.message(TextFormat.BLUE + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"))
			.send();
	}
}
