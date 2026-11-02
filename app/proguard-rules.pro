# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Kakao SDK: 에러 enum 필드를 리플렉션(getField)으로 조회함
# https://developers.kakao.com/docs/ko/android/getting-started
-keep class com.kakao.sdk.**.model.* { <fields>; }

# Navigation 타입 안전 라우트는 enum 인자를 클래스 전체 이름과 상수 이름으로 찾는다
# 난독화로 이름이 바뀌면 NavHost를 만들 때(앱 시작 시) 크래시가 나므로 모든 모듈의 앱 enum 이름을 유지한다
-keepnames enum com.nexters.boolti.** { *; }
