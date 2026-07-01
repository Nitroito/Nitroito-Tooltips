package pt.nitroito.tooltips;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.*;


public class TooltipsGlobals {
    public static final ArrayList<String> CREATIVE_GROUPS = new ArrayList<>();
    public static final Map<String, String> FABRIC_MODS = new HashMap<>();
    public static final Map<String, Identifier> TRIM_MATERIALS = new HashMap<>();


    private static ItemStack itemStack = ItemStack.EMPTY;
    private static Identifier dataComponentType;
    private static TooltipFlag tooltipFlag;
    private static TooltipContext tooltipContext;

    public static ItemStack getStack() {return itemStack;}
    public static void setStack(ItemStack stack) {itemStack = stack;}
    public static Identifier getDataComponentType() {return dataComponentType;}
    public static void setDataComponentType(Identifier dataType) {dataComponentType = dataType;}
    public static void setTooltipContext(TooltipContext context) {tooltipContext = context;}
    public static void setTooltipFlag(TooltipFlag flag) {tooltipFlag = flag;}

    public static float getTickRate(){
        return tooltipContext.tickRate();
    }
    public static Level getLevel() {
        return Minecraft.getInstance().player.level();
    }
    public static boolean isAdvancedTooltip(){
        return tooltipFlag.isAdvanced();
    }
    public static boolean isCreativeMode(){
        return tooltipFlag.isCreative();
    }
}
