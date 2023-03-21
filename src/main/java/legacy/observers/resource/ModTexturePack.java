package legacy.observers.resource;

import java.io.InputStream;

import net.minecraft.client.resource.pack.AbstractTexturePack;

public class ModTexturePack extends AbstractTexturePack {

	public ModTexturePack() {
		super("legacy-observers", null, "Legacy Observers", null);
	}

	@Override
	public InputStream getResource(String path) {
		return ModTexturePack.class.getResourceAsStream(path);
	}

	@Override
	public boolean hasResource(String path) {
		return getResource(path) != null;
	}

	@Override
	public boolean isCompatible() {
		return true;
	}
}
