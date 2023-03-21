package legacy.observers.mixin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import legacy.observers.resource.ModTexturePack;

import net.minecraft.locale.LanguageManager;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {

	private static final String EN_US_KEY = "en_US";
	private static final String EN_US_LANG_FILE = "assets/minecraft/lang/" + EN_US_KEY + ".lang";

	@Inject(
		method = "m_2304208",
		at = @At(
			value = "TAIL"
		)
	)
	private void loadTranslations(Properties translations, String language) {
		if (!language.equals(EN_US_KEY)) {
			return;
		}

		InputStream resource = ModTexturePack.class.getResourceAsStream(EN_US_LANG_FILE);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(resource))) {
			String line = null;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (!line.startsWith("#")) {
					String[] args = line.split("[=]");

					if (args.length == 2) {
						translations.setProperty(args[0], args[1]);
					}
				}
			}
		} catch (IOException e) {
		}
	}
}
