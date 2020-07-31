package com.thevoxelbox.voxelsniper.util.painter;

import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;

public interface Painter {

	void paint();

	VectorVS getCenter();

	BlockSetter getBlockSetter();
}
