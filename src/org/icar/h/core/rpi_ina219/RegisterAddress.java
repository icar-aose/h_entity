package org.icar.h.core.rpi_ina219;

/**
 * Enumeration of the register addresses used by the INA219.
 */
public enum RegisterAddress {
    CONFIGURATION(0), SHUNT_VOLTAGE(1), BUS_VOLTAGE(2), POWER(3), CURRENT(4), CALIBRATION(5);

    private final int addr;

    RegisterAddress(final int a) {
        addr = a;
    }

    int getValue() {
        return addr;
    }

}
