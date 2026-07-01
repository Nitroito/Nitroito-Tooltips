package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapDecorations.Entry;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.saveddata.maps.*;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.utils.UtilsString;


public class MapTooltipComponent implements CustomTooltipComponent {
    private final int ICON_SIZE = 8;
    private final int ICON_MARGIN = 1;
    private final int MAP_PREVIEW_OFFSET = 4;
    private final int MAP_PREVIEW_SIZE = 100;
    private final int MAP_BACKGROUND_SIZE = MAP_PREVIEW_SIZE+(MAP_PREVIEW_OFFSET*2);
    private final int MAP_MARGIN = 1;
    private final MapId mapId;
    private final MapDecorations mapDecorations;
    private final MapPostProcessing mapPostProcessing;
    private final MapItemSavedData mapSavedData;

    public MapTooltipComponent(ItemStack stack) {
        this.mapId = stack.get(DataComponents.MAP_ID);
        this.mapDecorations = stack.get(DataComponents.MAP_DECORATIONS);
        this.mapPostProcessing = stack.get(DataComponents.MAP_POST_PROCESSING);
        this.mapSavedData = MapItem.getSavedData(stack, TooltipsGlobals.getLevel());
    }

    @Override
    public int getHeight(@NotNull Font font) {
        if (this.mapSavedData==null) return 0;
        int result = 0;
        result += font.lineHeight+1;
        result += font.lineHeight+1;
        if (TooltipsConfig.mapShowDimension)
            result += font.lineHeight+1;
        if (TooltipsConfig.mapShowPreview)
            result += MAP_BACKGROUND_SIZE+MAP_MARGIN;
        if (!this.mapDecorations.decorations().isEmpty() && TooltipsConfig.mapShowCoordinates)
            result += (font.lineHeight+1);
        result += 2;
        return result;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (this.mapSavedData == null) return 0;
        int result = 0;
        result = Math.max(result, font.width(getMapIdComponent()));
        result = Math.max(result, font.width(getMapLevelComponent()));
        if (TooltipsConfig.mapShowDimension)
            result = Math.max(result, font.width(getMapDimensionComponent()));
        if (TooltipsConfig.mapShowPreview)
            result = Math.max(result, MAP_BACKGROUND_SIZE);
        if (!this.mapDecorations.decorations().isEmpty() && TooltipsConfig.mapShowCoordinates) {
            Entry mapMarker = this.mapDecorations.decorations().values().stream().toList().getFirst();
            result = Math.max(result, ICON_SIZE+ICON_MARGIN+font.width(getMapMarkerComponent(mapMarker)));
        }
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        if (this.mapSavedData==null) return;
        int posY = y;
        graphics.drawString(font, getMapIdComponent(), x, posY, -1, true);
        posY += font.lineHeight+1;
        graphics.drawString(font, getMapLevelComponent(), x, posY, -1, true);
        posY += font.lineHeight+1;
        if (TooltipsConfig.mapShowDimension){
            graphics.drawString(font, getMapDimensionComponent(), x, posY, -1, true);
            posY += font.lineHeight + 1;
        }
        if (TooltipsConfig.mapShowPreview){
            renderMapPreview(graphics,x, posY);
            posY += MAP_BACKGROUND_SIZE+MAP_MARGIN;
        }
        if (!this.mapDecorations.decorations().values().isEmpty() && TooltipsConfig.mapShowCoordinates) {
            Entry mapMarker = this.mapDecorations.decorations().values().stream().toList().getFirst();
            Identifier markerIcon = Identifier.withDefaultNamespace("map/decorations/"+ mapMarker.type().value().assetId().getPath());
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, markerIcon, x, posY+1, ICON_SIZE, ICON_SIZE);
            graphics.drawString(font, getMapMarkerComponent(mapMarker), x+ICON_SIZE+ICON_MARGIN, posY+1, -1, true);
        }
    }

    private void renderMapPreview(GuiGraphics graphics, int x, int y) {
        Identifier mapBackground = Identifier.withDefaultNamespace("map/map_background_checkerboard");
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, mapBackground, x, y, MAP_BACKGROUND_SIZE, MAP_BACKGROUND_SIZE);
        MapRenderer mapRenderer = Minecraft.getInstance().getMapRenderer();
        MapRenderState renderState = new MapRenderState();
        mapRenderer.extractRenderState(this.mapId, this.mapSavedData, renderState);
        float mapScale = (float)MAP_PREVIEW_SIZE/128;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x+MAP_PREVIEW_OFFSET, y+MAP_PREVIEW_OFFSET);
        graphics.pose().scale(mapScale, mapScale);
        graphics.submitMapRenderState(renderState);
        graphics.pose().popMatrix();
    }

    private Component getMapIdComponent(){
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.map.id").withStyle(ChatFormatting.GRAY).append(": ");
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.PLAIN_TEXT)
            result.append(Component.literal("#"+this.mapId.id()).withStyle(ChatFormatting.GRAY));
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.COLORED_TEXT)
            result.append(Component.literal("#"+this.mapId.id()).withStyle(ChatFormatting.WHITE));
        if (this.mapSavedData.locked || this.mapPostProcessing==MapPostProcessing.LOCK) {
            if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.PLAIN_TEXT)
                result.append(Component.literal(" ("+Component.translatable("filled_map.locked").getString()+")").withStyle(ChatFormatting.GRAY));
            if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.COLORED_TEXT)
                result.append(Component.literal(" ("+Component.translatable("filled_map.locked").getString()+")").withStyle(ChatFormatting.RED));
        }
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.PLAIN_TEXT)
            result.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        return result;
    }

    private Component getMapLevelComponent(){
        int mapLevel = this.mapSavedData.scale;
        int mapScale = 1 << Math.min(mapLevel+(this.mapPostProcessing==MapPostProcessing.SCALE ? 1 : 0), MapItemSavedData.MAX_SCALE);
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.map.level").withStyle(ChatFormatting.GRAY).append(": ");
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.PLAIN_TEXT)
            result.append(Component.literal(String.format("%d/%d [1:%d]",mapLevel, MapItemSavedData.MAX_SCALE, mapScale)).withStyle(ChatFormatting.GRAY));
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.COLORED_TEXT)
            result.append(Component.literal(String.format("%d/%d [1:%d]",mapLevel, MapItemSavedData.MAX_SCALE, mapScale)).withStyle(ChatFormatting.WHITE));
        return result;
    }

    private Component getMapDimensionComponent(){
        MutableComponent result = Component.empty();
        result.append(Component.translatable(Tooltips.MOD_ID+".tooltip.map.dimension").withStyle(ChatFormatting.GRAY)).append(": ");
        Identifier dimensionIdentifier = this.mapSavedData.dimension.identifier();
        if (TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.PLAIN_TEXT)
            return result.append(Component.literal(UtilsString.titleCase(dimensionIdentifier.getPath())).withStyle(ChatFormatting.GRAY));
        return switch (dimensionIdentifier.toString()) {
            case "minecraft:overworld" -> result.append(Component.literal(UtilsString.titleCase(dimensionIdentifier.getPath())).withColor(0xFF55AA55));
            case "minecraft:the_nether" -> result.append(Component.literal(UtilsString.titleCase(dimensionIdentifier.getPath())).withColor(0xFFAA0000));
            case "minecraft:the_end" -> result.append(Component.literal(UtilsString.titleCase(dimensionIdentifier.getPath())).withColor(0xFFAA00AA));
            default -> result.append(Component.literal(UtilsString.titleCase(dimensionIdentifier.getPath())).withStyle(ChatFormatting.GRAY));
        };
    }

    private Component getMapMarkerComponent(Entry marker){
        return Component.literal(String.format(": %dx%d",(int)marker.x(), (int)marker.z())).withStyle(ChatFormatting.GRAY);
    }

    public static boolean includeMapTooltipComponent(ItemStack itemStack){
        boolean hasMapId = itemStack.has(DataComponents.MAP_ID);
        boolean hasMapDecoration = itemStack.has(DataComponents.MAP_DECORATIONS);
        boolean hasMapPostProcessing = itemStack.has(DataComponents.MAP_POST_PROCESSING);
        boolean isVanilla = TooltipsConfig.mapStyle==TooltipsConfig.MapStyle.VANILLA;
        return (hasMapId || hasMapDecoration || hasMapPostProcessing) && !isVanilla;
    }
}
