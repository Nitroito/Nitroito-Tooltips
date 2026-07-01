package pt.nitroito.tooltips.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.utils.UtilsGraphics;
import pt.nitroito.tooltips.utils.UtilsString;
import java.util.ArrayList;
import java.util.function.Consumer;


@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin {
    @Unique private final Identifier ENCHANTMENTS_KEY = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.ENCHANTMENTS);
    @Unique private final Identifier STORED_ENCHANTMENTS_KEY = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.STORED_ENCHANTMENTS);
    @Unique private final ItemEnchantments self = (ItemEnchantments)(Object)this;

    @Inject(method = "addToTooltip",at = @At("HEAD"), cancellable = true)
	public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components, CallbackInfo ci) {
        if (self.isEmpty() || TooltipsConfig.enchantmentsStyle==TooltipsConfig.EnchantmentsStyle.VANILLA) return;

        Identifier currentDataComponentType = TooltipsGlobals.getDataComponentType();
        if (BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(currentDataComponentType)) {
            if(currentDataComponentType.equals(this.STORED_ENCHANTMENTS_KEY)){
                consumer.accept(Component.translatable(Tooltips.MOD_ID + ".tooltip.stored_enchantments").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                addEnchantmentsToTooltip(self, consumer);
            }
            if(currentDataComponentType.equals(this.ENCHANTMENTS_KEY)){
                consumer.accept(Component.translatable(Tooltips.MOD_ID + ".tooltip.enchantments").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                addEnchantmentsToTooltip(self, consumer);
            }
            ci.cancel();
        }
    }

    @Unique private void addEnchantmentsToTooltip(ItemEnchantments enchantments, Consumer<Component> consumer) {
        ArrayList<Holder<Enchantment>> list = new ArrayList<>(enchantments.keySet().stream().toList());
        if (TooltipsConfig.enchantmentsSorted){
            list.sort((a,b) -> getSortString(a).compareToIgnoreCase(getSortString(b)));
        }
        list.forEach(holder -> consumer.accept(getEnchantmentComponent(holder, enchantments.getLevel(holder))));
    }

    @Unique private static Component getEnchantmentComponent(Holder<Enchantment> enchantment, int enchantmentLevel) {
        int enchantmentMaxLevel = enchantment.value().getMaxLevel();
        int enchantmentMinLevel = enchantment.value().getMinLevel();
        MutableComponent result = Component.literal(" ").append(enchantment.value().description());
        if (enchantmentMinLevel!=enchantmentMaxLevel && enchantmentLevel>=enchantmentMinLevel && enchantmentLevel!=0) {
            result.append(" "+(TooltipsConfig.enchantmentsRomanNumerals ? UtilsString.integerToRoman(enchantmentLevel) : enchantmentLevel));
        }
        if (TooltipsConfig.enchantmentsStyle==TooltipsConfig.EnchantmentsStyle.PLAIN_TEXT)
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        if (enchantmentLevel<0)
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withStrikethrough(true));
        if (enchantmentLevel==0)
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withStrikethrough(true));
        if (enchantment.is(EnchantmentTags.CURSE))
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED));
        if (enchantmentMinLevel==enchantmentMaxLevel)
            return result.setStyle(Style.EMPTY.withColor(UtilsGraphics.DARK_GREEN));
        if (enchantmentLevel==enchantmentMaxLevel-1)
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
        if (enchantmentLevel==enchantmentMaxLevel)
            return result.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        if (enchantmentLevel>enchantmentMaxLevel)
            return result.setStyle(Style.EMPTY.withColor(DyeColor.ORANGE.getTextColor()));
        return result.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
    }

    @Unique private String getSortString(Holder<Enchantment> enchantment){
        String description = enchantment.value().description().getString();
        if (!enchantment.is(EnchantmentTags.CURSE)){
            if (enchantment.value().getMinLevel()==enchantment.value().getMaxLevel()) return "#1)"+description;
            return "#2)"+description;
        }
        return "#9)"+description;
    }
}
