package com.uib.gateway.Enums;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED,

    PENDING,       // Application submitted, KYC not started
    UNDER_REVIEW,  // KYC process ongoing
    APPROVED,      // KYC successful, merchant approved
    REJECTED,      // KYC failed, merchant rejected
    SUSPENDED      // Account suspended (e.g., due to suspicious activity)
}
