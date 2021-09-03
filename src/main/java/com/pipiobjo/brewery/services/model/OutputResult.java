package com.pipiobjo.brewery.services.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutputResult {
    long motorControlTargetPositionInc;
    BigDecimal motorControlTargetPercent;
}
