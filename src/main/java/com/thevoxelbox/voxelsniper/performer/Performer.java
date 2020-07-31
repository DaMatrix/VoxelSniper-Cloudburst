package com.thevoxelbox.voxelsniper.performer;

import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
public interface Performer {

	void initialize(PerformerSnipe snipe);

	void perform(Block block);

	void sendInfo(PerformerSnipe snipe);

	void initializeUndo();

	Undo getUndo();
}
