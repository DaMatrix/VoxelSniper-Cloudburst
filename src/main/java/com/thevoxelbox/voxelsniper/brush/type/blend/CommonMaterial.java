package com.thevoxelbox.voxelsniper.brush.type.blend;

import org.cloudburstmc.server.utils.Identifier;
import org.jetbrains.annotations.Nullable;

class CommonMaterial {

	@Nullable
	private Identifier material;
	private int frequency;

	@Nullable
	public Identifier getMaterial() {
		return this.material;
	}

	public void setMaterial(@Nullable Identifier material) {
		this.material = material;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
