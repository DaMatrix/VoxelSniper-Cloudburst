package com.thevoxelbox.voxelsniper.brush.type.stamp;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.utils.TextFormat;
/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set.
 */
public class CloneStampBrush extends AbstractStampBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		String parameter = parameters[0];
		if (parameter.equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.GOLD + "Clone / Stamp Cylinder brush parameters");
			messenger.sendMessage(TextFormat.GREEN + "cs f -- Activates Fill mode");
			messenger.sendMessage(TextFormat.GREEN + "cs a -- Activates No-Air mode");
			messenger.sendMessage(TextFormat.GREEN + "cs d -- Activates Default mode");
		}
		if (parameter.equalsIgnoreCase("a")) {
			setStamp(StampType.NO_AIR);
			reSort();
			messenger.sendMessage(TextFormat.AQUA + "No-Air stamp brush");
		} else if (parameter.equalsIgnoreCase("f")) {
			setStamp(StampType.FILL);
			reSort();
			messenger.sendMessage(TextFormat.AQUA + "Fill stamp brush");
		} else if (parameter.equalsIgnoreCase("d")) {
			setStamp(StampType.DEFAULT);
			reSort();
			messenger.sendMessage(TextFormat.AQUA + "Default stamp brush");
		} else if (!parameter.isEmpty() && parameter.charAt(0) == 'c') {
			Integer cylinderCenter = NumericParser.parseInteger(parameter.replace("c", ""));
			if (cylinderCenter == null) {
				return;
			}
			toolkitProperties.setCylinderCenter(cylinderCenter);
			messenger.sendMessage(TextFormat.BLUE + "Center set to " + toolkitProperties.getCylinderCenter());
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		clone(snipe);
	}

	/**
	 * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen.
	 * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the height of the cylinder c.cCen -- the offset on
	 * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
	 */
	private void clone(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		this.clone.clear();
		this.fall.clear();
		this.drop.clear();
		this.solid.clear();
		this.sorted = false;
		Block targetBlock = getTargetBlock();
		int targetBlockY = targetBlock.getY();
		int yStartingPoint = targetBlockY + toolkitProperties.getCylinderCenter();
		int yEndPoint = targetBlockY + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
		World world = getWorld();
		if (yStartingPoint < 0) {
			yStartingPoint = 0;
			messenger.sendMessage(TextFormat.DARK_PURPLE + "Warning: off-world start position.");
		} else if (yStartingPoint > world.getMaxHeight() - 1) {
			yStartingPoint = world.getMaxHeight() - 1;
			messenger.sendMessage(TextFormat.DARK_PURPLE + "Warning: off-world start position.");
		}
		if (yEndPoint < 0) {
			yEndPoint = 0;
			messenger.sendMessage(TextFormat.DARK_PURPLE + "Warning: off-world end position.");
		} else if (yEndPoint > world.getMaxHeight() - 1) {
			yEndPoint = world.getMaxHeight() - 1;
			messenger.sendMessage(TextFormat.DARK_PURPLE + "Warning: off-world end position.");
		}
		double bSquared = Math.pow(brushSize, 2);
		int targetBlockX = targetBlock.getX();
		int targetBlockZ = targetBlock.getZ();
		for (int z = yStartingPoint; z < yEndPoint; z++) {
			this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ), 0, z - yStartingPoint, 0));
			for (int y = 1; y <= brushSize; y++) {
				this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ + y), 0, z - yStartingPoint, y));
				this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ - y), 0, z - yStartingPoint, -y));
				this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX + y, z, targetBlockZ), y, z - yStartingPoint, 0));
				this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX - y, z, targetBlockZ), -y, z - yStartingPoint, 0));
			}
			for (int x = 1; x <= brushSize; x++) {
				double xSquared = Math.pow(x, 2);
				for (int y = 1; y <= brushSize; y++) {
					if ((xSquared + Math.pow(y, 2)) <= bSquared) {
						this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX + x, z, targetBlockZ + y), x, z - yStartingPoint, y));
						this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX + x, z, targetBlockZ - y), x, z - yStartingPoint, -y));
						this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX - x, z, targetBlockZ + y), -x, z - yStartingPoint, y));
						this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX - x, z, targetBlockZ - y), -x, z - yStartingPoint, -y));
					}
				}
			}
		}
		messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.clone.size()) + TextFormat.AQUA + " blocks copied successfully.");
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendVoxelHeightMessage();
		messenger.sendCylinderCenterMessage();
		switch (this.stamp) {
			case DEFAULT:
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Default Stamp");
				break;
			case NO_AIR:
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "No-Air Stamp");
				break;
			case FILL:
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Fill Stamp");
				break;
			default:
				messenger.sendMessage(TextFormat.DARK_RED + "Error while stamping! Report");
				break;
		}
	}
}
