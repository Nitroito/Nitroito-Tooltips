package pt.nitroito.tooltips.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pt.nitroito.tooltips.utils.UtilsMisc;


public class HeaderTooltipComponent implements CustomTooltipComponent {
    private static final int ICON_SIZE = 16;
    private static final int ICON_MARGIN = 2;
    private final ItemStack stack;
    private final Component name;
    private final Component rarity;

    public HeaderTooltipComponent(ItemStack stack) {
        this.stack = stack;
        this.name = UtilsMisc.getDisplayName(stack);
        this.rarity = UtilsMisc.getDisplayName(stack.getRarity());
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return (font.lineHeight+1) * 2 + 1;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return  Math.max(font.width(this.name.getString()), font.width(this.rarity.getString())) + (ICON_SIZE+(ICON_MARGIN*3));
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, Font font, int x, int y, int w, int h) {

        //graphics.fill(posX, posY+(ICON_SIZE/2), posX+ICON_SIZE, posY+ICON_SIZE+(ICON_SIZE/2), 0xFF888888);
        graphics.renderItem(stack, x+ICON_MARGIN, y+ICON_MARGIN+1);

        int posX = x+ICON_SIZE+(ICON_MARGIN*3);
        int posY = y+ICON_MARGIN;

        graphics.drawString(font, this.name, posX, posY, -1, true);
        posY += font.lineHeight+1;
        graphics.drawString(font, this.rarity, posX, posY, -1, true);
    }
}
