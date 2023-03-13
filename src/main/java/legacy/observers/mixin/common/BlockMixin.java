package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ObserverBlock;

import net.minecraft.block.Block;

@Mixin(Block.class)
public class BlockMixin {

	@Shadow private static void register(int rawId, String name, Block block) { }

	@Inject(
		method = "init",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/registry/DefaultedIdRegistry;validate()V"
		)
	)
	private static void init(CallbackInfo ci) {
		register(218, "observer", new ObserverBlock().setId("observer"));
	}
}
