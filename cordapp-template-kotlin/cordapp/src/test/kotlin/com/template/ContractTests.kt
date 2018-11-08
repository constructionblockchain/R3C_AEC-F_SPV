package com.template

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractClassName
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.finance.contracts.ICommercialPaperState
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import net.corda.testing.node.makeTestIdentityService
import org.junit.Test

interface ICommercialPaperTestTemplate {
    fun getPaper(): ICommercialPaperState
    fun getIssueCommand(notary: Party): CommandData
    fun getRedeemCommand(notary: Party): CommandData
    fun getMoveCommand(): CommandData
    fun getContract(): ContractClassName
}

class ContractTests {
    private val ledgerServices = MockServices()

    @Test
    fun `dummy test`() {

    }
}
class FinanceContractTests {

    private companion object {

        val megaCorp = TestIdentity(CordaX500Name("MegaCorp", "London", "GB"))
//        private val ledgerServices = MockServices(listOf("net.corda.finance.schemas"), megaCorp, miniCorp)


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

        private val dummyNotary = TestIdentity(DUMMY_NOTARY_NAME, 20)


    }

    @Test
    fun emptyLedger() {
        ledgerServices.ledger {
            
        }

    }

    @Test
    fun simpleFCDoesntCompile() {
        val inState = getPaper()
        ledgerServices.ledger(dummyNotary.party) {
            transaction {

            }

        }
    }
}