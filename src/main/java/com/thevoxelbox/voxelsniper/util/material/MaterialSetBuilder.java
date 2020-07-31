package com.thevoxelbox.voxelsniper.util.material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.cloudburstmc.server.utils.Identifier;

public class MaterialSetBuilder {

	private List<Identifier> materials = new ArrayList<>(1);

	public MaterialSetBuilder add(Identifier material) {
		this.materials.add(material);
		return this;
	}

	public MaterialSetBuilder with(Identifier... materials) {
		List<Identifier> list = Arrays.asList(materials);
		this.materials.addAll(list);
		return this;
	}

	public MaterialSetBuilder with(Collection<Identifier> materials) {
		this.materials.addAll(materials);
		return this;
	}

	public MaterialSetBuilder with(MaterialSet materialSet) {
		Set<Identifier> materials = materialSet.getMaterials();
		this.materials.addAll(materials);
		return this;
	}

	public MaterialSet build() {
		return new MaterialSet(this.materials);
	}
}
