package com.rodkrtz.foundationkit.valueobject.document.br.validation

import com.rodkrtz.foundationkit.valueobject.document.br.CNPJ
import com.rodkrtz.foundationkit.valueobject.document.br.CPF
import com.rodkrtz.foundationkit.valueobject.document.br.inline.BrDocUtil
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [CpfOrCnpj.CpfOrCnpjValidator::class])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
public annotation class CpfOrCnpj(

    val message: String = "{br.doc.cpf_cnpj.invalido}",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = [],

    val accept: Accept = Accept.CPF_OR_CNPJ,

    val allowNull: Boolean = true
) {

    public enum class Accept {
        CPF,
        CNPJ,
        CPF_OR_CNPJ
    }

    public class CpfOrCnpjValidator : ConstraintValidator<CpfOrCnpj, CharSequence?> {

        private lateinit var accept: Accept
        private var allowNull: Boolean = true

        override fun initialize(annotation: CpfOrCnpj) {
            accept = annotation.accept
            allowNull = annotation.allowNull
        }

        override fun isValid(value: CharSequence?, context: ConstraintValidatorContext): Boolean {

            if (value.isNullOrBlank()) return allowNull

            val clean = BrDocUtil.onlyDigits(value)

            return when (clean.length) {
                11 -> when (accept) {
                    Accept.CPF,
                    Accept.CPF_OR_CNPJ -> CPF.isValid(clean)
                    else -> false
                }

                14 -> when (accept) {
                    Accept.CNPJ,
                    Accept.CPF_OR_CNPJ -> CNPJ.isValid(clean)
                    else -> false
                }

                else -> false
            }
        }
    }
}