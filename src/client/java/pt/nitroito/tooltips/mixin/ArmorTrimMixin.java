package pt.nitroito.tooltips.mixin;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.TooltipsConfig;

import java.util.function.Consumer;


@Mixin(ArmorTrim.class)
public class ArmorTrimMixin {
    @Inject(method = "addToTooltip",at = @At("HEAD"), cancellable = true)
	public void addToTooltip(final TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components, CallbackInfo ci) {
        if (TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.VANILLA) return;
        ci.cancel();
    }
}
