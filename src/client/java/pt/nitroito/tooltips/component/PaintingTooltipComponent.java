package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;


public class PaintingTooltipComponent implements CustomTooltipComponent {
    private final int PAINTING_SCALE = 32;
    private final int PAINTING_MARGIN = 2;
    private final PaintingVariant paintingVariant;

    public PaintingTooltipComponent(ItemStack stack) {
        this.paintingVariant = getPaintingVariant(stack);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        if (this.paintingVariant==null) return 0;
        int result = 0;
        if (this.paintingVariant.title().isPresent())
            result += font.lineHeight+1;
        result += font.lineHeight+1;
        if (TooltipsConfig.paintingShowPreview)
            result += (this.paintingVariant.height() * PAINTING_SCALE) + PAINTING_MARGIN;
        if (this.paintingVariant.author().isPresent() && TooltipsConfig.paintingShowAuthor)
            result += font.lineHeight+1;
        return result;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (this.paintingVariant==null) return 0;
        int result = 0;
        result = Math.max(result, font.width(getTitleComponent()));
        result = Math.max(result, font.width(getSizeComponent()));
        if (TooltipsConfig.paintingShowPreview)
            result = Math.max(result, this.paintingVariant.width() * PAINTING_SCALE);
        if (TooltipsConfig.paintingShowAuthor)
            result = Math.max(result, font.width(getAuthorComponent()));
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        if (this.paintingVariant==null) return;
        int posY = y;
        if (this.paintingVariant.title().isPresent()){
            graphics.drawString(font, getTitleComponent(), x, posY, -1, true);
            posY += font.lineHeight+1;
        }
        graphics.drawString(font, getSizeComponent(), x, posY, -1, true);
        posY += font.lineHeight+1;
        if (TooltipsConfig.paintingShowPreview){
            renderPainting(graphics, x, posY);
            posY += this.paintingVariant.height()*PAINTING_SCALE+PAINTING_MARGIN;
        }
        if (this.paintingVariant.author().isPresent() && TooltipsConfig.paintingShowAuthor){
            graphics.drawString(font, getAuthorComponent(), x, posY, -1, true);
        }
    }

    private Component getTitleComponent(){
        if (this.paintingVariant.title().isEmpty()) return Component.empty();
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.painting.title").withStyle(ChatFormatting.GRAY).append(": ");
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.PLAIN_TEXT)
            return result.append(this.paintingVariant.title().get().copy().withStyle(ChatFormatting.GRAY));
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.COLORED_TEXT)
            return result.append(this.paintingVariant.title().get().copy().withStyle(ChatFormatting.WHITE));
        return Component.empty();
    }

    private Component getSizeComponent(){
        int paintingWidth = this.paintingVariant.width();
        int paintingHeight = this.paintingVariant.height();
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.painting.size").withStyle(ChatFormatting.GRAY).append(": ");
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.PLAIN_TEXT)
            return result.append(Component.literal(String.format("%dx%d", paintingWidth, paintingHeight)).withStyle(ChatFormatting.GRAY));
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.COLORED_TEXT)
            return result.append(Component.literal(String.format("%dx%d", paintingWidth, paintingHeight)).withStyle(ChatFormatting.WHITE));
        return Component.empty();
    }

    private Component getAuthorComponent(){
        if (this.paintingVariant.author().isEmpty()) return Component.empty();
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.PLAIN_TEXT)
            return this.paintingVariant.author().get().copy().withStyle(ChatFormatting.GRAY);
        if (TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.COLORED_TEXT)
            return  this.paintingVariant.author().get().copy().withStyle(ChatFormatting.YELLOW);
        return Component.empty();
    }

    private PaintingVariant getPaintingVariant(ItemStack itemStack){
        Holder<PaintingVariant> variantData = itemStack.get(DataComponents.PAINTING_VARIANT);
        return variantData!=null ? variantData.value() : null;
    }

    private void renderPainting(GuiGraphics graphics, int x, int y){
        int paintingWidth = this.paintingVariant.width() * PAINTING_SCALE;
        int paintingHeight = this.paintingVariant.height() * PAINTING_SCALE;
        Identifier textureIdentifier = Identifier.withDefaultNamespace("textures/atlas/paintings.png");
        TextureAtlas textureAtlas = (TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(textureIdentifier);
        TextureAtlasSprite paintingSprite = textureAtlas.getSprite(this.paintingVariant.assetId());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, paintingSprite, x, y, paintingWidth, paintingHeight);
    }

    public static boolean includePaintingTooltipComponent(ItemStack stack){
        boolean hasPaintingVariant = stack.has(DataComponents.PAINTING_VARIANT);
        boolean isVanilla = TooltipsConfig.paintingStyle==TooltipsConfig.PaintingStyle.VANILLA;
        return hasPaintingVariant && !isVanilla;
    }
}
