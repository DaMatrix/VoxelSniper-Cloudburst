package com.thevoxelbox.voxelsniper.listener;

public interface Listener<T extends Event> extends org.bukkit.event.Listener {

	void listen(T event);
}
