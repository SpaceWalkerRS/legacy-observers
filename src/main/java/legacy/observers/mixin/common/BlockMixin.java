package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ObserverBlock;

import net.minecraft.block.Block;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(
		method = "init",
		at = @At(
			value = "HEAD"
		)
	)
	private static void init(CallbackInfo ci) {
		register(218, "observer", new ObserverBlock().spriteId("observer").strength(3.0F).setId("observer"));
	}

	private static void register(int id, String key, Block block) {
		Block.REGISTRY.register(id, key, block);
	}
}
