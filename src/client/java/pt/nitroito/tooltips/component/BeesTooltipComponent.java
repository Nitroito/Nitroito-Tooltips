package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.Occupant;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.utils.UtilsMisc;

import java.util.*;


public class BeesTooltipComponent implements CustomTooltipComponent {
    private final int ICON_SIZE = 8;
    private final int ICON_MARGIN = 2;
    private final int MAX_BEE_COUNT = BeehiveBlockEntity.MAX_OCCUPANTS;
    private final int MAX_HONEY_LEVELS = BeehiveBlock.MAX_HONEY_LEVELS;
    private final Identifier BEE = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/bee");
    private final Identifier BEE_GRAY = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/bee_gray");
    private final Identifier HONEY = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/honeycomb");
    private final Identifier HONEY_GRAY = Identifier.fromNamespaceAndPath(Tooltips.MOD_ID, "icon/honeycomb_gray");
    private final List<Occupant> bees;
    private final BlockItemStateProperties honey;

    public BeesTooltipComponent(ItemStack stack) {
        this.bees = stack.getOrDefault(DataComponents.BEES, Bees.EMPTY).bees();
		this.honey = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return (font.lineHeight+1)*2;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        if (TooltipsConfig.beeInformationStyle==TooltipsConfig.BeeInformationStyle.FORMATED_VALUES){
            result = Math.max(result, font.width(getBeeCountComponent())+font.width(getValueComponent(0,MAX_BEE_COUNT)));
            result = Math.max(result, font.width(getHoneyLevelComponent())+font.width(getValueComponent(0,MAX_HONEY_LEVELS)));
        }
        if (TooltipsConfig.beeInformationStyle==TooltipsConfig.BeeInformationStyle.ICONS) {
            result = Math.max(result, font.width(getBeeCountComponent())+ ICON_MARGIN +(MAX_BEE_COUNT*ICON_SIZE));
            result = Math.max(result, font.width(getHoneyLevelComponent())+ ICON_MARGIN +(MAX_HONEY_LEVELS*ICON_SIZE));
        }
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Font font, int x, int y, int w, int h) {
        Integer propertyValue = honey.get(BlockStateProperties.LEVEL_HONEY);
        int beeCount = this.bees.size();
        int honeyLevel = propertyValue!=null ? propertyValue : 0;
        int x1 = x + font.width(getBeeCountComponent())+ ICON_MARGIN;
        int x2 = x + font.width(getHoneyLevelComponent())+ ICON_MARGIN;
        int y2 = y+font.lineHeight+1;
        graphics.text(font, getBeeCountComponent(), x, y, -1, true);
        graphics.text(font, getHoneyLevelComponent(), x, y2, -1, true);

        if (TooltipsConfig.beeInformationStyle==TooltipsConfig.BeeInformationStyle.FORMATED_VALUES) {
            graphics.text(font, getValueComponent(beeCount, MAX_BEE_COUNT), x1, y, -1, true);
            graphics.text(font, getValueComponent(honeyLevel, MAX_HONEY_LEVELS), x2, y2, -1, true);
        }
        if (TooltipsConfig.beeInformationStyle==TooltipsConfig.BeeInformationStyle.ICONS) {
            for (int i=0; i<MAX_BEE_COUNT; i++)
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, i<beeCount ? BEE : BEE_GRAY, x1+(i*ICON_SIZE), y, ICON_SIZE, ICON_SIZE);
            for (int i=0; i<MAX_HONEY_LEVELS; i++)
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, i<honeyLevel ? HONEY : HONEY_GRAY, x2+(i*ICON_SIZE), y2, ICON_SIZE, ICON_SIZE);
        }
    }

    private MutableComponent getBeeCountComponent(){
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.beehive.bees").append(":");
        return result.withStyle(ChatFormatting.GRAY);
    }

    private MutableComponent getHoneyLevelComponent(){
        MutableComponent result = Component.translatable(Tooltips.MOD_ID+".tooltip.beehive.honey").append(":");
        return result.withStyle(ChatFormatting.GRAY);
    }

    private Component getValueComponent(int value, int maxValue){
        MutableComponent result = CommonComponents.space();
        result.append(Component.literal(String.valueOf(value)).withStyle(UtilsMisc.getFormatingStyle(value, maxValue)));
        result.append(Component.literal(" / ").withStyle(ChatFormatting.GRAY));
        result.append(Component.literal(String.valueOf(maxValue)).withStyle(ChatFormatting.GOLD));
        return result;
    }

    public static boolean includeBeesTooltipComponent(ItemStack stack){
        BlockItemStateProperties blockState = stack.get(DataComponents.BLOCK_STATE);
        boolean hasBees = stack.has(DataComponents.BEES);
        boolean hasHoney = blockState!=null && blockState.get(BlockStateProperties.LEVEL_HONEY)!=null;
        boolean isHidden = TooltipsConfig.beeInformationStyle == TooltipsConfig.BeeInformationStyle.HIDDEN;
        boolean isVanilla = TooltipsConfig.beeInformationStyle == TooltipsConfig.BeeInformationStyle.VANILLA;
        return (hasBees || hasHoney) && !isHidden && !isVanilla;
    }
}
