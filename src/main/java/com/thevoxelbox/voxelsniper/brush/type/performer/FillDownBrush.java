package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.behavior.BlockBehaviorLiquid;
import org.cloudburstmc.server.utils.TextFormat;
public class FillDownBrush extends AbstractPerformerBrush {

	private double trueCircle;
	private boolean fillLiquid = true;
	private boolean fromExisting;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				snipe.createMessageSender()
					.message(TextFormat.GOLD + "Fill Down Parameters:")
					.message(TextFormat.AQUA + "/b fd true -- will use a true circle algorithm.")
					.message(TextFormat.AQUA + "/b fd false -- will switch back. (Default)")
					.message(TextFormat.AQUA + "/b fd some -- Fills only into air.")
					.message(TextFormat.AQUA + "/b fd all -- Fills into liquids as well. (Default)")
					.message(TextFormat.AQUA + "/b fd -e -- Fills into only existing blocks. (Toggle)")
					.send();
				return;
			} else if (parameter.equalsIgnoreCase("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode ON.");
			} else if (parameter.equalsIgnoreCase("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode OFF.");
			} else if (parameter.equalsIgnoreCase("all")) {
				this.fillLiquid = true;
				messenger.sendMessage(TextFormat.AQUA + "Now filling liquids as well as air.");
			} else if (parameter.equalsIgnoreCase("some")) {
				this.fillLiquid = false;
				ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
				toolkitProperties.resetReplaceBlockData();
				messenger.sendMessage(TextFormat.AQUA + "Now only filling air.");
			} else if (parameter.equalsIgnoreCase("-e")) {
				this.fromExisting = !this.fromExisting;
				messenger.sendMessage(TextFormat.AQUA + "Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.");
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		fillDown(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		fillDown(snipe);
	}

	private void fillDown(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Block targetBlock = this.getTargetBlock();
		for (int x = -brushSize; x <= brushSize; x++) {
			double currentXSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
					int y = 0;
					if (this.fromExisting) {
						boolean found = false;
						for (y = -toolkitProperties.getVoxelHeight(); y < toolkitProperties.getVoxelHeight(); y++) {
							Block currentBlock = getLevel().getBlock(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
							if (!Materials.isEmpty(currentBlock.getState().getType())) {
								found = true;
								break;
							}
						}
						if (!found) {
							continue;
						}
						y--;
					}
					for (; y >= -targetBlock.getY(); --y) {
						Block currentBlock = getLevel().getBlock(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
						if (Materials.isEmpty(currentBlock.getState().getType()) || (this.fillLiquid && currentBlock.getState().getBehavior() instanceof BlockBehaviorLiquid)) {
							this.performer.perform(currentBlock);
						} else {
							break;
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
