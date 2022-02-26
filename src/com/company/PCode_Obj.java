package com.company;

public class PCode_Obj {
    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public PCode_Obj(String country, String region) {
        Country = country;
        Region = region;
    }

    public String Country;
    public String Region;

}
