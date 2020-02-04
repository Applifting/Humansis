package cz.applifting.humansis.model

data class PersonCount(
    val age: AgeCategory,
    val gender: Gender,
    val count: Int
)

enum class AgeCategory {
    AGE_0_TO_2,
    AGE_2_TO_5,
    AGE_5_TO_17,
    AGE_18_TO_64,
    AGE_65_MORE
}

enum class Gender {
    MALE,
    FEMALE
}
