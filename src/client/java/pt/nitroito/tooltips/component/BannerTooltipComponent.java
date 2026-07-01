package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Layer;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.utils.UtilsGraphics;

import java.util.*;


public class BannerTooltipComponent implements CustomTooltipComponent {
    private final int ICON_SIZE = Minecraft.getInstance().font.lineHeight;
    private final int ICON_SPACING = 2;
    private final List<Layer> bannerLayers = new ArrayList<>();
    private final DyeColor baseColor;

    public BannerTooltipComponent(ItemStack stack) {
        this.baseColor = stack.get(DataComponents.BASE_COLOR);
        getBannerPatternLayers(stack, this.bannerLayers);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int result = 0;
        if (this.baseColor!=null)
            result += font.lineHeight+1;
        if (!this.bannerLayers.isEmpty()){
            result += font.lineHeight+1;
            result += (font.lineHeight+1) * this.bannerLayers.size();
        }
        return result;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        int iconOffset = TooltipsConfig.bannerPatternShowIcons ? (ICON_SIZE+ICON_SIZE/2+ICON_SPACING+1) : 0;
        if (this.baseColor!=null) {
            int baseWidth = font.width(Component.translatable(Tooltips.MOD_ID+".tooltip.banner.base").append(": "));
            int bannerWidth = font.width(Component.translatable("block.minecraft."+this.baseColor.getName()+"_banner"));
            result = Math.max(result, baseWidth+iconOffset+bannerWidth);
        }
        if (!this.bannerLayers.isEmpty()) {
            int indentOffset = font.width(CommonComponents.space());
            result = Math.max(result, font.width(Component.translatable(Tooltips.MOD_ID + ".tooltip.banner.layers")));
            for (Layer layer : this.bannerLayers) {
                result = Math.max(result, indentOffset+iconOffset+font.width(layer.description()));
            }
        }
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        int posY = y;
        if (this.baseColor!=null){
            int posX = x;
            //TEXT: Base
            MutableComponent componentBase = Component.translatable(Tooltips.MOD_ID+".tooltip.banner.base").append(": ");
            graphics.drawString(font, componentBase.withStyle(ChatFormatting.GRAY), posX, posY, -1, true);
            posX += font.width(componentBase);
            //SPRITE: Banner
            if (TooltipsConfig.bannerPatternShowIcons) {
                Identifier bannerIcon = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "banner/"+this.baseColor.getName()+"_banner");
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bannerIcon, posX, posY, ICON_SIZE/2, ICON_SIZE-1);
                posX += ICON_SIZE/2 + ICON_SPACING + 1;
            }
            //TEXT: Banner Name
            MutableComponent componentBannerName = Component.translatable("block.minecraft."+this.baseColor.getName()+"_banner");
            if (TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.COLORED_TEXT) {
                int bannerColor = this.baseColor.getTextColor();
                if (this.baseColor.equals(DyeColor.GREEN)) bannerColor = UtilsGraphics.DARK_GREEN;
                if (this.baseColor.equals(DyeColor.BLACK)) bannerColor = UtilsGraphics.LIGHT_BLACK;
                graphics.drawString(font, componentBannerName.withColor(bannerColor), posX, posY, -1, true);
            }
            if (TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.PLAIN_TEXT)
                graphics.drawString(font, componentBannerName.withStyle(ChatFormatting.GRAY), posX, posY, -1, true);
            posY += font.lineHeight+1;
        }

        if (!this.bannerLayers.isEmpty()){
            Component layersTitle = Component.translatable(Tooltips.MOD_ID+".tooltip.banner.layers").withStyle(ChatFormatting.GRAY);
            graphics.drawString(font, layersTitle, x, posY, -1, true);
            posY += font.lineHeight+1;
            for (Layer layer: this.bannerLayers){
                int posX = x;
                if (TooltipsConfig.bannerPatternShowIcons) {
                    //SPRITE: Dye Color
                    Identifier colorIcon = Identifier.withDefaultNamespace("item/"+layer.color().getName()+"_dye");
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, colorIcon, posX, posY, ICON_SIZE-1, ICON_SIZE-1);
                    posX += ICON_SIZE;
                    //SPRITE: Banner Pattern
                    Identifier bannerKey = Identifier.parse(layer.pattern().getRegisteredName());
                    Identifier bannerIcon = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "banner_pattern/"+bannerKey.getPath());
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bannerIcon, posX, posY, ICON_SIZE/2, ICON_SIZE-1);
                    posX += ICON_SIZE/2 + ICON_SPACING + 1;
                }
                //TEXT: Layer Description
                if (TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.COLORED_TEXT) {
                    int layerColor = layer.color().getTextColor();
                    if (layer.color().equals(DyeColor.GREEN)) layerColor = UtilsGraphics.DARK_GREEN;
                    if (layer.color().equals(DyeColor.BLACK)) layerColor = UtilsGraphics.LIGHT_BLACK;
                    graphics.drawString(font, layer.description().withColor(layerColor), posX, posY, -1, true);
                }
                if (TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.PLAIN_TEXT)
                    graphics.drawString(font, layer.description().withStyle(ChatFormatting.GRAY), posX, posY, -1, true);
                posY += font.lineHeight+1;
            }
        }
    }

    private void getBannerPatternLayers(ItemStack stack, List<Layer> bannerLayers) {
        BannerPatternLayers bannerData = stack.get(DataComponents.BANNER_PATTERNS);
        bannerLayers.addAll(bannerData!=null ?  bannerData.layers() : List.of());
    }

    public static boolean includeBannerTooltipComponent(ItemStack stack){
        boolean hasBannerPatterns = stack.has(DataComponents.BANNER_PATTERNS);
        boolean isHidden = TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.HIDDEN;
        boolean isVanilla = TooltipsConfig.bannerPatternStyle==TooltipsConfig.BannerPatternStyle.VANILLA;
        return hasBannerPatterns && !isHidden && !isVanilla;
    }
}
