package xyz.ludwicz.ludwigmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Nested Class `NotificationTask` doesn't exposed in 1.18.2 mojmap.
@Mixin(targets = "net.minecraft.client.resource.PeriodicNotificationManager$NotifyTask")
public class MixinNotifyTask {
	// TODO: Make sure only remove Compliance Notification for future.
	// Note: PeriodicNotificationManager only used for regional compliance notification as of 1.18.2.
	@Inject(at = @At("HEAD"), cancellable = true, method = "Lnet/minecraft/client/resource/PeriodicNotificationManager$NotifyTask;run()V")
	private void run(CallbackInfo ci) {
		ci.cancel();
	}
}