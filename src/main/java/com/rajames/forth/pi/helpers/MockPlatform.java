package com.rajames.forth.pi.helpers;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformBase;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import com.pi4j.plugin.pigpio.provider.serial.PiGpioSerialProvider;
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider;
import com.pi4j.plugin.raspberrypi.platform.RaspberryPiPlatform;

/**
 * Custom Pi4J platform class which ensures that PiGPIO is used for interacting with the hardware components.
 * Without these overrides the auto-detection is not guaranteed to pick a well-known working set of plugins.
 */
public class MockPlatform extends PlatformBase<MockPlatform> implements Platform {
    /**
     * Helper method for instantiating a new Pi4J context based on the {@link MockPlatform} platform with PiGPIO plugins.
     *
     * @return Pi4J context instance
     */
    public static Context buildNewContext() {

        // Build Pi4J context with this platform and PiGPIO providers
        return Pi4J.newContextBuilder()
            .noAutoDetect()
            .add(new MockPlatform())
            .build();
    }



    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean enabled(Context context) {
        return false;
    }

    /**
     * Override default providers which would otherwise be inherited from {@link RaspberryPiPlatform} with an empty list.
     * This is required for manually controlling which providers are being loaded as part of {@link #buildNewContext()}.
     *
     * @return Empty provider list
     */
    @Override
    protected String[] getProviders() {
        return new String[]{};
    }
}
