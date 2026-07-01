package pt.nitroito.tooltips.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.utils.UtilsString;
import java.text.*;
import java.util.*;


public class EffectsTooltipComponent implements CustomTooltipComponent {
    private record RecordEffect(MobEffectInstance effect, float durationScale, float probability){}
    private record RecordModifier(Attribute attribute, AttributeModifier modifier){}
    private final int ICON_SIZE = 8;
    private final int ICON_MARGIN = 2;
    private final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.#%", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private final List<RecordEffect> effectsList = new ArrayList<>();
    private final List<RecordModifier> modifiersList = new ArrayList<>();
    private final ItemStack stack;

    public EffectsTooltipComponent(ItemStack stack) {
        this.stack = stack;
        getEffectsAndModifiers(stack, this.effectsList, this.modifiersList);
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int result = 0;
        result += (font.lineHeight+1)*this.effectsList.size();
        if (this.effectsList.isEmpty() && providesPotionEffects())
            result += font.lineHeight+1;
        if (!this.modifiersList.isEmpty() && TooltipsConfig.effectsShowModifiers) {
            result += font.lineHeight+1;
            if (!TooltipsConfig.removeEmptyLines)
                result += font.lineHeight+1;
            for (RecordModifier effectEntry : this.modifiersList)
                if (effectEntry.modifier.amount()!=0)
                    result += font.lineHeight+1;
        }
        return result;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int result = 0;
        for (RecordEffect effectEntry : this.effectsList) {
            int iconOffset = TooltipsConfig.effectsShowIcons ? ICON_SIZE+ICON_MARGIN : 0;
            result = Math.max(result, font.width(getEffectComponent(effectEntry))+iconOffset);
        }
        if (!this.modifiersList.isEmpty() && TooltipsConfig.effectsShowModifiers) {
            for (RecordModifier modifierEntry : this.modifiersList)
                if (modifierEntry.modifier.amount()!=0)
                    result = Math.max(result, font.width(getModifierComponent(modifierEntry)));
        }
        return result;
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor graphics, Font font, int x, int y, int w, int h) {
        int posY = y;
        int iconOffset = TooltipsConfig.effectsShowIcons ? ICON_SIZE+ICON_MARGIN : 0;
        if (this.effectsList.isEmpty() && providesPotionEffects())
            graphics.text(font, Component.translatable("effect.none").withStyle(ChatFormatting.GRAY), x, posY, -1, true);
        if (!this.effectsList.isEmpty()){
            for (RecordEffect effectEntry : this.effectsList) {
                MutableComponent effectComponent = getEffectComponent(effectEntry);
                if (TooltipsConfig.effectsShowIcons) {
                    Identifier effectIcon = Gui.getMobEffectSprite(effectEntry.effect.getEffect());
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, effectIcon, x, posY, ICON_SIZE, ICON_SIZE);
                }
                graphics.text(font, effectComponent, x+iconOffset, posY, -1, true);
                posY += font.lineHeight+1;
            }
            if (!this.modifiersList.isEmpty() && TooltipsConfig.effectsShowModifiers) {
                if (!TooltipsConfig.removeEmptyLines)
                    posY += font.lineHeight+1;
                graphics.text(font, Component.translatable("potion.whenDrank").withStyle(ChatFormatting.GRAY), x, posY, -1, true);
                posY += font.lineHeight+1;
                for (RecordModifier modifierEntry : this.modifiersList) {
                    double amount = modifierEntry.modifier.amount();
                    if (amount > 0.0){
                        graphics.text(font, getModifierComponent(modifierEntry).withStyle(ChatFormatting.BLUE), x, posY, -1, true);
                        posY += font.lineHeight+1;
                    }
                    if (amount < 0.0) {
                        graphics.text(font, getModifierComponent(modifierEntry).withStyle(ChatFormatting.RED), x, posY, -1, true);
                        posY += font.lineHeight+1;
                    }
                }
            }
        }
    }

    private MutableComponent getEffectComponent(RecordEffect effectEntry){
        MutableComponent result = Component.translatable(effectEntry.effect.getDescriptionId());
        if (effectEntry.effect.getAmplifier()>0)
            result.append(" "+ UtilsString.integerToRoman(effectEntry.effect.getAmplifier()));
        if (!effectEntry.effect.isInfiniteDuration()){
            result.append(" ("+UtilsString.formatDuration(effectEntry.effect.getDuration(), effectEntry.durationScale)+")");
        }
        if (effectEntry.probability>0 && effectEntry.probability<1){
            result.append(" ["+ PERCENTAGE_FORMAT.format(effectEntry.probability)+"]");
        }
        if (TooltipsConfig.effectsStyle == TooltipsConfig.EffectsStyle.COLORED_TEXT)
            result.withColor(effectEntry.effect.getEffect().value().getColor() + 0xFF000000);
        if (TooltipsConfig.effectsStyle==TooltipsConfig.EffectsStyle.PLAIN_TEXT)
            result.withStyle(ChatFormatting.GRAY);
        return result;
    }

    private MutableComponent getModifierComponent(RecordModifier entry){
        boolean operationAddMultipliedBase = entry.modifier.operation()==AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        boolean operationAddMultipliedTotal = entry.modifier.operation()==AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        double displayAmount;
        int operationId = entry.modifier.operation().id();
        MutableComponent descriptionComponent = Component.translatable(entry.attribute.getDescriptionId());

        if (!operationAddMultipliedBase && !operationAddMultipliedTotal) {
            displayAmount = entry.modifier.amount();
        } else {
            displayAmount = entry.modifier.amount() * 100.0;
        }
        if (entry.modifier.amount() > 0.0) {
            String amountString = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount);
            return Component.translatable("attribute.modifier.plus."+operationId, amountString, descriptionComponent);
        }
        if (entry.modifier.amount() < 0.0) {
            String amountString = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount*-1.0);
            return Component.translatable("attribute.modifier.take."+operationId, amountString, descriptionComponent);
        }
        return Component.empty();
    }

    private void getEffectsAndModifiers(ItemStack stack, List<RecordEffect> effectsList, List<RecordModifier> modifiersList) {
        if (stack.has(DataComponents.CONSUMABLE)) {
            Consumable consumables = stack.get(DataComponents.CONSUMABLE);
            if (consumables!=null && !consumables.onConsumeEffects().isEmpty())
                for (ConsumeEffect consumeEffect : consumables.onConsumeEffects())
                    if (consumeEffect instanceof ApplyStatusEffectsConsumeEffect statusEffects)
                        for (MobEffectInstance entry : statusEffects.effects()) {
                            effectsList.add(new RecordEffect(entry, 1.0f, statusEffects.probability()));
                            entry.getEffect().value().createModifiers(entry.getAmplifier(),(a,m)->modifiersList.add(new RecordModifier(a.value(), m)));
                        }
        }
        if (stack.has(DataComponents.SUSPICIOUS_STEW_EFFECTS) && TooltipsGlobals.isCreativeMode()){
            SuspiciousStewEffects suspiciousStewEffects = stack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
            if (suspiciousStewEffects!=null && !suspiciousStewEffects.effects().isEmpty())
                for (SuspiciousStewEffects.Entry entry : suspiciousStewEffects.effects()){
                    effectsList.add(new RecordEffect(entry.createEffectInstance(), 1.0f, 1.0f));
                    entry.effect().value().createModifiers(entry.createEffectInstance().getAmplifier(),(a, m)->modifiersList.add(new RecordModifier(a.value(), m)));
                }
        }
        if (stack.has(DataComponents.POTION_CONTENTS)){
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            if (potionContents!=null){
                potionContents.getAllEffects().forEach(entry-> {
                    Float durationScale = stack.get(DataComponents.POTION_DURATION_SCALE);
                    effectsList.add(new RecordEffect(entry, 1.0f, durationScale!=null ? durationScale : 1.0f));
                    entry.getEffect().value().createModifiers(entry.getAmplifier(),(a,m)->modifiersList.add(new RecordModifier(a.value(), m)));
                });
            }
        }
        if (stack.has(DataComponents.OMINOUS_BOTTLE_AMPLIFIER)){
            OminousBottleAmplifier amplifier = stack.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
            if (amplifier!=null){
                MobEffectInstance effect = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, amplifier.value(), false, false, true);
                effectsList.add(new RecordEffect(effect, 1.0f, 1.0f));
            }
        }
    }

    private boolean providesPotionEffects(){
        return this.stack.is(Items.POTION) || this.stack.is(Items.LINGERING_POTION) || this.stack.is(Items.SPLASH_POTION);
    }

    public static boolean includeEffectsTooltipComponent(ItemStack itemStack){
        boolean hasConsumable = itemStack.has(DataComponents.CONSUMABLE);
        boolean hasSuspiciousStewEffects = itemStack.has(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        boolean hasPotionContents = itemStack.has(DataComponents.POTION_CONTENTS);
        boolean hasOminousAmplifier = itemStack.has(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
        boolean showEffects = TooltipsConfig.effectsStyle!=TooltipsConfig.EffectsStyle.HIDDEN;
        return (hasConsumable || hasSuspiciousStewEffects || hasPotionContents || hasOminousAmplifier) && showEffects;
    }
}
