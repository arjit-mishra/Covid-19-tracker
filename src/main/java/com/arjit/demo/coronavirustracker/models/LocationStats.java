package com.arjit.demo.coronavirustracker.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LocationStats {
    private String state;
    private String country;
    private Long latestTotalCases;
    private Long differenceFromPreviousDay;
    private Long latestTotalDeath;
    private Long deathDifferenceFromPreviousDay;
    private Long latestTotalRecovery;
    private Long recoveryDifferenceFromPreviousDay;


}
