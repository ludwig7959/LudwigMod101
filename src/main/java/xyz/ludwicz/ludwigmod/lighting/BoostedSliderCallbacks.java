package xyz.ludwicz.ludwigmod.lighting;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.client.option.SimpleOption.SliderCallbacks;

import java.util.Optional;

public enum BoostedSliderCallbacks implements SliderCallbacks<Double> {
    INSTANCE;

    @Override
    public Optional<Double> validate(Double double_) {
        return double_ >= LightingMod.minBrightness && double_ <= LightingMod.maxBrightness ? Optional.of(double_) : Optional.empty();
    }

    @Override
    public double toSliderProgress(Double double_) {
        double range = LightingMod.maxBrightness - LightingMod.minBrightness;
        double offset = LightingMod.minBrightness;
        return (double_ - offset) / range;
    }

    @Override
    public Double toValue(double d) {
        double range = LightingMod.maxBrightness - LightingMod.minBrightness;
        double offset = LightingMod.minBrightness;
        return d * range + offset;
    }

    @Override
    public Codec<Double> codec() {
        return Codec.either(Codec.doubleRange(LightingMod.minBrightness, LightingMod.maxBrightness), Codec.BOOL).xmap(either -> either.map(value -> value, value -> value != false ? 1.0 : 0.0), Either::left);
    }
}