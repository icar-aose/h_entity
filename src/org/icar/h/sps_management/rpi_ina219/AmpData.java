package org.icar.h.sps_management.rpi_ina219;


import java.io.Serializable;


public class AmpData  {

    private Double[] current;
    private Double[] volt;


    public AmpData (){

        this.current = new Double[4];
        this.volt = new Double[4];
    }

    public void setCurrent(Double current , int i) {
        this.current[i] = current;
    }

    public void setVolt(Double volt, int i) {
        this.volt[i] = volt;
    }

    public Double getCurrent(int i)
    {
        return this.current[i];
    }


}
