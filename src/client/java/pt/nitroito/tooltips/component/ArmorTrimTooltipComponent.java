package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.*;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;


public class ArmorTrimTooltipComponent implements CustomTooltipComponent {
    private final int ICON_SIZE = 8;
    private final int ICON_MARGIN = 2;
    private final ArmorTrim armorTrim;

    public ArmorTrimTooltipComponent(ItemStack stack) {
        this.armorTrim = stack.get(DataComponents.TRIM);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return (font.lineHeight+1) * 3;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        int indentOffset = font.width(CommonComponents.space());
        int iconOffset = (TooltipsConfig.armorTrimShowIcons ? ICON_SIZE+ICON_MARGIN : 0);
        result = Math.max(result, font.width(getHeaderComponent()));
        result = Math.max(result, indentOffset+iconOffset+font.width(getPatternComponent()));
        result = Math.max(result, indentOffset+iconOffset+font.width(getMaterialComponent()));
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        int posY = y;
        int posX = x + font.width(CommonComponents.space());
        int iconOffset = (TooltipsConfig.armorTrimShowIcons ? ICON_SIZE+ICON_MARGIN : 0);
        graphics.drawString(font, getHeaderComponent(), x, posY, -1, true);
        posY += font.lineHeight+1;
        if (TooltipsConfig.armorTrimShowIcons){
            Identifier pattern = Identifier.parse(this.armorTrim.pattern().getRegisteredName());
            Identifier patternIcon = Identifier.withDefaultNamespace("item/"+pattern.getPath()+"_armor_trim_smithing_template");
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, patternIcon, posX, posY, ICON_SIZE, ICON_SIZE);
        }
        graphics.drawString(font, getPatternComponent(), posX+iconOffset, posY, -1, true);
        posY += font.lineHeight+1;
        if (TooltipsConfig.armorTrimShowIcons){
            Identifier materialIcon = TooltipsGlobals.TRIM_MATERIALS.get(this.armorTrim.material().getRegisteredName());
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, materialIcon, posX, posY, ICON_SIZE, ICON_SIZE);
        }
        graphics.drawString(font, getMaterialComponent(), posX+iconOffset, posY, -1, true);
    }

    private Component getHeaderComponent(){
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.armor_trim").append(":");
        return result.withStyle(ChatFormatting.GRAY);
    }

    private Component getPatternComponent(){
        MutableComponent result = Component.literal(this.armorTrim.pattern().value().description().getString());
        TextColor textColor = this.armorTrim.material().value().description().getStyle().getColor();
        if (textColor!=null && TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.COLORED_TEXT)
            result.withStyle(ChatFormatting.AQUA);
        if (textColor==null || TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.PLAIN_TEXT)
            result.withStyle(ChatFormatting.GRAY);
        return result;
    }

    private Component getMaterialComponent(){
        MutableComponent result = Component.literal(this.armorTrim.material().value().description().getString());
        TextColor textColor = this.armorTrim.material().value().description().getStyle().getColor();
        if (textColor!=null && TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.COLORED_TEXT)
            result.withColor(textColor.getValue());
        if (textColor==null || TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.PLAIN_TEXT)
            result.withStyle(ChatFormatting.GRAY);
        return result;
    }

    public static boolean includeArmorTrimTooltipComponent(ItemStack stack){
        boolean hasTrim = stack.has(DataComponents.TRIM);
        boolean isVanilla = TooltipsConfig.armorTrimStyle==TooltipsConfig.ArmorTrimStyle.VANILLA;
        return hasTrim && !isVanilla;
    }
}
