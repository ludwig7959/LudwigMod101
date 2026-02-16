package xyz.ludwicz.ludwigmod.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.saturation.FoodHelper;
import xyz.ludwicz.ludwigmod.saturation.FoodValues;

import java.util.ArrayList;
import java.util.List;

public class TooltipMod {

    private static final Identifier MC_ICONS = new Identifier("textures/gui/icons.png");
    private static final Identifier MOD_ICONS = new Identifier("appleskin", "textures/icons.png");

    private static final TextureOffsets normalBarTextureOffsets = new TextureOffsets();

    static {
        normalBarTextureOffsets.containerNegativeHunger = 43;
        normalBarTextureOffsets.containerExtraHunger = 133;
        normalBarTextureOffsets.containerNormalHunger = 16;
        normalBarTextureOffsets.containerPartialHunger = 124;
        normalBarTextureOffsets.containerMissingHunger = 34;
        normalBarTextureOffsets.shankMissingFull = 70;
        normalBarTextureOffsets.shankMissingPartial = normalBarTextureOffsets.shankMissingFull + 9;
        normalBarTextureOffsets.shankFull = 52;
        normalBarTextureOffsets.shankPartial = normalBarTextureOffsets.shankFull + 9;
    }

    private static final TextureOffsets rottenBarTextureOffsets = new TextureOffsets();

    static {
        rottenBarTextureOffsets.containerNegativeHunger = normalBarTextureOffsets.containerNegativeHunger;
        rottenBarTextureOffsets.containerExtraHunger = normalBarTextureOffsets.containerExtraHunger;
        rottenBarTextureOffsets.containerNormalHunger = normalBarTextureOffsets.containerNormalHunger;
        rottenBarTextureOffsets.containerPartialHunger = normalBarTextureOffsets.containerPartialHunger;
        rottenBarTextureOffsets.containerMissingHunger = normalBarTextureOffsets.containerMissingHunger;
        rottenBarTextureOffsets.shankMissingFull = 106;
        rottenBarTextureOffsets.shankMissingPartial = rottenBarTextureOffsets.shankMissingFull + 9;
        rottenBarTextureOffsets.shankFull = 88;
        rottenBarTextureOffsets.shankPartial = rottenBarTextureOffsets.shankFull + 9;
    }

    static class TextureOffsets {
        int containerNegativeHunger;
        int containerExtraHunger;
        int containerNormalHunger;
        int containerPartialHunger;
        int containerMissingHunger;
        int shankMissingFull;
        int shankMissingPartial;
        int shankFull;
        int shankPartial;
    }

    static abstract class EmptyText implements Text {
        @Override
        public Style getStyle() {
            return Style.EMPTY;
        }

        @Override
        public TextContent getContent() {
            return TextContent.EMPTY;
        }

        static List<Text> emptySiblings = new ArrayList<Text>();

        @Override
        public List<Text> getSiblings() {
            return emptySiblings;
        }
    }

    // Bind to text line, because food overlay must apply line offset of all case.
    public static class FoodOverlayTextComponent extends EmptyText implements OrderedText {
        public FoodOverlay foodOverlay;

        FoodOverlayTextComponent(FoodOverlay foodOverlay) {
            this.foodOverlay = foodOverlay;
        }

        @Override
        public OrderedText asOrderedText() {
            return this;
        }

        @Override
        public boolean accept(CharacterVisitor visitor) {
            return TextVisitFactory.visitFormatted(this, getStyle(), visitor);
        }
    }

    public static class FoodOverlay implements TooltipComponent, TooltipData {
        private FoodValues defaultFood;
        private FoodValues modifiedFood;

        private int biggestHunger;
        private float biggestSaturationIncrement;

        private int hungerBars;
        private String hungerBarsText;

        private int saturationBars;
        private String saturationBarsText;

        private ItemStack itemStack;

        FoodOverlay(ItemStack itemStack, FoodValues defaultFood, FoodValues modifiedFood, PlayerEntity player) {
            this.itemStack = itemStack;
            this.defaultFood = defaultFood;
            this.modifiedFood = modifiedFood;

            biggestHunger = Math.max(defaultFood.hunger, modifiedFood.hunger);
            biggestSaturationIncrement = Math.max(defaultFood.getSaturationIncrement(), modifiedFood.getSaturationIncrement());

            hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
            if (hungerBars > 10) {
                hungerBarsText = "x" + ((biggestHunger < 0 ? -1 : 1) * hungerBars);
                hungerBars = 1;
            }

            saturationBars = (int) Math.ceil(Math.abs(biggestSaturationIncrement) / 2f);
            if (saturationBars > 10 || saturationBars == 0) {
                saturationBarsText = "x" + ((biggestSaturationIncrement < 0 ? -1 : 1) * saturationBars);
                saturationBars = 1;
            }
        }

        boolean shouldRenderHungerBars() {
            return hungerBars > 0;
        }

        @Override
        public int getHeight() {
            // hunger + spacing + saturation + arbitrary spacing,
            // for some reason 3 extra looks best
            return 9 + 1 + 7 + 3;
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            int hungerBarLength = hungerBars * 9;
            if (hungerBarsText != null) {
                hungerBarLength += textRenderer.getWidth(hungerBarsText);
            }
            int saturationBarLength = saturationBars * 7;
            if (saturationBarsText != null) {
                saturationBarLength += textRenderer.getWidth(saturationBarsText);
            }
            return Math.max(hungerBarLength, saturationBarLength);
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext drawContext) {
            renderTooltip(drawContext, this, x, y, 0, textRenderer);
        }
    }

