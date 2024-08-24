package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.world.ModGamerules;

import net.minecraft.world.Gamerules;

@Mixin(Gamerules.class)
public class GamerulesMixin {

	@Shadow private void add(String name, String defaultValue) { }

	@Inject(
		method = "<init>",
		at = @At(
			value = "TAIL"
		)
	)
	private void init(CallbackInfo ci) {
		add(ModGamerules.NOTE_BLOCKS_TRIGGER_OBSERVERS, "false");
	}

	@Inject(
		method = "set",
		at = @At(
			value = "TAIL"
		)
	)
	private void update(String name, String value, CallbackInfo ci) {
		if (ModGamerules.NOTE_BLOCKS_TRIGGER_OBSERVERS.equals(name)) {
			ModGamerules.noteBlocksTriggerObservers = Boolean.parseBoolean(value);
		}
	}
}
