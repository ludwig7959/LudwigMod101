package xyz.ludwicz.ludwigmod.zoom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomMod {

    private static KeyBinding zoomKey;

    private static int defaultFov = 100;
    private static int targetFov = 30;

    private static boolean flag = false;
    private static boolean flag1 = true;

    public static void init() {
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ludwigmod.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "Zoom"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (zoomKey.isPressed()) {
                if (flag1) {
                    defaultFov = client.options.getFov().getValue();
                    flag1 = false;
                }
                client.options.smoothCameraEnabled = true;
                client.options.getFov().setValue(targetFov);
                flag = true;
            } else if (flag) {
                client.options.smoothCameraEnabled = false;
                MinecraftClient.getInstance().options.getFov().setValue(defaultFov);
                flag = false;
                flag1 = true;
            }
        });
    }

    private ZoomMod() {

    }
}
