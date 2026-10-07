package com.nexora.tools

enum class RiskTier {
    LOW,          // Automated execution without user interaction
    CONFIRMATION, // Requires explicit vocal/UI confirmation
    HIGH          // Payment / Auth / Destructive operations requiring PIN or explicit confirmation
}
