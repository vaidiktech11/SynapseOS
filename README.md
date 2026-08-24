# SynapseOS 🧠⚡
> **On-Device Edge-AI Cognitive Gap Diagnostic & Multimodal Study Suite**  
> *Built for the iQOO Hackathon | Powered by Snapdragon® 8 Elite NPU & iQOO Office Kit*

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Runtime](https://img.shields.io/badge/Inference-Google%20LiteRT--LM-blue.svg)](https://ai.google.dev/edge/litert)
[![Security](https://img.shields.io/badge/Database-SQLCipher%20AES--256-red.svg)](https://www.zetetic.net/sqlcipher/)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

---

## 📌 Overview

**SynapseOS** transforms the smartphone from a passive screen into an active, on-device sensory diagnostic tool for STEM education. Unlike standard multiple-choice quiz apps or cloud-based chat wrappers, SynapseOS operates **100% offline** on the device's **Hexagon NPU** to diagnose the exact algebraic and conceptual breakdown points in handwritten problem-solving, transcribe live classroom lectures in real-time, and beam tailored remedial workflows to a paired PC via the **iQOO Office Kit**.

---

## 🚀 Key Features

### 1. 📷 Synapse Snap (Step-Wise Diagnostic Engine)
* Uses CameraX and on-device Vision-Language Models (`Qwen2.5-VL` / `PaliGemma`) to scan physical paper workings.
* Isolates step-by-step mathematical reasoning ($Step\ 1 \rightarrow Step\ 2 \rightarrow Step\ 3$).
* Pinpoints root prerequisite errors (e.g., correct physical formula setup, but sign error during algebraic substitution) instead of binary $0/1$ grading.

### 2. 🎙️ Focus Flow (Offline Lecture Scribe)
* Employs on-device `Whisper.cpp (Tiny)` for continuous real-time classroom speech-to-text.
* Works seamlessly in zero-connectivity environments (Airplane Mode).
* Auto-summarizes key formulas and generates markdown flashcards at lecture completion.

### 3. 📊 Cognitive Gap & Telemetry Engine
* Tracks hesitation time, stroke erasures, and solution latency per step.
* Builds an on-device **Knowledge Graph** linking observed mistakes to prerequisite conceptual gaps.

### 4. 💻 iQOO Office Kit Bridge
* Seamlessly bridges mobile data capture with laptop deep work.
* Automatically compiles a **Remedial Practice Worksheet (PDF)** and syncs it directly to the student's PC desktop folder via P2P local transfer.

---

## 🏗️ Architecture Pipeline

```text
  [Physical Input]               [iQOO 15 Hardware Engine]               [Student PC / Output]
Camera (Paper Steps) ──►  Snapdragon 8 Elite Hexagon NPU       ──►  Office Kit Local Bridge
Mic (Lecture Audio)  ──►  • Qwen2.5-VL / Phi-4 / Whisper-tiny   ──►  Auto-Synced Remedial PDF
On-Screen Telemetry  ──►  • SQLCipher Encrypted Database      ──►  PC Study Dashboard
