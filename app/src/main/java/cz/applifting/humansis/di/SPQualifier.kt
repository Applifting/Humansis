package cz.applifting.humansis.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Qualifier
annotation class SPQualifier(
    val type: Type = Type.GENERIC
) {
    enum class Type(
        val spName: String
    ) {
        GENERIC("HumansisSP"),
        CRYPTO("crypto-sp")
    }
}