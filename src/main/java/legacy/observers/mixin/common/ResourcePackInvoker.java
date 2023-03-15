package legacy.observers.mixin.common;

import java.io.InputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.pack.ResourcePack;

@Mixin(ResourcePack.class)
public interface ResourcePackInvoker {

	@Invoker("getMetadataSection")
	public static ResourceMetadataSection invokeGetMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, InputStream file, String name) {
		throw new AssertionError();
	}
}
