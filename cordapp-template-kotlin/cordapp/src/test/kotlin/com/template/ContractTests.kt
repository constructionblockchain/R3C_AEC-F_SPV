package com.template

import net.corda.testing.node.MockServices
import net.corda.testing.node.makeTestIdentityService
import org.junit.Test

class ContractTests {
    private val ledgerServices = MockServices()

    @Test
    fun `dummy test`() {

    }
}
class FinanceContractTests {
    private val ledgerServices = MockServices(
            // A list of packages to scan for cordapps
            cordappPackages = listOf("net.corda.finance.contracts"),
            // The identity represented by this set of mock services. Defaults to a test identity.
            // You can also use the alternative parameter initialIdentityName which accepts a
            // [CordaX500Name]
            initialIdentity = megaCorp,
            // An implementation of IdentityService, which contains a list of all identities known
            // to the node. Use [makeTestIdentityService] which returns an implementation of
            // [InMemoryIdentityService] with the given identities
            identityService = makeTestIdentityService(megaCorp.identity)
    )

    fun emptyLedger() {
        ledger {
            
        }

    }
}