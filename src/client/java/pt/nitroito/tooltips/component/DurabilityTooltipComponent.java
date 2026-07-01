package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.utils.UtilsGraphics;
import pt.nitroito.tooltips.utils.UtilsMisc;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;


public class DurabilityTooltipComponent implements CustomTooltipComponent {
    private final int BAR_WIDTH = 70;
    private final DecimalFormat PERCENTAGE = new DecimalFormat("0.#%", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private final int damageLeft;
    private final int maxDamage;

    public DurabilityTooltipComponent(ItemStack stack) {
        this.maxDamage = stack.getMaxDamage();
        this.damageLeft = maxDamage - stack.getDamageValue();
    }

    @Override
    public int getHeight(@NotNull Font font) {
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.HIDDEN) return 0;
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.VANILLA) return 0;
        return font.lineHeight+1;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.HIDDEN) return 0;
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.VANILLA) return 0;

        int prefixWidth = font.width(Component.translatable(Tooltips.MOD_ID+".tooltip.durability").append(":"));

        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.BAR)
            return prefixWidth + BAR_WIDTH + 2;
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.PERCENTAGE)
            return prefixWidth + font.width(" "+PERCENTAGE.format((float)damageLeft/maxDamage));

        return prefixWidth + font.width(String.format(" %d / %d", damageLeft, maxDamage));
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.HIDDEN) return;
        if (TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.VANILLA) return;

        MutableComponent durabilityComponent = Component.translatable(Tooltips.MOD_ID+".tooltip.durability").append(":").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        switch(TooltipsConfig.itemDurabilityStyle){
            case VALUES -> {
                durabilityComponent.append(Component.literal(" "+String.format("%d / %d", damageLeft, maxDamage)).withStyle(ChatFormatting.GRAY));
                graphics.drawString(font, durabilityComponent, x, y, -1, true);
            }
            case FORMATED_VALUES -> {
                Style durabilityStyle = UtilsMisc.getFormatingStyle(damageLeft, maxDamage, Math.max(10,maxDamage*0.10));
                Component damageLeftComponent = Component.literal(" "+damageLeft).setStyle(durabilityStyle);
                Component maxDamageComponent = Component.literal(String.valueOf(maxDamage)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                durabilityComponent.append(damageLeftComponent).append(" §7/ ").append(maxDamageComponent);
                graphics.drawString(font, durabilityComponent, x, y, -1, true);
            }
            case PERCENTAGE -> {
                Style durabilityStyle = UtilsMisc.getFormatingStyle(damageLeft, maxDamage, Math.max(10,maxDamage*0.10));
                durabilityComponent.append(Component.literal(" "+PERCENTAGE.format((float)damageLeft/maxDamage)).setStyle(durabilityStyle));
                graphics.drawString(font, durabilityComponent, x, y, -1, true);
            }
            case BAR -> {
                graphics.drawString(font, durabilityComponent, x, y, -1, true);
                int barHeight = font.lineHeight-1;
                int x1 = x + font.width(durabilityComponent)+2;
                int x2 = x1 + BAR_WIDTH;
                int y2 = y+barHeight;
                int offset = Math.round(((float)damageLeft/maxDamage)*BAR_WIDTH);
                UtilsGraphics.horizontalGradient(graphics, x1, y, BAR_WIDTH, barHeight, UtilsGraphics.RED, UtilsGraphics.ORANGE, UtilsGraphics.GREEN);
                graphics.fill(x1+offset, y, x2, y2, 0xFF111111);
                graphics.renderOutline(x1, y, BAR_WIDTH, barHeight, DyeColor.LIGHT_GRAY.getTextColor());
            }
        }
    }

    public static boolean includeDurabilityTooltipComponent(ItemStack itemStack){
        boolean isHidden = TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.HIDDEN;
        boolean isVanilla = TooltipsConfig.itemDurabilityStyle == TooltipsConfig.ItemDurabilityStyle.VANILLA;
        return itemStack.isDamageableItem() && !isHidden && !isVanilla;
    }
}
