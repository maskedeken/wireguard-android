# Android GUI for [WireGuard](https://www.wireguard.com/)

**[Download from the Play Store](https://play.google.com/store/apps/details?id=com.wireguard.android)**

This is an Android GUI for [WireGuard](https://www.wireguard.com/). It [opportunistically uses the kernel implementation](https://git.zx2c4.com/android_kernel_wireguard/about/), and falls back to using the non-root [userspace implementation](https://git.zx2c4.com/wireguard-go/about/).

## Building

```
$ git clone --recurse-submodules https://github.com/zaneschepke/wireguard-android.git
$ cd wireguard-android
$ ./gradlew assembleRelease
```

macOS users may need [flock(1)](https://github.com/discoteq/flock).

## Embedding

The tunnel library is [on Maven Central](https://search.maven.org/artifact/com.wireguard.android/tunnel), alongside [extensive class library documentation](https://javadoc.io/doc/com.wireguard.android/tunnel).

```
implementation 'com.wireguard.android:tunnel:$wireguardTunnelVersion'
```

The library makes use of Java 8 features, so be sure to support those in your gradle configuration with [desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring):

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
    coreLibraryDesugaringEnabled = true
}
dependencies {
    coreLibraryDesugaring "com.android.tools:desugar_jdk_libs:2.0.3"
}
```

## Publishing

1. Update tunnel's build.gradle.kts to the publishing repository with credentials.
2. Install gpg
```
brew install gpg
git config --global gpg.program $(which gpg)
```
3. Create gpg key
```
gpg --full-generate-key2
```
4. Add key information to ~/.gradle/gradle.properties
```
signing.gnupg.keyName=name
signing.gnupg.passphrase=********
signing.gnupg.executable=gpg
signing.secretKeyRingFile=/path/to/.gnupg/secring.gpg
```
5. Add username and token for repository to local.properties
```
GITHUB_USER=zaneschepke
GITHUB_TOKEN=***
```
6. Run publish
```
./gradlew publish
```

## Translating

Please help us translate the app into several languages on [our translation platform](https://crowdin.com/project/WireGuard).
