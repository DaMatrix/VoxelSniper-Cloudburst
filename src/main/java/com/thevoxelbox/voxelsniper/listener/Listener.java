package com.thevoxelbox.voxelsniper.listener;

import org.cloudburstmc.server.event.Event;

public interface Listener<T extends Event> extends org.cloudburstmc.server.event.Listener {

	void listen(T event);
}
