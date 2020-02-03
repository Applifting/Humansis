package cz.applifting.humansis.di

import javax.inject.Qualifier

const val SP_GENERIC_NAME = "HumansisSP"

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Qualifier
annotation class SPQualifier(
    val type: Type = Type.GENERIC
) {
    enum class Type(
        val spName: String
    ) {
        GENERIC(SP_GENERIC_NAME),
        CRYPTO("crypto-sp")
    }
}