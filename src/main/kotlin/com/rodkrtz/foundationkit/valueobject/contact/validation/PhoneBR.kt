package com.rodkrtz.foundationkit.valueobject.contact.validation

import com.rodkrtz.foundationkit.valueobject.contact.Phone
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PhoneBR.PhoneBRValidator::class])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
public annotation class PhoneBR(
    val message: String = "{phone.br.invalid}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val allowNull: Boolean = true
) {
    public class PhoneBRValidator : ConstraintValidator<PhoneBR, CharSequence?> {

        private var allowNull: Boolean = true

        override fun initialize(annotation: PhoneBR) {
            allowNull = annotation.allowNull
        }

        override fun isValid(value: CharSequence?, context: ConstraintValidatorContext): Boolean {
            if (value.isNullOrBlank()) return allowNull
            return Phone.isValid(value)
        }
    }
}


