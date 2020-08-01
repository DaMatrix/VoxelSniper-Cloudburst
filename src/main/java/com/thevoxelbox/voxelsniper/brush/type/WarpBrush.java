package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.player.Player;

public class WarpBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location playerLocation = player.getLocation();
		Location location = Location.from(lastBlock.getPosition().toFloat(), playerLocation.getYaw(), playerLocation.getPitch(), playerLocation.getLevel());
		player.teleport(location);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location playerLocation = player.getLocation();
		Location location = Location.from(lastBlock.getPosition().toFloat(), playerLocation.getYaw(), playerLocation.getPitch(), playerLocation.getLevel());
		//TODO: lightning
		// getLevel().strikeLightning(location);
		player.teleport(location);
		//getLevel().strikeLightning(location);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
