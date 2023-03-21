package legacy.observers.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;

import net.minecraft.client.resource.pack.BuiltInResourcePack;
import net.minecraft.resource.Identifier;

public class ModResourcePack extends BuiltInResourcePack {

	public ModResourcePack() {
		super(Collections.emptyMap());
	}

	@Override
	public InputStream getResource(Identifier location) {
		InputStream resource = openResource(location);

		if (resource == null) {
			throw new IllegalStateException(new FileNotFoundException(location.getPath()));
		} else {
			return resource;
		}
	}

	private InputStream openResource(Identifier location) {
		return ModResourcePack.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath());
	}

	@Override
	public boolean hasResource(Identifier location) {
		return openResource(location) != null;
	}

	@Override
	public String getName() {
		return "Legacy Observers";
	}
}
