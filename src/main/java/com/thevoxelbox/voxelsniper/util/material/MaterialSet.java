package com.thevoxelbox.voxelsniper.util.material;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.utils.Identifier;

public class MaterialSet implements Iterable<Identifier> {

	private Set<Identifier> materials;

	public static MaterialSetBuilder builder() {
		return new MaterialSetBuilder();
	}

	public MaterialSet(Collection<Identifier> materials) {
		this.materials = new HashSet<>(materials);
	}

	public boolean contains(BlockState blockState) {
		Identifier type = blockState.getType();
		return contains(type);
	}

	public boolean contains(Identifier material) {
		return this.materials.contains(material);
	}

	@Override
	public Iterator<Identifier> iterator() {
		return this.materials.iterator();
	}

	public Set<Identifier> getMaterials() {
		return Collections.unmodifiableSet(this.materials);
	}
}
