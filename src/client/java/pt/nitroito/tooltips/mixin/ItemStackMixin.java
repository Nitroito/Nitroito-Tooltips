package pt.nitroito.tooltips.mixin;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.TooltipsGlobals;
import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Unique private final ItemStack self =(ItemStack)(Object)this;

    @Inject(method = "addDetailsToTooltip",at = @At("HEAD"))
	public void addDetailsToTooltip(final TooltipContext context, final TooltipDisplay display, @Nullable final Player player, final TooltipFlag tooltipFlag, final Consumer<Component> builder, CallbackInfo ci){
        TooltipsGlobals.setStack(self);
        TooltipsGlobals.setTooltipFlag(tooltipFlag);
        TooltipsGlobals.setTooltipContext(context);
    }

    @Inject(method = "addToTooltip",at = @At("HEAD"))
    public <T extends TooltipProvider> void addToTooltip(final DataComponentType<T> type, final TooltipContext context, final TooltipDisplay display, final Consumer<Component> consumer, final TooltipFlag flag, CallbackInfo ci){
        TooltipsGlobals.setDataComponentType(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type));
    }
}
