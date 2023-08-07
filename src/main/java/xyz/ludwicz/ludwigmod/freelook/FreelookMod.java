package xyz.ludwicz.ludwigmod.freelook;

import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;

public class FreelookMod {

    @Getter
    private static KeyBinding freelookKey;

    @Getter
    private static boolean freelookEnabled;

    private static Perspective previousPerspective;

    @Getter
    @Setter
    private static float yaw;
    @Getter
    @Setter
    private static float pitch;

    public static void init() {
        freelookKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.ludwigmod.freelook",
                        InputUtil.Type.KEYSYM,
                        InputUtil.GLFW_KEY_LEFT_ALT,
                        KeyBinding.GAMEPLAY_CATEGORY
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(freelookKey.isPressed()) {
                if(!freelookEnabled)
                    previousPerspective = MinecraftClient.getInstance().options.getPerspective();

                freelookEnabled = true;
                MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
            } else if(freelookEnabled) {
                freelookEnabled = false;
                MinecraftClient.getInstance().options.setPerspective(previousPerspective);
            }
        });
    }

    private FreelookMod() {

    }
}
