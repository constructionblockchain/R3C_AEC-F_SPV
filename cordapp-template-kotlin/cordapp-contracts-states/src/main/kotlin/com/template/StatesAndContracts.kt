package com.template

import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction

// *****************
// * Contract Code *
// *****************
class SPVContract : Contract {
    companion object {
        val ID = "com.template.SPVContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val spvCommand = tx.commands.requireSingleCommand<Commands>()
        when (spvCommand.value) {
            is Commands.Create -> {
                // TODO: Creation logic.
            }
            is Commands.Dissolve -> {
                // TODO: Dissolution logic.
            }
            is Commands.AddAgreement -> {
                // TODO: Add agreement logic.
            }
            is Commands.AddMember -> {
                // TODO: Add member logic.
            }
            else -> throw IllegalArgumentException()
        }
    }

    interface Commands : CommandData {
        class Create : Commands
        class Dissolve : Commands
        class AddAgreement : Commands
        class AddMember : Commands
    }
}

// *********
// * State *
// *********
data class SPVState(
        val owner: Party,
        val members: List<Party> = listOf(owner),
        val agreements: List<String> = listOf(),
        override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {
    override val participants = members
}
