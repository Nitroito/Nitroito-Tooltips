package pt.nitroito.tooltips.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.jetbrains.annotations.NotNull;


public class ClientTextTooltipVisitor implements FormattedCharSink {
    private final MutableComponent text = Component.empty();
    public Component getText() {return text;}

    @Override
    public boolean accept(int index, @NotNull Style style, int codePoint) {
        String car = new String(Character.toChars(codePoint));
        text.append(Component.literal(car).setStyle(style));
        return true;
    }

    public static Component getComponent(FormattedCharSequence text) {
        ClientTextTooltipVisitor visitor = new ClientTextTooltipVisitor();
        text.accept(visitor);
        return visitor.getText();
    }
}
