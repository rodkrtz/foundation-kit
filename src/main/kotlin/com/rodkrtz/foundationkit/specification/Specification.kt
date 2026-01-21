package com.rodkrtz.foundationkit.specification

/**
 * Specification pattern for encapsulating business rules.
 *
 * Specifications represent reusable business rules that can be combined
 * using logical operations (AND, OR, NOT). They are useful for:
 * - Expressing complex business rules in a declarative way
 * - Reusing rules across different parts of the application
 * - Composing rules to create more complex criteria
 * - Keeping validation logic separate from entities
 *
 * Example usage:
 * ```kotlin
 * // Define specifications
 * class ActiveUserSpec : Specification<User> {
 *     override fun isSatisfiedBy(user: User) = user.isActive && !user.isDeleted
 * }
 *
 * class PremiumUserSpec : Specification<User> {
 *     override fun isSatisfiedBy(user: User) = user.subscriptionType == SubscriptionType.PREMIUM
 * }
 *
 * class EmailVerifiedSpec : Specification<User> {
 *     override fun isSatisfiedBy(user: User) = user.emailVerified
 * }
 *
 * class MinimumAgeSpec(private val minAge: Int) : Specification<User> {
 *     override fun isSatisfiedBy(user: User) = user.age >= minAge
 * }
 *
 * // Compose specifications
 * val eligibleForDiscount = ActiveUserSpec()
 *     .and(PremiumUserSpec())
 *     .and(EmailVerifiedSpec())
 *
 * val adultPremiumUsers = PremiumUserSpec()
 *     .and(MinimumAgeSpec(18))
 *
 * val notPremiumSpec = PremiumUserSpec().not()
 *
 * val premiumOrVerified = PremiumUserSpec().or(EmailVerifiedSpec())
 *
 * // Use in filtering
 * val users = listOf(user1, user2, user3)
 * val eligibleUsers = users.filter { eligibleForDiscount.isSatisfiedBy(it) }
 *
 * // Use in repository
 * class UserRepository {
 *     fun findBySpecification(spec: Specification<User>): List<User> {
 *         return allUsers.filter { spec.isSatisfiedBy(it) }
 *     }
 * }
 *
 * val repo = UserRepository()
 * val premiumAdults = repo.findBySpecification(adultPremiumUsers)
 *
 * // Use in domain logic
 * class DiscountService {
 *     private val eligibilitySpec = ActiveUserSpec()
 *         .and(PremiumUserSpec())
 *         .and(EmailVerifiedSpec())
 *
 *     fun canReceiveDiscount(user: User): Boolean {
 *         return eligibilitySpec.isSatisfiedBy(user)
 *     }
 * }
 * ```
 *
 * @param T The type of object to evaluate against this specification
 */
interface Specification<T> {
    /**
     * Checks if a candidate object satisfies this specification.
     *
     * @param candidate The object to evaluate
     * @return true if the candidate satisfies this specification
     */
    fun isSatisfiedBy(candidate: T): Boolean

    /**
     * Combines this specification with another using logical AND.
     *
     * The resulting specification is satisfied only if both specifications are satisfied.
     *
     * @param other The specification to combine with
     * @return A new specification representing AND operation
     */
    fun and(other: Specification<T>): Specification<T> =
        AndSpecification(this, other)

    /**
     * Combines this specification with another using logical OR.
     *
     * The resulting specification is satisfied if either specification is satisfied.
     *
     * @param other The specification to combine with
     * @return A new specification representing OR operation
     */
    fun or(other: Specification<T>): Specification<T> =
        OrSpecification(this, other)

    /**
     * Negates this specification using logical NOT.
     *
     * The resulting specification is satisfied when this specification is not satisfied.
     *
     * @return A new specification representing NOT operation
     */
    fun not(): Specification<T> =
        NotSpecification(this)
}

/**
 * Specification that combines two specifications with logical AND.
 */
internal class AndSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate)
}

/**
 * Specification that combines two specifications with logical OR.
 */
internal class OrSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate)
}

/**
 * Specification that negates another specification with logical NOT.
 */
internal class NotSpecification<T>(
    private val spec: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        !spec.isSatisfiedBy(candidate)
}
