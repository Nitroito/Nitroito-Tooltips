package pt.nitroito.tooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tooltips implements ClientModInitializer {
    public static final String MOD_ID = "nitroito_tooltips";
    public static final Logger LOGGER = LoggerFactory.getLogger(Tooltips.MOD_ID);
    public static final boolean DEVELOPMENT_MODE = FabricLoader.getInstance().isDevelopmentEnvironment();

    @Override
    public void onInitializeClient() {
        TooltipsConfig.register();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            TooltipsGlobals.FABRIC_MODS.put(mod.getMetadata().getId(), mod.getMetadata().getName());
        }
        for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
            TooltipsGlobals.CREATIVE_GROUPS.add(group.getDisplayName().getString());
        }
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            BuiltInRegistries.ITEM.forEach(item->{
                Holder<TrimMaterial> trimMaterial = item.getDefaultInstance().get(DataComponents.PROVIDES_TRIM_MATERIAL);
                if (trimMaterial!=null) {
                    Identifier sprite = Identifier.withDefaultNamespace("item/"+BuiltInRegistries.ITEM.getKey(item).getPath());
                    TooltipsGlobals.TRIM_MATERIALS.put(trimMaterial.getRegisteredName(), sprite);
                }
            });
        });
    }
}
