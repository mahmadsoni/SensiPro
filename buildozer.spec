[app]
title = SensiPro
package.name = sensipro
package.domain = org.sensipro

source.dir = .
source.include_exts = py,png,jpg,kv,atlas

version = 1.0.0

requirements = python3,kivy==2.3.0,pyjnius

orientation = portrait
fullscreen = 0

android.permissions = MANAGE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE

android.api = 33
android.minapi = 24
android.ndk = 25b
android.accept_sdk_license = True
android.archs = arm64-v8a,armeabi-v7a

android.allow_backup = True

[buildozer]
log_level = 2
warn_on_root = 1
