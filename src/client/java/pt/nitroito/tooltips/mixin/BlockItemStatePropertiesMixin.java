package pt.nitroito.tooltips.mixin;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.BeehiveBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.TooltipsConfig;

import java.util.function.Consumer;


@Mixin(BlockItemStateProperties.class)
public abstract class BlockItemStatePropertiesMixin {
    @Unique private final BlockItemStateProperties self = (BlockItemStateProperties)(Object)this;

    @Inject(method = "addToTooltip",at = @At("HEAD"), cancellable = true)
    public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components, CallbackInfo ci) {
        if (TooltipsConfig.beeInformationStyle==TooltipsConfig.BeeInformationStyle.VANILLA) return;
        if (components.get(DataComponents.BEES)!=null || self.get(BeehiveBlock.HONEY_LEVEL)!=null)
            ci.cancel();
    }
}
