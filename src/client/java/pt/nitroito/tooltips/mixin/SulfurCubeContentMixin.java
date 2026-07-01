package pt.nitroito.tooltips.mixin;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.SulfurCubeContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;


@Mixin(SulfurCubeContent.class)
public class SulfurCubeContentMixin {
    @Inject(method = "addToTooltip",at = @At("HEAD"), cancellable = true)
	public void addToTooltip(final TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components, CallbackInfo ci) {
        ci.cancel();
    }
}
