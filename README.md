# Midnight Fade

Android prototype for a gradual, bedtime-friendly screen tint after midnight.

## Goal
When the phone is used after 00:00 local time, gradually add a warm, low-saturation-looking translucent tint and gentle dimming, without abrupt transitions. Reset at 06:00. This is a visual reminder, **not** a clinically validated way to induce sleep.

## Android limitations
A normal Android app cannot reliably change the saturation of all other apps. The prototype uses an optional system overlay to apply a warm translucent tint; it does **not** change actual display saturation or system brightness. The user must explicitly grant Android's **Display over other apps** permission. Some secure/system surfaces cannot be covered. A foreground service is required for reliable background operation on modern Android, with a persistent notification and user-visible stop control.

## Implementation status
This repository contains the initial product specification and implementation plan. No APK, background service, deployment or production-ready code has been implemented yet.

## Proposed MVP
- Kotlin + Jetpack Compose Android app (min SDK 29)
- User-configurable start/end time (default 00:00–06:00)
- Warm tint overlay ramping from 0% to a configurable cap over 45 minutes
- Immediate pause/stop and accessibility-friendly controls
- Local-only settings, no tracking or account
- Battery, accessibility, OEM restrictions and permission revocation testing

## Next build milestones
1. Android app scaffold and settings UI.
2. Foreground service, notification, overlay permission flow.
3. Timed alpha ramp with manual override and overnight rollover.
4. Instrumented device tests and signed APK CI build.

## Safety
Avoid suggesting that color changes alone will cause sleepiness. Users should not use the tint while driving or in safety-critical contexts. Android permissions and store policies must be reviewed before distribution.
