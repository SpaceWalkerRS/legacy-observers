package legacy.observers.mixin.client;

import java.io.InputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import legacy.observers.resource.ModTexturePack;

import net.minecraft.client.resource.pack.BuiltInTexturePack;

@Mixin(BuiltInTexturePack.class)
public class BuiltInTexturePackMixin {

	private final ModTexturePack modTexturePack = new ModTexturePack();

	@Inject(
		method = "hasResource",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void hasResource(String path, CallbackInfoReturnable<Boolean> cir) {
		if (modTexturePack.hasResource(path)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(
		method = "getResource",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void getResource(String path, CallbackInfoReturnable<InputStream> cir) {
		InputStream resource = modTexturePack.getResource(path);

		if (resource != null) {
			cir.setReturnValue(resource);
		}
	}
}
