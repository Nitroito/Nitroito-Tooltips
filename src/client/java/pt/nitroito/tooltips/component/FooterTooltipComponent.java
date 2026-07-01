package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.utils.UtilsMisc;


public class FooterTooltipComponent implements CustomTooltipComponent {
    private final boolean USE_LINE_SPACER = false;
    private final ItemStack stack;
    private final MutableComponent itemDurability;
    private final MutableComponent itemIdentifier;
    private final MutableComponent itemComponents;
    private final MutableComponent modDisplayName;

    public FooterTooltipComponent(ItemStack stack) {
        this.stack = stack;
        this.itemDurability = Component.translatable("item.durability", stack.getMaxDamage()-stack.getDamageValue(), stack.getMaxDamage());
        this.itemIdentifier = Component.literal(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        this.itemComponents = Component.translatable("item.components", stack.components.size());
        this.modDisplayName = Component.literal(UtilsMisc.getModDisplayName(BuiltInRegistries.ITEM.getKey(stack.getItem())));
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int result = 0;
        if (includeItemDurability()) result += font.lineHeight+1;
        if (includeItemIdentifier()) result += font.lineHeight+1;
        if (includeComponentCount()) result += font.lineHeight+1;
        if (includeModDisplayName()) result += font.lineHeight+1;
        return result + (USE_LINE_SPACER ? font.lineHeight : 0);
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        if (includeItemDurability()) result = Math.max(result, font.width(this.itemDurability));
        if (includeItemIdentifier()) result = Math.max(result, font.width(this.itemIdentifier));
        if (includeComponentCount()) result = Math.max(result, font.width(this.itemComponents));
        if (includeModDisplayName()) result = Math.max(result, font.width(this.modDisplayName));
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Font font, int x, int y, int w, int h) {
        int posY = y + (USE_LINE_SPACER ? font.lineHeight : 0);

        if (includeItemDurability()) {
            graphics.text(font, itemDurability, x, posY, -1, true);
            posY += font.lineHeight + 1;
        }
        if (includeItemIdentifier()) {
            graphics.text(font, itemIdentifier.withStyle(ChatFormatting.DARK_GRAY), x, posY, -1, true);
            posY += font.lineHeight + 1;
        }
        if (includeComponentCount()) {
            graphics.text(font, itemComponents.withStyle(ChatFormatting.DARK_GRAY), x, posY, -1, true);
            posY += font.lineHeight + 1;
        }
        if (includeModDisplayName()) {
            graphics.text(font, modDisplayName.withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)), x, posY, -1, true);
        }
    }

    private boolean includeItemDurability(){
        boolean vanillaDurability = TooltipsConfig.itemDurabilityStyle==TooltipsConfig.ItemDurabilityStyle.VANILLA;
        return stack.isDamaged() && TooltipsGlobals.isAdvancedTooltip() && vanillaDurability;
    }

    private boolean includeItemIdentifier(){
        boolean isHidden = TooltipsConfig.itemIdentifierStyle==TooltipsConfig.ItemIdentifierStyle.HIDDEN;
        boolean isAlwaysVisible = TooltipsConfig.itemIdentifierStyle==TooltipsConfig.ItemIdentifierStyle.ALWAYS_VISIBLE;
        return (TooltipsGlobals.isAdvancedTooltip() && !isHidden) || (!TooltipsGlobals.isAdvancedTooltip() && isAlwaysVisible);
    }

    private boolean includeComponentCount(){
        return TooltipsGlobals.isAdvancedTooltip();
    }

    private boolean includeModDisplayName(){
        return TooltipsConfig.showModName;
    }
}
