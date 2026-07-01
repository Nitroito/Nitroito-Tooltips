package pt.nitroito.tooltips.utils;

import net.minecraft.util.Mth;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;

import java.util.Locale;
import java.util.TreeMap;
import java.util.Set;


public class UtilsString {
    private static final TreeMap<Integer,String> ROMAN_NUMBERS = new TreeMap<>();


    public static String integerToRoman(int number) {
        if (number<0) number = Math.abs(number);
        if (number>3999)
            return String.valueOf(number);
        if (number==ROMAN_NUMBERS.floorKey(number))
            return ROMAN_NUMBERS.get(number);
        int key = ROMAN_NUMBERS.floorKey(number);
        return ROMAN_NUMBERS.get(key)+ integerToRoman(number-key);
    }

    public static String formatDuration(int durationTicks, float durationScale){
        durationTicks = Mth.floor(durationTicks * durationScale);

        if (TooltipsConfig.durationFormat==TooltipsConfig.DurationFormat.TICKS)
            return Math.abs(durationTicks)+"tk";

        int totalSeconds = Math.abs(durationTicks)/Math.round(TooltipsGlobals.getTickRate());

        if (TooltipsConfig.durationFormat==TooltipsConfig.DurationFormat.SECONDS)
            return totalSeconds+"s";

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (TooltipsConfig.durationFormat==TooltipsConfig.DurationFormat.TIME){
            if (hours>0) return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds);
            if (minutes>0 || seconds>0) return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
        }
        if (TooltipsConfig.durationFormat==TooltipsConfig.DurationFormat.TEXT){
            if (hours>0) return String.format("%dh %dm %ds", hours, minutes, seconds);
            if (minutes>0) return String.format("%dm %ds", minutes, seconds);
            if (seconds>0) return String.format("%ds", seconds);
        }
        return String.format("%dms", (1000/Math.round(TooltipsGlobals.getTickRate()))*Math.abs(durationTicks));
    }

    public static String titleCase(String text) {
        Set<String> smallWords = Set.of("a", "an", "the", "of", "in", "on", "at", "to", "for", "and", "or");
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i > 0 && smallWords.contains(word)) {
                result.append(word);
            } else {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
            if (i < words.length - 1) {result.append(' ');}
        }
        return result.toString();
    }

    static{
        ROMAN_NUMBERS.put(1000,"M");
        ROMAN_NUMBERS.put(900,"CM");
        ROMAN_NUMBERS.put(500,"D");
        ROMAN_NUMBERS.put(400,"CD");
        ROMAN_NUMBERS.put(100,"C");
        ROMAN_NUMBERS.put(90,"XC");
        ROMAN_NUMBERS.put(50,"L");
        ROMAN_NUMBERS.put(40,"XL");
        ROMAN_NUMBERS.put(10,"X");
        ROMAN_NUMBERS.put(9,"IX");
        ROMAN_NUMBERS.put(5,"V");
        ROMAN_NUMBERS.put(4,"IV");
        ROMAN_NUMBERS.put(1,"I");
        ROMAN_NUMBERS.put(0,"O");
    }
}
