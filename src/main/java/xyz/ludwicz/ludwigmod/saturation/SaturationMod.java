package xyz.ludwicz.ludwigmod.saturation;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import org.lwjgl.opengl.GL11;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.util.IntPoint;

import java.util.Random;
import java.util.Vector;

public class SaturationMod {

    private static float unclampedFlashAlpha = 0f;
    private static float flashAlpha = 0f;
    private static byte alphaDir = 1;
    private static int foodIconsOffset;
    private static boolean needDisableBlend = false;

    public static int FOOD_BAR_HEIGHT = 39;

    public static final Vector<IntPoint> healthBarOffsets = new Vector<>();
    public static final Vector<IntPoint> foodBarOffsets = new Vector<>();

    private static final Random random = new Random();
    private static final Identifier MC_ICONS = new Identifier("textures/gui/icons.png");
    private static final Identifier MOD_ICONS = new Identifier("appleskin", "textures/icons.png");

    public static final int TOOLTIP_REAL_HEIGHT_OFFSET_BOTTOM = 3;
    public static final int TOOLTIP_REAL_HEIGHT_OFFSET_TOP = -3;
    public static final int TOOLTIP_REAL_WIDTH_OFFSET_RIGHT = 3;

    public static void preRenderHUD(DrawContext drawContext) {
        foodIconsOffset = FOOD_BAR_HEIGHT;

        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.showExhaustionOverlay)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        assert player != null;

        int right = mc.getWindow().getScaledWidth() / 2 + 91;
        int top = mc.getWindow().getScaledHeight() - foodIconsOffset;
        float exhaustion = player.getHungerManager().getExhaustion();

