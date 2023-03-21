package legacy.observers.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.resource.ModResourcePack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.pack.ResourcePack;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow @Final private List<ResourcePack> defaultResourcePacks;

	private ResourcePack modResourcePack = new ModResourcePack();

	@Inject(
		method = "addDefaultResourcePacks",
		at = @At(
			value = "HEAD"
		)
	)
	private void addDefaultResourcePacks(CallbackInfo ci) {
		defaultResourcePacks.add(modResourcePack);
	}
}
