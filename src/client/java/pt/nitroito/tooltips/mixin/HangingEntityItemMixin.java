package pt.nitroito.tooltips.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.TooltipsConfig;

import java.util.function.Consumer;


@Mixin(HangingEntityItem.class)
public class HangingEntityItemMixin {
    @Unique private final HangingEntityItem self =(HangingEntityItem)(Object)this;

    @Inject(method = "appendHoverText",at = @At("HEAD"), cancellable = true)
	public void appendHoverText(final ItemStack itemStack, final Item.TooltipContext context, final TooltipDisplay display, final Consumer<Component> builder, final TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (self.type==EntityType.PAINTING && TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.VANILLA) return;
        if (self.type!=EntityType.PAINTING) return;
        ci.cancel();
    }
}
