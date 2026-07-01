package pt.nitroito.tooltips;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import pt.nitroito.tooltips.config.TooltipsConfigEvents;
import pt.nitroito.tooltips.config.TooltipsConfigFile;

public class TooltipsConfig {
    public static BeeInformationStyle beeInformationStyle;
    public static ItemDurabilityStyle itemDurabilityStyle;
    public static EnchantmentsStyle enchantmentsStyle;
    public static boolean enchantmentsSorted;
    public static boolean enchantmentsRomanNumerals;
    public static FoodNutritionStyle foodNutritionStyle;
    public static EffectsStyle effectsStyle;
    public static boolean effectsShowIcons;
    public static boolean effectsShowModifiers;
    public static BucketEntityStyle bucketEntityStyle;
    public static boolean bucketEntityRotate;
    public static boolean bucketEntityAnimate;
    public static BannerPatternStyle bannerPatternStyle;
    public static boolean bannerPatternShowIcons;
    public static MapStyle mapStyle;
    public static boolean mapShowPreview;
    public static boolean mapShowDimension;
    public static boolean mapShowCoordinates;
    public static TooltipsConfig.PaintingStyle paintingStyle;
    public static boolean paintingShowPreview;
    public static boolean paintingShowAuthor;
    public static TooltipsConfig.ArmorTrimStyle armorTrimStyle;
    public static boolean armorTrimShowIcons;
    public static ItemIdentifierStyle itemIdentifierStyle;
    public static boolean showModName;
    public static boolean removeEmptyLines;
    public static boolean removeCreativeTabNames;
    public static DurationFormat durationFormat;


    public static void loadConfigFile(TooltipsConfigFile configFile) {
        beeInformationStyle = configFile.beeInformationStyle;
        itemDurabilityStyle = configFile.itemDurabilityStyle;
        enchantmentsStyle = configFile.enchantmentsStyle;
        enchantmentsSorted = configFile.enchantmentsSorted;
        enchantmentsRomanNumerals = configFile.enchantmentsRomanNumerals;
        foodNutritionStyle = configFile.foodNutritionStyle;
        effectsStyle = configFile.effectsStyle;
        effectsShowIcons = configFile.effectsShowIcons;
        effectsShowModifiers = configFile.effectsShowModifiers;
        bucketEntityStyle = configFile.bucketEntityStyle;
        bucketEntityRotate = configFile.bucketEntityRotate;
        bucketEntityAnimate = configFile.bucketEntityAnimate;
        bannerPatternStyle = configFile.bannerPatternStyle;
        bannerPatternShowIcons = configFile.bannerPatternShowIcons;
        paintingStyle = configFile.paintingStyle;
        paintingShowPreview = configFile.paintingShowPreview;
        paintingShowAuthor = configFile.paintingShowAuthor;
        mapStyle = configFile.mapStyle;
        mapShowPreview = configFile.mapShowPreview;
        mapShowDimension = configFile.mapShowDimension;
        mapShowCoordinates = configFile.mapShowCoordinates;
        armorTrimStyle = configFile.armorTrimStyle;
        armorTrimShowIcons = configFile.armorTrimShowIcons;
        itemIdentifierStyle = configFile.itemIdentifierStyle;
        showModName = configFile.showModName;
        removeEmptyLines = configFile.removeEmptyLines;
        removeCreativeTabNames = configFile.removeCreativeTabNames;
        durationFormat = configFile.durationFormat;
    }

    public static void register() {
        AutoConfig.register(TooltipsConfigFile.class, Toml4jConfigSerializer::new);
        TooltipsConfig.loadConfigFile(AutoConfig.getConfigHolder(TooltipsConfigFile.class).getConfig());
        TooltipsConfigEvents.fire();
    }

    public enum BeeInformationStyle {HIDDEN, FORMATED_VALUES, ICONS, VANILLA}
    public enum ItemDurabilityStyle {HIDDEN, VALUES, FORMATED_VALUES, PERCENTAGE, BAR, VANILLA}
    public enum EnchantmentsStyle {PLAIN_TEXT, COLORED_TEXT, VANILLA}
    public enum ItemIdentifierStyle {HIDDEN, ALWAYS_VISIBLE, VANILLA}
    public enum FoodNutritionStyle {HIDDEN, VALUES, ICONS}
    public enum EffectsStyle {HIDDEN, PLAIN_TEXT, COLORED_TEXT}
    public enum BucketEntityStyle {HIDDEN, VISIBLE, VISIBLE_WITH_MODEL}
    public enum BannerPatternStyle {HIDDEN, PLAIN_TEXT, COLORED_TEXT, VANILLA}
    public enum MapStyle {PLAIN_TEXT, COLORED_TEXT, VANILLA}
    public enum PaintingStyle {PLAIN_TEXT, COLORED_TEXT, VANILLA}
    public enum ArmorTrimStyle {PLAIN_TEXT, COLORED_TEXT, VANILLA}
    public enum DurationFormat {TICKS, SECONDS, TIME, TEXT}
}
