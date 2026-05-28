# Preserve model classes for Firestore and other reflection-based deserialization
-keep class com.lhavanguane.tisimu.models.** { *; }

# If you use Room, it usually handles its own rules, but keeping entities is safe
-keep class com.lhavanguane.tisimu.entities.** { *; }

# Firebase Firestore rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.firebase.firestore.**
