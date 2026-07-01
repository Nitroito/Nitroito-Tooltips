package pt.nitroito.tooltips.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import pt.nitroito.tooltips.TooltipsConfig;


@Config(name = "nitroito-tooltips")
public class TooltipsConfigFile implements ConfigData {

    public TooltipsConfig.BeeInformationStyle beeInformationStyle = TooltipsConfig.BeeInformationStyle.ICONS;
    public TooltipsConfig.ItemDurabilityStyle itemDurabilityStyle = TooltipsConfig.ItemDurabilityStyle.FORMATED_VALUES;
    public TooltipsConfig.EnchantmentsStyle enchantmentsStyle = TooltipsConfig.EnchantmentsStyle.COLORED_TEXT;
    public boolean enchantmentsSorted = true;
    public boolean enchantmentsRomanNumerals = true;
    public TooltipsConfig.FoodNutritionStyle foodNutritionStyle = TooltipsConfig.FoodNutritionStyle.ICONS;
    public TooltipsConfig.EffectsStyle effectsStyle = TooltipsConfig.EffectsStyle.COLORED_TEXT;
    public boolean effectsShowIcons = true;
    public boolean effectsShowModifiers = true;
    public TooltipsConfig.BucketEntityStyle bucketEntityStyle = TooltipsConfig.BucketEntityStyle.VISIBLE_WITH_MODEL;
    public boolean bucketEntityRotate = true;
    public boolean bucketEntityAnimate = true;
    public TooltipsConfig.BannerPatternStyle bannerPatternStyle = TooltipsConfig.BannerPatternStyle.COLORED_TEXT;
    public boolean bannerPatternShowIcons;
    public TooltipsConfig.MapStyle mapStyle = TooltipsConfig.MapStyle.COLORED_TEXT;
    public boolean mapShowPreview = true;
    public boolean mapShowDimension = false;
    public boolean mapShowCoordinates = false;
    public TooltipsConfig.PaintingStyle paintingStyle = TooltipsConfig.PaintingStyle.COLORED_TEXT;
    public boolean paintingShowPreview = true;
    public boolean paintingShowAuthor = true;
    public TooltipsConfig.ArmorTrimStyle armorTrimStyle = TooltipsConfig.ArmorTrimStyle.COLORED_TEXT;
    public boolean armorTrimShowIcons = true;
    public TooltipsConfig.ItemIdentifierStyle itemIdentifierStyle = TooltipsConfig.ItemIdentifierStyle.ALWAYS_VISIBLE;
    public boolean showModName = true;
    public boolean removeEmptyLines = true;
    public boolean removeCreativeTabNames = true;
    public TooltipsConfig.DurationFormat durationFormat = TooltipsConfig.DurationFormat.TIME;
}
