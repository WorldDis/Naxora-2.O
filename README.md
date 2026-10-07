# NEXORA — AI Accessibility & Action Agent

> **Tagline:** *"You speak. Nexora understands. Nexora acts."*

NEXORA is an autonomous, accessibility-first AI action agent designed for Android. It uses offline Speech-to-Text, Android Accessibility Services, and decoupled AI reasoning to perform multi-step tasks across system applications, the local file system, and web interfaces.

---

## Key Features
- **Offline Voice Control:** Speech-to-Text via Vosk & Native Text-to-Speech (English & Bengali support).
- **Semantic UI Interaction:** Operates via Android Accessibility Trees instead of hardcoded screen coordinates.
- **Decoupled AI Brain:** Swappable AI provider layer (Cloud LLMs & Local SLMs).
- **Zero-Trust Safety Model:** Enforces confirmation for sensitive and high-risk actions.

---

## Project Structure
```text
Nexora/
├── app/                  # Main Android Application Module
├── .gitignore            # Git exclusion rules
├── settings.gradle.kts   # Root Gradle Configuration
└── README.md             # Project documentation
