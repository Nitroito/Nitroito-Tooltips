package pt.nitroito.tooltips.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.component.*;
import pt.nitroito.tooltips.helper.ClientTextTooltipVisitor;
import pt.nitroito.tooltips.utils.UtilsMisc;
import java.util.List;


@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void renderTooltip(Font textRenderer, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier texture, CallbackInfo ci) {
        if (components == null || components.isEmpty() || TooltipsGlobals.getStack().isEmpty()) return;
        ItemStack currentStack =  TooltipsGlobals.getStack();
        Component headerComponent = ClientTextTooltipVisitor.getComponent(((ClientTextTooltip)components.getFirst()).text);
        Component stackComponent = ClientTextTooltipVisitor.getComponent(currentStack.getStyledHoverName().getVisualOrderText());
        if (headerComponent.equals(stackComponent)) try {
            // ======================================== WARNING ========================================
            // We have to use a Try-Catch block to avoid the game crashing on "Recipe Book" screen
            // because "components" collection is Immutable on that screen
            // =========================================================================================
            // HEADER
            components.set(0, new HeaderTooltipComponent(currentStack));
            // ======================================== NOTE ===========================================
            // This block must after "header" to avoid removal of the first entry on some modded items!!
            // MODS FOUND > Waystones
            components.removeIf(component ->
                component instanceof ClientTextTooltip textTooltip && (
                    (TooltipsConfig.removeCreativeTabNames && UtilsMisc.isCreativeGroup(textTooltip)) ||
                    (TooltipsConfig.removeEmptyLines && (UtilsMisc.isEmptyLine(textTooltip))) ||
                    (UtilsMisc.isFooterComponent(textTooltip, currentStack))
                )
            );
            // MAP
            if (MapTooltipComponent.includeMapTooltipComponent(currentStack))
                components.add(1, new MapTooltipComponent(currentStack));
            // BANNER PATTERNS (Banner & Shield)
            if (BannerTooltipComponent.includeBannerTooltipComponent(currentStack))
                components.add(1, new BannerTooltipComponent(currentStack));
            // PAINTING
            if (PaintingTooltipComponent.includePaintingTooltipComponent(currentStack))
                components.add(1, new PaintingTooltipComponent(currentStack));
            // BUCKET ENTITY
            if (BucketEntityTooltipComponent.includeBucketEntityTooltipComponent(currentStack))
                components.add(1, new BucketEntityTooltipComponent(currentStack));
            // BEES & HONEY
            if (BeesTooltipComponent.includeBeesTooltipComponent(currentStack))
                components.add(1, new BeesTooltipComponent(currentStack));
            // EFFECTS (Food, SuspiciousStew, Potion, TipperArrow & OminousBottle)
            if (EffectsTooltipComponent.includeEffectsTooltipComponent(currentStack))
                components.add(1, new EffectsTooltipComponent(currentStack));
            // FOOD NUTRITION
            if (FoodTooltipComponent.includeFoodTooltipComponent(currentStack))
                components.add(1, new FoodTooltipComponent(currentStack));
            // ARMOR TRIM
            if (ArmorTrimTooltipComponent.includeArmorTrimTooltipComponent(currentStack))
                components.add(1, new ArmorTrimTooltipComponent(currentStack));
            // DURABILITY
            if (DurabilityTooltipComponent.includeDurabilityTooltipComponent(currentStack))
                components.add(1, new DurabilityTooltipComponent(currentStack));
            // FOOTER
            components.add(new FooterTooltipComponent(currentStack));
        }catch (Exception ignored){}
        TooltipsGlobals.setStack(ItemStack.EMPTY);
    }
}
