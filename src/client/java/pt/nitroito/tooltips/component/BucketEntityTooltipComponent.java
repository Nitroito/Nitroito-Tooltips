package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.*;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.model.BucketEntityModel;
import pt.nitroito.tooltips.utils.UtilsString;


public class BucketEntityTooltipComponent implements CustomTooltipComponent {
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
    private final ItemStack stack;
    private final CustomData entityData;
    private final BucketEntityModel entityModel;

    public BucketEntityTooltipComponent(ItemStack stack) {
        this.stack = stack;
        this.entityData = stack.get(DataComponents.BUCKET_ENTITY_DATA);
        this.entityModel = BucketEntityModel.from(stack);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int result = 0;
        if (this.entityData.isEmpty()) return result;
        if (this.entityModel==BucketEntityModel.AXOLOTL || this.entityModel==BucketEntityModel.SALMON)
            result += font.lineHeight+1;
        if (this.entityModel.isTropicalFish()) {
            result += (!getTropicalFishVariantComponent().getString().isEmpty() ? font.lineHeight+1 : 0) + font.lineHeight+1;
        }
        if (getBucketEntityCooldown()!=0)
            result += font.lineHeight+1;
        if (TooltipsConfig.bucketEntityStyle==TooltipsConfig.BucketEntityStyle.VISIBLE_WITH_MODEL){
            result += this.entityModel.tooltipHeight();
            if (getBucketEntityCooldown()<0 && this.entityModel==BucketEntityModel.AXOLOTL)
                result -= font.lineHeight+1;
        }
        return result;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        if (this.entityData.isEmpty()) return result;
        if (this.entityModel==BucketEntityModel.AXOLOTL)
            result = Math.max(result, font.width(getAxolotlVariantComponent()));
        if (this.entityModel.isTropicalFish()) {
            if (getTropicalFishVariantComponent().getString().isEmpty())
                result = Math.max(result, font.width(getTropicalFishVariantComponent()));
            result = Math.max(result, font.width(getTropicalFishPatternComponent()));
        }
        if (getBucketEntityCooldown()!=0)
            result = Math.max(result, font.width(getBucketEntityCooldownComponent()));
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {
        if (this.entityData.isEmpty()) return;

        int posY = y;
        if (this.entityModel==BucketEntityModel.AXOLOTL){
            graphics.drawString(font, getAxolotlVariantComponent(), x, posY, -1, true);
            posY += font.lineHeight+1;
        }
        if (this.entityModel==BucketEntityModel.SALMON){
            graphics.drawString(font, getSalmonVariantComponent(), x, posY, -1, true);
            posY += font.lineHeight+1;
        }
        if (this.entityModel.isTropicalFish()) {
            if (!getTropicalFishVariantComponent().getString().isEmpty()) {
                graphics.drawString(font, getTropicalFishVariantComponent(), x, posY, -1, true);
                posY += font.lineHeight+1;
            }
            graphics.drawString(font, getTropicalFishPatternComponent(), x, posY, -1, true);
            posY += font.lineHeight+1;
        }
        if (getBucketEntityCooldown()!=0){
            graphics.drawString(font, getBucketEntityCooldownComponent(), x, posY, -1, true);
            posY += font.lineHeight+1;
        }
        if (TooltipsConfig.bucketEntityStyle==TooltipsConfig.BucketEntityStyle.VISIBLE_WITH_MODEL){
            boolean isBaby = getBucketEntityCooldown()<0;
            posY -= isBaby ? font.lineHeight+1 : 0;
            this.entityModel.renderModel(graphics, this.stack, isBaby, CURRENT_TIME_MILLIS, x, posY);
        }
    }

    private MutableComponent getAxolotlVariantComponent(){
        Axolotl.Variant axolotlVariant = stack.get(DataComponents.AXOLOTL_VARIANT);
        int axolotlVariantId = axolotlVariant!=null ? axolotlVariant.getId() : -1;
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.entity.variant").append(": ").withStyle(ChatFormatting.GRAY);
        switch (axolotlVariantId) {
            case 0 -> {return result.append(Component.literal("Lucy").withColor(DyeColor.PINK.getTextColor()));}
            case 1 -> {return result.append(Component.literal("Wild").withColor(DyeColor.BROWN.getTextColor()));}
            case 2 -> {return result.append(Component.literal("Gold").withStyle(ChatFormatting.GOLD));}
            case 3 -> {return result.append(Component.literal("Cyan").withColor(DyeColor.CYAN.getTextColor()));}
            case 4 -> {return result.append(Component.literal("Blue").withStyle(ChatFormatting.BLUE));}
            default -> {return result.append(Component.literal("<Unknown>").withStyle(ChatFormatting.DARK_GRAY));}
        }
    }

    private MutableComponent getSalmonVariantComponent(){
        Salmon.Variant salmonVariant = stack.get(DataComponents.SALMON_SIZE);
        if (salmonVariant==null) return Component.empty();
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.entity.variant").append(": ").withStyle(ChatFormatting.GRAY);
        return result.append(Component.literal(UtilsString.titleCase(salmonVariant.name())).withStyle(ChatFormatting.DARK_AQUA));
    }

    private MutableComponent getTropicalFishVariantComponent() {
        TropicalFish.Pattern pattern = stack.get(DataComponents.TROPICAL_FISH_PATTERN);
        DyeColor baseColor = stack.get(DataComponents.TROPICAL_FISH_BASE_COLOR);
        DyeColor patternColor = stack.get(DataComponents.TROPICAL_FISH_PATTERN_COLOR);
        if (pattern==null || baseColor==null || patternColor==null) return Component.empty();

        TropicalFish.Variant fishVariant = new TropicalFish.Variant(pattern, baseColor, patternColor);
        if (!TropicalFish.COMMON_VARIANTS.contains(fishVariant)) return Component.empty();

        String fishName = TropicalFish.getPredefinedName(TropicalFish.COMMON_VARIANTS.indexOf(fishVariant));
        return Component.translatable(fishName).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true));
    }

    private MutableComponent getTropicalFishPatternComponent(){
        TropicalFish.Pattern pattern = stack.get(DataComponents.TROPICAL_FISH_PATTERN);
        DyeColor baseColor = stack.get(DataComponents.TROPICAL_FISH_BASE_COLOR);
        DyeColor patternColor = stack.get(DataComponents.TROPICAL_FISH_PATTERN_COLOR);
        if (pattern==null || baseColor==null || patternColor==null) return Component.empty();
        return pattern.displayName().copy().withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
            .append(Component.literal(" (").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            .append(Component.literal(UtilsString.titleCase(baseColor.getName())).withStyle(Style.EMPTY.withColor(baseColor.getTextColor())))
            .append(Component.literal(", ").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            .append(Component.literal(UtilsString.titleCase(patternColor.getName())).withStyle(Style.EMPTY.withColor(patternColor.getTextColor())))
            .append(Component.literal(")").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    private MutableComponent getBucketEntityCooldownComponent(){
        int entityCooldown = getBucketEntityCooldown();
        MutableComponent result = Component.literal(UtilsString.formatDuration(entityCooldown, 1.0f)).withStyle(ChatFormatting.WHITE);
        if(entityCooldown<0)
            return Component.translatable(Tooltips.MOD_ID+".tooltip.entity.growing_time").withStyle(ChatFormatting.GRAY).append(": ").append(result);
        if(entityCooldown>0)
            return Component.translatable(Tooltips.MOD_ID+".tooltip.entity.breeding_cooldown").withStyle(ChatFormatting.GRAY).append(": ").append(result);
        return Component.empty();
    }

    private int getBucketEntityCooldown(){
        int result = entityData.copyTag().getIntOr("Age",0);
        if (this.entityModel==BucketEntityModel.TADPOLE)
            result -= Tadpole.ticksToBeFrog;
        return result;
    }

    public static boolean includeBucketEntityTooltipComponent(ItemStack itemStack){
        boolean hasBucketEntityData = itemStack.has(DataComponents.BUCKET_ENTITY_DATA);
        boolean isHidden = TooltipsConfig.bucketEntityStyle==TooltipsConfig.BucketEntityStyle.HIDDEN;
        return hasBucketEntityData && !isHidden;
    }
}