    public static void onItemTooltip(ItemStack hoveredStack, PlayerEntity player, TooltipContext context, List<Text> tooltip) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (hoveredStack == null || tooltip == null || config == null)
            return;

        if(shouldShowRepairCost(hoveredStack)) {
            tooltip.add(Text.translatable("tooltip.ludwigmod.repaircost", hoveredStack.getRepairCost()));
        }

        if (shouldShowFoodValues(hoveredStack)) {
            FoodValues defaultFood = FoodHelper.getDefaultFoodValues(hoveredStack);
            FoodValues modifiedFood = FoodHelper.getModifiedFoodValues(hoveredStack, player);

            FoodOverlay foodOverlay = new FoodOverlay(hoveredStack, defaultFood, modifiedFood, player);
            if (foodOverlay.shouldRenderHungerBars()) {
                tooltip.add(new FoodOverlayTextComponent(foodOverlay));
            }
        }
    }

    public static void renderTooltip(DrawContext drawContext, FoodOverlay foodOverlay, int toolTipX, int toolTipY, int tooltipZ, TextRenderer textRenderer) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (drawContext == null || config == null)
            return;

        // Not found overlay text lines, maybe some mods removed it.
        if (foodOverlay == null)
            return;

        ItemStack itemStack = foodOverlay.itemStack;

        FoodValues defaultFood = foodOverlay.defaultFood;
        FoodValues modifiedFood = foodOverlay.modifiedFood;

        int x = toolTipX;
        int y = toolTipY;
        MatrixStack matrixStack = drawContext.getMatrices();

        int defaultFoodHunger = defaultFood.hunger;
        int modifiedFoodHunger = modifiedFood.hunger;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render from right to left so that the icons 'face' the right way
        x += (foodOverlay.hungerBars - 1) * 9;

        TextureOffsets offsets = FoodHelper.isRotten(itemStack) ? rottenBarTextureOffsets : normalBarTextureOffsets;
        for (int i = 0; i < foodOverlay.hungerBars * 2; i += 2) {

            if (modifiedFoodHunger < 0)
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, offsets.containerNegativeHunger, 27, 9, 9, 256, 256);
            else if (modifiedFoodHunger > defaultFoodHunger && defaultFoodHunger <= i)
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, offsets.containerExtraHunger, 27, 9, 9, 256, 256);
            else if (modifiedFoodHunger > i + 1 || defaultFoodHunger == modifiedFoodHunger)
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, offsets.containerNormalHunger, 27, 9, 9, 256, 256);
            else if (modifiedFoodHunger == i + 1)
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, offsets.containerPartialHunger, 27, 9, 9, 256, 256);
            else {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, .5F);
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, offsets.containerMissingHunger, 27, 9, 9, 256, 256);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, .25F);
            drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, defaultFoodHunger - 1 == i ? offsets.shankMissingPartial : offsets.shankMissingFull, 27, 9, 9, 256, 256);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (modifiedFoodHunger > i)
                drawContext.drawTexture(MC_ICONS, x, y, tooltipZ, modifiedFoodHunger - 1 == i ? offsets.shankPartial : offsets.shankFull, 27, 9, 9, 256, 256);

            x -= 9;
        }
        if (foodOverlay.hungerBarsText != null) {
            x += 18;
            matrixStack.push();
            matrixStack.translate(x, y, tooltipZ);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            drawContext.drawTextWithShadow(textRenderer, foodOverlay.hungerBarsText, 2, 2, 0xFFAAAAAA);
            matrixStack.pop();
        }

        x = toolTipX;
        y += 10;

        float modifiedSaturationIncrement = modifiedFood.getSaturationIncrement();
        float absModifiedSaturationIncrement = Math.abs(modifiedSaturationIncrement);

        // Render from right to left so that the icons 'face' the right way
        x += (foodOverlay.saturationBars - 1) * 7;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        for (int i = 0; i < foodOverlay.saturationBars * 2; i += 2) {
            float effectiveSaturationOfBar = (absModifiedSaturationIncrement - i) / 2f;

            boolean shouldBeFaded = absModifiedSaturationIncrement <= i;
            if (shouldBeFaded)
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, .5F);

            drawContext.drawTexture(MOD_ICONS, x, y, tooltipZ, effectiveSaturationOfBar >= 1 ? 21 : effectiveSaturationOfBar > 0.5 ? 14 : effectiveSaturationOfBar > 0.25 ? 7 : effectiveSaturationOfBar > 0 ? 0 : 28, modifiedSaturationIncrement >= 0 ? 27 : 34, 7, 7, 256, 256);

            if (shouldBeFaded)
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            x -= 7;
        }
        if (foodOverlay.saturationBarsText != null) {
            x += 14;
            matrixStack.push();
            matrixStack.translate(x, y, tooltipZ);
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            drawContext.drawTextWithShadow(textRenderer, foodOverlay.saturationBarsText, 2, 1, 0xFFAAAAAA);
            matrixStack.pop();
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MC_ICONS);

        // reset to drawHoveringText state
        RenderSystem.disableDepthTest();
    }

    private static boolean shouldShowFoodValues(ItemStack hoveredStack) {
        if (hoveredStack.isEmpty())
            return false;

        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.showFoodValuesInTooltip)
            return false;

        if (!FoodHelper.isFood(hoveredStack))
            return false;

        return true;
    }


    private static boolean shouldShowRepairCost(ItemStack hoveredStack) {
        if (hoveredStack.isEmpty())
            return false;

        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.showRepairCostInTooltip)
            return false;

        if (!hoveredStack.isDamageable())
            return false;

        return true;
    }
}
