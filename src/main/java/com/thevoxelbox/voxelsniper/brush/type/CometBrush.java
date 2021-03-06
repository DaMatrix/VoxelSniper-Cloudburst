package com.thevoxelbox.voxelsniper.brush.type;

import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.TextFormat;
public class CometBrush extends AbstractBrush {

	private boolean useBigBalls;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 0; index < parameters.length; ++index) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage("Parameters:");
				messenger.sendMessage("balls [big|small]  -- Sets your ball size.");
			}
			if (parameter.equalsIgnoreCase("balls")) {
				if (index + 1 >= parameters.length) {
					messenger.sendMessage("The balls parameter expects a ball size after it.");
				}
				String newBallSize = parameters[++index];
				if (newBallSize.equalsIgnoreCase("big")) {
					this.useBigBalls = true;
					messenger.sendMessage("Your balls are " + TextFormat.DARK_RED + ("BIG"));
				} else if (newBallSize.equalsIgnoreCase("small")) {
					this.useBigBalls = false;
					messenger.sendMessage("Your balls are " + TextFormat.DARK_RED + ("small"));
				} else {
					messenger.sendMessage("Unknown ball size.");
				}
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		doFireball(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		doFireball(snipe);
	}

	private void doFireball(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		int x = targetBlock.getX();
		int y = targetBlock.getY();
		int z = targetBlock.getZ();
		Vector3f targetCoordinates = Vector3f.from(x + 0.5 * x / Math.abs(x), y + 0.5, z + 0.5 * z / Math.abs(z));
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Vector3f slope = targetCoordinates.sub(player.getPosition().add(0f, player.getEyeHeight(), 0f));
		Vector3f normalizedSlope = slope.normalize();
		//TODO: fireballs
		/*if (this.useBigBalls) {
			LargeFireball fireball = player.launchProjectile(LargeFireball.class);
			fireball.setVelocity(normalizedSlope);
		} else {
			SmallFireball fireball = player.launchProjectile(SmallFireball.class);
			fireball.setVelocity(normalizedSlope);
		}*/
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBlockTypeMessage();
		messenger.sendMessage("Your balls are " + TextFormat.DARK_RED + (this.useBigBalls ? "BIG" : "small"));
	}
}
