package pt.nitroito.tooltips.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pt.nitroito.tooltips.Tooltips;
import pt.nitroito.tooltips.TooltipsConfig;


public class TooltipsConfigScreen {

    public static Screen create(Screen parent) {
        TooltipsConfigFile config = AutoConfig.getConfigHolder(TooltipsConfigFile.class).getConfig();
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable(Tooltips.MOD_ID + ".config.title"));
        ConfigEntryBuilder entry = builder.entryBuilder();

        //==============================================================================================================
        // GENERAL SETTINGS
        //==============================================================================================================
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable(Tooltips.MOD_ID + ".config.tooltips_style"));
        //Bees
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.bees"), TooltipsConfig.BeeInformationStyle.class,config.beeInformationStyle)
            .setDefaultValue(TooltipsConfig.BeeInformationStyle.ICONS)
            .setSaveConsumer(value -> config.beeInformationStyle = value)
            .build());
        //Durability
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.durability"), TooltipsConfig.ItemDurabilityStyle.class,config.itemDurabilityStyle)
            .setDefaultValue(TooltipsConfig.ItemDurabilityStyle.FORMATED_VALUES)
            .setSaveConsumer(value -> config.itemDurabilityStyle = value)
            .build());
        //Enchantments
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.enchantments"),TooltipsConfig.EnchantmentsStyle.class, config.enchantmentsStyle)
            .setDefaultValue(TooltipsConfig.EnchantmentsStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.enchantmentsStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.enchantments.sorted"),config.enchantmentsSorted)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.enchantments.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.enchantmentsSorted = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.enchantments.roman_numeral"),config.enchantmentsRomanNumerals)
            .setTooltip(Component.translatable(Tooltips.MOD_ID +".config.tooltips.enchantments.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.enchantmentsRomanNumerals = value)
            .build());
        //Food Nutrition
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.food_nutrition"),TooltipsConfig.FoodNutritionStyle.class, config.foodNutritionStyle)
            .setDefaultValue(TooltipsConfig.FoodNutritionStyle.ICONS)
            .setSaveConsumer(value -> config.foodNutritionStyle = value)
            .build());
        //Effects
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.effects"),TooltipsConfig.EffectsStyle.class, config.effectsStyle)
            .setDefaultValue(TooltipsConfig.EffectsStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.effectsStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.effects.icons"),config.effectsShowIcons)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.effectsShowIcons = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.effects.modifiers"),config.effectsShowModifiers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.effectsShowModifiers = value)
            .build());
        //Bucket Entity
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.bucket_entity"),TooltipsConfig.BucketEntityStyle.class, config.bucketEntityStyle)
            .setDefaultValue(TooltipsConfig.BucketEntityStyle.VISIBLE_WITH_MODEL)
            .setSaveConsumer(value -> config.bucketEntityStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.bucket_entity.rotate"),config.bucketEntityRotate)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.bucketEntityRotate = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.bucket_entity.animate"),config.bucketEntityAnimate)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.bucketEntityAnimate = value)
            .build());
        //Banners
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.banner"),TooltipsConfig.BannerPatternStyle.class, config.bannerPatternStyle)
            .setDefaultValue(TooltipsConfig.BannerPatternStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.bannerPatternStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.banner.icons"),config.bannerPatternShowIcons)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.banner.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.bannerPatternShowIcons = value)
            .build());
        //Maps
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.map"),TooltipsConfig.MapStyle.class, config.mapStyle)
            .setDefaultValue(TooltipsConfig.MapStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.mapStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.map.preview"),config.mapShowPreview)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.map.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.mapShowPreview = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.map.dimension"),config.mapShowDimension)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.map.ignored"))
            .setDefaultValue(false)
            .setSaveConsumer(value -> config.mapShowDimension = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.map.coordinates"),config.mapShowCoordinates)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.map.ignored"))
            .setDefaultValue(false)
            .setSaveConsumer(value -> config.mapShowCoordinates = value)
            .build());
        //Painting
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.painting"),TooltipsConfig.PaintingStyle.class, config.paintingStyle)
            .setDefaultValue(TooltipsConfig.PaintingStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.paintingStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.painting.preview"),config.paintingShowPreview)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.painting.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.paintingShowPreview = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.painting.author"),config.paintingShowAuthor)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.painting.ignored"))
            .setDefaultValue(false)
            .setSaveConsumer(value -> config.paintingShowAuthor = value)
            .build());
        //Armor Trim
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.armor_trim"),TooltipsConfig.ArmorTrimStyle.class, config.armorTrimStyle)
            .setDefaultValue(TooltipsConfig.ArmorTrimStyle.COLORED_TEXT)
            .setSaveConsumer(value -> config.armorTrimStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(getIndentedComponent(Tooltips.MOD_ID +".config.tooltips.armor_trim.icons"),config.armorTrimShowIcons)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.armor_trim.ignored"))
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.armorTrimShowIcons = value)
            .build());
        //Footer
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.identifier"),TooltipsConfig.ItemIdentifierStyle.class, config.itemIdentifierStyle)
            .setDefaultValue(TooltipsConfig.ItemIdentifierStyle.ALWAYS_VISIBLE)
            .setSaveConsumer(value -> config.itemIdentifierStyle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(Component.translatable(Tooltips.MOD_ID +".config.tooltips.mod_name"),config.showModName)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.showModName = value)
            .build());
        general.addEntry(entry.startBooleanToggle(Component.translatable(Tooltips.MOD_ID +".config.tooltips.empty_lines"),config.removeEmptyLines)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.removeEmptyLines = value)
            .build());
        general.addEntry(entry.startBooleanToggle(Component.translatable(Tooltips.MOD_ID +".config.tooltips.creative_tabs"),config.removeCreativeTabNames)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.removeCreativeTabNames = value)
            .build());
        //General Settings
        general.addEntry(entry.startEnumSelector(Component.translatable(Tooltips.MOD_ID +".config.tooltips.duration_format"),TooltipsConfig.DurationFormat.class, config.durationFormat)
            .setTooltip(Component.translatable(Tooltips.MOD_ID + ".config.tooltips.duration_format.hint"))
            .setDefaultValue(TooltipsConfig.DurationFormat.TIME)
            .setSaveConsumer(value -> config.durationFormat = value)
            .build());

        //==============================================================================================================
        // SAVE CONFIG
        //==============================================================================================================
        builder.setSavingRunnable(() -> {
            AutoConfig.getConfigHolder(TooltipsConfigFile.class).save();
            TooltipsConfigFile cfg = AutoConfig.getConfigHolder(TooltipsConfigFile.class).getConfig();
            TooltipsConfig.loadConfigFile(cfg);
            TooltipsConfigEvents.fire();
        });

        return builder.build();
    }

    private static Component getIndentedComponent(String translationKey){
        return Component.literal("    ").append(Component.translatable(translationKey));
    }
}
