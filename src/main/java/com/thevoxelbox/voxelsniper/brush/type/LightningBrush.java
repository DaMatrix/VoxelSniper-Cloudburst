package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
public class LightningBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		world.strikeLightning(targetBlock.getLocation());
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		world.strikeLightning(targetBlock.getLocation());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Lightning Brush! Please use in moderation.");
	}
}