        drawExhaustionOverlay(drawContext, exhaustion, mc, right, top, 1f);
    }

    public static void renderHUD(DrawContext drawContext) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null)
            return;

        if (!shouldRenderAnyOverlays())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        assert player != null;
        HungerManager stats = player.getHungerManager();

        int top = mc.getWindow().getScaledHeight() - foodIconsOffset;
        int left = mc.getWindow().getScaledWidth() / 2 - 91; // left of health bar
        int right = mc.getWindow().getScaledWidth() / 2 + 91; // right of food bar

        // generate at the beginning to avoid ArrayIndexOutOfBoundsException
        generateBarOffsets(top, left, right, mc.inGameHud.getTicks(), player);

        if (config.showSaturationOverlay)
            drawSaturationOverlay(drawContext, 0, stats.getSaturationLevel(), mc, right, top, 1F);

        // try to get the item stack in the player hand
        ItemStack heldItem = player.getMainHandStack();
        if (!FoodHelper.canConsume(heldItem, player))
            heldItem = player.getOffHandStack();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && FoodHelper.canConsume(heldItem, player);
        if (!shouldRenderHeldItemValues) {
            resetFlash();
            return;
        }

        // restored hunger/saturation overlay while holding food
        FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(heldItem, player);

        // draw health overlay if needed
        if (shouldShowEstimatedHealth(heldItem, modifiedFoodValues)) {
            float foodHealthIncrement = FoodHelper.getEstimatedHealthIncrement(heldItem, modifiedFoodValues, player);
            float currentHealth = player.getHealth();
            float modifiedHealth = Math.min(currentHealth + foodHealthIncrement, player.getMaxHealth());

            if (currentHealth < modifiedHealth)
                drawHealthOverlay(drawContext, player.getHealth(), modifiedHealth, mc, left, top, flashAlpha);
        }

        if (config.showHungerRestoredByFood) {
            int foodHunger = modifiedFoodValues.hunger;
            float foodSaturationIncrement = modifiedFoodValues.getSaturationIncrement();

            drawHungerOverlay(drawContext, foodHunger, stats.getFoodLevel(), mc, right, top, flashAlpha, FoodHelper.isRotten(heldItem));

            int newFoodValue = stats.getFoodLevel() + foodHunger;
            float newSaturationValue = stats.getSaturationLevel() + foodSaturationIncrement;

            float saturationGained = newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodSaturationIncrement;
            drawSaturationOverlay(drawContext, saturationGained, stats.getSaturationLevel(), mc, right, top, flashAlpha);
        }
    }

    public static void drawSaturationOverlay(DrawContext drawContext, float saturationGained, float saturationLevel, MinecraftClient mc, int right, int top, float alpha) {
        if (saturationLevel + saturationGained < 0)
            return;

        enableAlpha(alpha);

        float modifiedSaturation = Math.max(0, Math.min(saturationLevel + saturationGained, 20));

        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        // when require rendering the gained saturation, start should relocation to current saturation tail.
        if (saturationGained != 0)
            startSaturationBar = (int) Math.max(saturationLevel / 2.0F, 0);

        int iconSize = 9;

        for (int i = startSaturationBar; i < endSaturationBar; ++i) {
            // gets the offset that needs to be render of icon
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            int v = 0;
            int u = 0;

            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize;
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = 1 * iconSize;

            drawContext.drawTexture(MOD_ICONS, x, y, u, v, iconSize, iconSize);
        }

        // rebind default icons
        RenderSystem.setShaderTexture(0, MC_ICONS);
        disableAlpha(alpha);
    }

    public static void drawHungerOverlay(DrawContext drawContext, int hungerRestored, int foodLevel, MinecraftClient mc, int right, int top, float alpha, boolean useRottenTextures) {
        if (hungerRestored <= 0)
            return;

        enableAlpha(alpha);

        int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        int startFoodBars = Math.max(0, foodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        int iconStartOffset = 16;
        int iconSize = 9;

        for (int i = startFoodBars; i < endFoodBars; ++i) {
            // gets the offset that needs to be render of icon
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            // location to normal food by default
            int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;
            int ub = iconStartOffset + 1 * iconSize;

            // relocation to rotten food
            if (useRottenTextures) {
                u += 4 * iconSize;
                ub += 12 * iconSize;
            }

            // relocation to half food
            if (i * 2 + 1 == modifiedFood)
                u += 1 * iconSize;

            // very faint background
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.25F);
            drawContext.drawTexture(MC_ICONS, x, y, ub, v, iconSize, iconSize);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            drawContext.drawTexture(MC_ICONS, x, y, u, v, iconSize, iconSize);
        }

        disableAlpha(alpha);
    }

    public static void drawHealthOverlay(DrawContext drawContext, float health, float modifiedHealth, MinecraftClient mc, int right, int top, float alpha) {
        if (modifiedHealth <= health)
            return;

        enableAlpha(alpha);
        mc.getTextureManager().bindTexture(MC_ICONS);

        int fixedModifiedHealth = (int) Math.ceil(modifiedHealth);
        boolean isHardcore = mc.player.getWorld() != null && mc.player.getWorld().getLevelProperties().isHardcore();

        int startHealthBars = (int) Math.max(0, (Math.ceil(health) / 2.0F));
        int endHealthBars = (int) Math.max(0, Math.ceil(modifiedHealth / 2.0F));

        int iconStartOffset = 16;
        int iconSize = 9;

        for (int i = startHealthBars; i < endHealthBars; ++i) {
            // gets the offset that needs to be render of icon
            IntPoint offset = healthBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            // location to full heart icon by default
            int v = 0 * iconSize;
            int u = iconStartOffset + 4 * iconSize;
            int ub = iconStartOffset + 1 * iconSize;

            // relocation to half heart
            if (i * 2 + 1 == fixedModifiedHealth)
                u += 1 * iconSize;

            // relocation to special heart of hardcore
            if (isHardcore)
                v = 5 * iconSize;

            //// apply the status effects of the player
            //if (player.hasStatusEffect(StatusEffects.POISON)) {
            //	u += 4 * iconSize;
            //} else if (player.hasStatusEffect(StatusEffects.WITHER)) {
            //	u += 8 * iconSize;
            //}

            // very faint background
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.25F);
            drawContext.drawTexture(MC_ICONS, x, y, ub, v, iconSize, iconSize);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            drawContext.drawTexture(MC_ICONS, x, y, u, v, iconSize, iconSize);
        }

        disableAlpha(alpha);
    }

    public static void drawExhaustionOverlay(DrawContext drawContext, float exhaustion, MinecraftClient mc, int right, int top, float alpha) {
        float maxExhaustion = FoodHelper.MAX_EXHAUSTION;
        // clamp between 0 and 1
        float ratio = Math.min(1, Math.max(0, exhaustion / maxExhaustion));
        int width = (int) (ratio * 81);
        int height = 9;

        enableAlpha(.75f);
        drawContext.drawTexture(MOD_ICONS, right - width, top, 81 - width, 18, width, height);
        disableAlpha(.75f);

        // rebind default icons
        RenderSystem.setShaderTexture(0, MC_ICONS);
    }

    private static boolean shouldRenderAnyOverlays() {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null)
            return false;

        return config.showHungerRestoredByFood || config.showSaturationOverlay || config.showEstimatedHealth;
    }

    private static void enableAlpha(float alpha) {
        needDisableBlend = !GL11.glIsEnabled(GL11.GL_BLEND);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }

    private static void disableAlpha(float alpha) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (needDisableBlend)
            RenderSystem.disableBlend();
    }


    public static void tick() {
        unclampedFlashAlpha += alphaDir * 0.125F;
        if (unclampedFlashAlpha >= 1.5F) {
            alphaDir = -1;
        } else if (unclampedFlashAlpha <= -0.5F) {
            alphaDir = 1;
        }
        flashAlpha = Math.max(0F, Math.min(1F, unclampedFlashAlpha)) * Math.max(0F, Math.min(1F, 0.65F));
    }

    public static void resetFlash() {
        unclampedFlashAlpha = flashAlpha = 0;
        alphaDir = 1;
    }


    private static boolean shouldShowEstimatedHealth(ItemStack hoveredStack, FoodValues modifiedFoodValues) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.showEstimatedHealth)
            return false;

        if (healthBarOffsets.size() == 0)
            return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        HungerManager stats = player.getHungerManager();

        // in the `PEACEFUL` mode, health will restore faster
        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL)
            return false;

        // when player has any changes health amount by any case can't show estimated health
        // because player will confused how much of restored/damaged healths
        if (stats.getFoodLevel() >= 18)
            return false;

        if (player.hasStatusEffect(StatusEffects.POISON))
            return false;

        if (player.hasStatusEffect(StatusEffects.WITHER))
            return false;

        if (player.hasStatusEffect(StatusEffects.REGENERATION))
            return false;

        return true;
    }

    private static void generateBarOffsets(int top, int left, int right, int ticks, PlayerEntity player) {
        final int preferHealthBars = 10;
        final int preferFoodBars = 10;

        final float maxHealth = player.getMaxHealth();
        final float absorptionHealth = (float) Math.ceil(player.getAbsorptionAmount());

        int healthBars = (int) Math.ceil((maxHealth + absorptionHealth) / 2.0F);
        int healthRows = (int) Math.ceil((float) healthBars / (float) preferHealthBars);

        int healthRowHeight = Math.max(10 - (healthRows - 2), 3);

        boolean shouldAnimatedHealth = false;
        boolean shouldAnimatedFood = false;

        HungerManager hungerManager = player.getHungerManager();

        // in vanilla saturation level is zero will show hunger animation
        float saturationLevel = hungerManager.getSaturationLevel();
        int foodLevel = hungerManager.getFoodLevel();
        shouldAnimatedFood = saturationLevel <= 0.0F && ticks % (foodLevel * 3 + 1) == 0;

        // in vanilla health is too low (below 5) will show heartbeat animation
        // when regeneration will also show heartbeat animation, but we don't need now
        shouldAnimatedHealth = Math.ceil(player.getHealth()) <= 4;

        // hard code in `InGameHUD`
        random.setSeed((long) (ticks * 312871));

        // Special case for infinite/NaN. Infinite absorption has been seen in the wild.
        // This will effectively disable rendering while health is infinite.
        if (!Float.isFinite(maxHealth + absorptionHealth))
            healthBars = 0;

        // adjust the size
        if (healthBarOffsets.size() != healthBars)
            healthBarOffsets.setSize(healthBars);

        if (foodBarOffsets.size() != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars);

        // left alignment, multiple rows, reverse
        for (int i = healthBars - 1; i >= 0; --i) {
            int row = (int) Math.ceil((float) (i + 1) / (float) preferHealthBars) - 1;
            int x = left + i % preferHealthBars * 8;
            int y = top - row * healthRowHeight;
            // apply the animated offset
            if (shouldAnimatedHealth)
                y += random.nextInt(2);

            // reuse the point object to reduce memory usage
            IntPoint point = healthBarOffsets.get(i);
            if (point == null) {
                point = new IntPoint();
                healthBarOffsets.set(i, point);
            }

            point.x = x - left;
            point.y = y - top;
        }

        // right alignment, single row
        for (int i = 0; i < preferFoodBars; ++i) {
            int x = right - i * 8 - 9;
            int y = top;

            // apply the animated offset
            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1;

            // reuse the point object to reduce memory usage
            IntPoint point = foodBarOffsets.get(i);
            if (point == null) {
                point = new IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y - top;
        }
    }

}
