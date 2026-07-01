package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import java.text.*;
import java.util.*;


public class FoodTooltipComponent implements CustomTooltipComponent {
    private final int ICON_SIZE = 8;
    private final int ICON_MARGIN = 2;
    private final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.#%", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private final DecimalFormat FLOAT_FORMAT = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private final Identifier NUTRITION = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/food");
    private final Identifier NUTRITION_HALF = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/food_half");
    private final FoodProperties foodProperties;

    public FoodTooltipComponent(ItemStack stack) {
        this.foodProperties = stack.get(DataComponents.FOOD);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return (font.lineHeight+1)*2;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        int nutritionWidth = font.width(getNutritionComponent())+ ICON_MARGIN;
        if (TooltipsConfig.foodNutritionStyle==TooltipsConfig.FoodNutritionStyle.VALUES){
            int valueWidth = font.width(Component.literal(FLOAT_FORMAT.format(foodProperties.nutrition()/2)));
            result = Math.max(result, nutritionWidth+valueWidth);
        }
        if (TooltipsConfig.foodNutritionStyle==TooltipsConfig.FoodNutritionStyle.ICONS) {
            int iconsWidth=(foodProperties.nutrition()/2)*ICON_SIZE;
            if (foodProperties.nutrition() % 2 != 0) iconsWidth += ICON_SIZE;
            result = Math.max(result, nutritionWidth+iconsWidth);
        }
        result = Math.max(result, font.width(getSaturationComponent()));
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Font font, int x, int y, int w, int h) {
        int posY = y;
        int posX = x + font.width(getNutritionComponent())+ ICON_MARGIN;
        int foodNutrition = foodProperties.nutrition();
        graphics.text(font, getNutritionComponent(), x, posY, -1, true);
        if (TooltipsConfig.foodNutritionStyle==TooltipsConfig.FoodNutritionStyle.VALUES) {
            Component valueComponent = Component.literal(FLOAT_FORMAT.format(foodNutrition/2)).withStyle(ChatFormatting.WHITE);
            graphics.text(font, valueComponent, posX, posY, -1, true);
        }
        if (TooltipsConfig.foodNutritionStyle==TooltipsConfig.FoodNutritionStyle.ICONS) {
            int maxCount =  foodNutrition/2;
            for (int i=0; i<maxCount; i++) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NUTRITION, posX, y, ICON_SIZE, ICON_SIZE);
                posX += ICON_SIZE;
            }
            if (foodNutrition % 2 != 0)
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NUTRITION_HALF, posX, y, ICON_SIZE, ICON_SIZE);
        }
        posY += font.lineHeight+1;
        graphics.text(font, getSaturationComponent(), x, posY, -1, true);
    }

    private MutableComponent getNutritionComponent() {
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.food.nutrition").append(":");
        return result.withStyle(ChatFormatting.GRAY);
    }

    private MutableComponent getSaturationComponent(){
        float saturationRatio = foodProperties.saturation() / (foodProperties.nutrition() *2 );
        MutableComponent result = Component.literal("+"+PERCENTAGE_FORMAT.format(saturationRatio));
        result.append(CommonComponents.space());
        result.append(Component.translatable("effect.minecraft.saturation"));
        result.setStyle(Style.EMPTY.withColor(DyeColor.CYAN.getTextColor()));
        return result;
    }

    public static boolean includeFoodTooltipComponent(ItemStack itemStack){
        boolean hasFoodProperties = itemStack.has(DataComponents.FOOD);
        boolean hasEntityDataComponent = itemStack.has(DataComponents.BUCKET_ENTITY_DATA);
        boolean showFoodNutrition = TooltipsConfig.foodNutritionStyle!=TooltipsConfig.FoodNutritionStyle.HIDDEN;
        return hasFoodProperties && !hasEntityDataComponent && showFoodNutrition;
    }
}
