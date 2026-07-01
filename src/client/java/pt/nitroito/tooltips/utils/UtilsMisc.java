package pt.nitroito.tooltips.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.helper.ClientTextTooltipVisitor;


public class UtilsMisc {

    public static Component getDisplayName(ItemStack itemStack){
        return itemStack.getHoverName().plainCopy().setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
    }

    public static Component getDisplayName(Rarity rarity){
        return Component.literal(UtilsString.titleCase(rarity.name())).setStyle(getFormatingStyle(rarity));
    }

    public static Style getFormatingStyle(Rarity rarity){
        return switch(rarity){
            case Rarity.UNCOMMON -> Style.EMPTY.withColor(ChatFormatting.YELLOW);
            case Rarity.RARE -> Style.EMPTY.withColor(ChatFormatting.GOLD);
            case Rarity.EPIC -> Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE);
            default -> Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
        };
    }

    public static Style getFormatingStyle(int value, int maxValue) {
        if (value == 0) return Style.EMPTY.withColor(ChatFormatting.RED);
        if (value == maxValue) return Style.EMPTY.withColor(ChatFormatting.GOLD);
        return Style.EMPTY.withColor(ChatFormatting.WHITE);
    }

    public static Style getFormatingStyle(int value, int maxValue, double minThreshold) {
        if (value<minThreshold) return Style.EMPTY.withColor(ChatFormatting.RED);
        if (value<maxValue) return Style.EMPTY.withColor(ChatFormatting.GREEN);
        return Style.EMPTY.withColor(UtilsGraphics.DARK_GREEN);
    }

    public static String getModDisplayName(Identifier identifier){
        return TooltipsGlobals.FABRIC_MODS.get(identifier.getNamespace());
    }

    public static boolean isCreativeGroup(ClientTextTooltip component){
        return TooltipsGlobals.CREATIVE_GROUPS.contains(ClientTextTooltipVisitor.getComponent(component.text).getString());
    }

    public static boolean isFooterComponent(ClientTextTooltip component, ItemStack stack){
        String text = ClientTextTooltipVisitor.getComponent(component.text).getString();
        return
            text.contains(Component.translatable("item.durability", stack.getMaxDamage()-stack.getDamageValue(), stack.getMaxDamage()).getString()) ||
            text.contains(Component.translatable("item.components", stack.components.size()).getString()) ||
            text.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()) ||
            text.contains(UtilsMisc.getModDisplayName(BuiltInRegistries.ITEM.getKey(stack.getItem()))) ||
            UtilsMisc.getModDisplayName(BuiltInRegistries.ITEM.getKey(stack.getItem())).contains(text);
    }

    public static boolean isEmptyLine(ClientTextTooltip component) {
        return ClientTextTooltipVisitor.getComponent(component.text).getString().isEmpty();
    }
}
