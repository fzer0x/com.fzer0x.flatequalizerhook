package com.fzer0x.flatequalizerhook

import android.app.Activity
import android.content.Intent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class EqualizerHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.fzer0x.flatequalizerhook" -> {
                try {
                    XposedHelpers.findAndHookMethod(
                        "com.fzer0x.flatequalizerhook.MainActivity",
                        lpparam.classLoader,
                        "isModuleActive",
                        XC_MethodReplacement.returnConstant(true)
                    )
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
            }
            TARGET_PACKAGE -> {
                hookAdSdks(lpparam)
                hookActivities()
                hookPremiumFlags(lpparam)
                hookBypass()
            }
        }
    }

    private fun isPremiumKey(key: String): Boolean {
        return key.lowercase().contains("purchase")
    }

    private fun hookAdSdks(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findClassIfExists("com.google.android.gms.ads.MobileAds", lpparam.classLoader)?.let {
                XposedBridge.hookAllMethods(it, "initialize", XC_MethodReplacement.DO_NOTHING)
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

    private fun hookActivities() {
        try {
            val relaunchClassName = "com.zipoapps.premiumhelper.ui.relaunch.RelaunchPremiumActivity"
            XposedBridge.hookAllMethods(Activity::class.java, "startActivity", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val intent = param.args.firstOrNull { it is Intent } as? Intent
                    if (intent?.component?.className == relaunchClassName) {
                        param.result = null
                    }
                }
            })
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

    private fun hookPremiumFlags(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findClassIfExists("com.jazibkhan.equalizer.utils.PremiumUtils", lpparam.classLoader)?.let {
                XposedBridge.hookAllMethods(it, "isPremium", XC_MethodReplacement.returnConstant(true))
                XposedBridge.hookAllMethods(it, "hasPro", XC_MethodReplacement.returnConstant(true))
                return // Early return if successful
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }

        try {
            XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", lpparam.classLoader)?.let {
                XposedBridge.hookAllMethods(it, "getBoolean", object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        (param.args.getOrNull(0) as? String)?.takeIf { key -> isPremiumKey(key) }?.let {
                            param.result = true
                        }
                    }
                })

                XposedBridge.hookAllMethods(it, "getInt", object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        (param.args.getOrNull(0) as? String)?.takeIf { key -> isPremiumKey(key) }?.let {
                            param.result = 1
                        }
                    }
                })
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

    private fun hookBypass() {
        try {
            XposedHelpers.findAndHookMethod(java.io.File::class.java, "exists", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val path = (param.thisObject as java.io.File).absolutePath
                    if (path.contains("su") || path.contains("magisk")) {
                        param.result = false
                    }
                }
            })
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }

        try {
            XposedHelpers.setStaticObjectField(android.os.Build::class.java, "TAGS", "release-keys")
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

    companion object {
        private const val TARGET_PACKAGE = "com.jazibkhan.equalizer"
    }
}
