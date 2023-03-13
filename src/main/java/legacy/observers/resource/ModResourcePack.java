package legacy.observers.resource;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.mojang.blaze3d.platform.TextureUtil;

import legacy.observers.mixin.common.ResourcePackInvoker;

import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.pack.DefaultResourcePack;
import net.minecraft.client.resource.pack.IResourcePack;
import net.minecraft.resource.Identifier;

public class ModResourcePack implements IResourcePack {

	public static final Set<String> NAMESPACES = ImmutableSet.of("minecraft");

	public ModResourcePack() {
	}

	@Override
	public InputStream getResource(Identifier id) throws IOException {
		InputStream resource = openResource(id);

		if (resource == null) {
			throw new FileNotFoundException(id.getPath());
		} else {
			return resource;
		}
	}

	private InputStream openResource(Identifier id) {
		return ModResourcePack.class.getResourceAsStream("/assets/" + id.getNamespace() + "/" + id.getPath());
	}

	@Override
	public boolean hasResource(Identifier id) {
		return openResource(id) != null;
	}

	@Override
	public Set<String> getNamespaces() {
		return NAMESPACES;
	}

	@Override
	public <T extends ResourceMetadataSection> T getMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, String name) throws IOException {
		try {
			InputStream inputStream = DefaultResourcePack.class.getResourceAsStream("pack.mcmeta");
			return ResourcePackInvoker.invokeGetMetadataSection(metadataSerializers, inputStream, name);
		} catch (RuntimeException var4) {
			return null;
		}
	}

	@Override
	public BufferedImage getIcon() throws IOException {
		return TextureUtil.readImage(DefaultResourcePack.class.getResourceAsStream("/" + new Identifier("pack.png").getPath()));
	}

	@Override
	public String getName() {
		return "Legacy Observers";
	}
}
