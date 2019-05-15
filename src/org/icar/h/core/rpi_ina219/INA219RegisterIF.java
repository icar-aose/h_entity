package org.icar.h.core.rpi_ina219;

import java.io.IOException;

/**
 * This interface abstracts the reading/writing of INA219 registers. It's primary purpose is to allow the testing of the INA219Base class without the use of I2C hardware.
 */
interface INA219RegisterIF {
    /**
     * Write a register with provided value.
     * @param ra The address of the register to be written.
     * @param value The value to write to the register.
     * @throws IOException If the register could not be written.
     */
    void writeRegister(final RegisterAddress ra, final int value) throws IOException;

    /**
     * Reads the register at the specified address as an unsigned 16 bit value.
     * @param ra The address of the register to be read.
     * @return The read value.
     * @throws IOException If the register could not be read.
     */
    int readRegister(final RegisterAddress ra) throws IOException;

    /**
     * Reads the register at the specified address as a signed 16 bit value.
     * @param ra The address of the register to be read.
     * @return The read value.
     * @throws IOException If the register could not be read.
     */
    short readSignedRegister(final RegisterAddress ra) throws IOException;
}
