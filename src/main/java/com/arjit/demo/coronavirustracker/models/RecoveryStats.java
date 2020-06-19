package com.arjit.demo.coronavirustracker.models;

import lombok.Data;

@Data
public class RecoveryStats {
    private Long latestTotalRecovery;
    private Long recoveryDifferenceFromPreviousDay;
}
