package com.yoavst.jeb.utils

import com.pnfsoftware.jeb.core.units.code.android.IDexDecompilerUnit
import com.pnfsoftware.jeb.core.units.code.android.IDexUnit
import com.pnfsoftware.jeb.core.units.code.android.dex.IDexField
import com.pnfsoftware.jeb.core.units.code.android.dex.IDexMethod
import com.pnfsoftware.jeb.core.units.code.java.IJavaField
import com.pnfsoftware.jeb.core.units.code.java.IJavaMethod
import com.pnfsoftware.jeb.core.util.DecompilerHelper
import com.pnfsoftware.jeb.util.logging.GlobalLog

private object DecompilerUtils

private val logger = GlobalLog.getLogger(DecompilerUtils::class.java)

val IDexUnit.decompiler: IDexDecompilerUnit get() = DecompilerHelper.getDecompiler(this) as IDexDecompilerUnit

fun IDexDecompilerUnit.decompileDexMethod(method: IDexMethod): IJavaMethod? {
    try {
        if (!decompileMethod(method.signature)) {
            logger.warning("Failed to decompile ${method.currentSignature}")
            return null
        }

        return getMethod(method.signature, false)
    } catch (e: StackOverflowError) {
        // Fix bug where JEB crashes with stackoverflow when trying to decompile
        logger.error("Encountered not decompileable method: ${method.currentSignature}")
        return null
    }
}

fun IDexDecompilerUnit.decompileDexField(field: IDexField): IJavaField? {
    if (!decompileField(field.signature)) {
        logger.warning("Failed to decompile ${field.currentSignature}")
        return null
    }

    return getField(field.signature, false)
}

fun IJavaMethod.toDexMethod(unit: IDexUnit): IDexMethod? {
    val result = unit.getMethod(signature)
    if (result == null)
        logger.error("Could not convert java method to dex method: $this")
    return result
}