package com.template

import net.corda.core.contracts.*
import net.corda.core.crypto.NullKeys
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.identity.Party
import net.corda.finance.utils.sumCashBy
import java.time.Instant
import java.util.*

// *****************
// * Contract Code *
// *****************


class TemplateContract : Contract {
    // This is used to identify our contract when building a transaction
    companion object {
        val ID = "com.template.TemplateContract"
    }
    // A transaction is considered valid if the verify() function of the contract of each of the transaction's input
    // and output states does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Action : Commands
    }
}

class FinanceContract : Contract {

    interface Commands : CommandData {
        class Move : TypeOnlyCommandData(), Commands
        class Redeem : TypeOnlyCommandData(), Commands
        class Issue : TypeOnlyCommandData(), Commands
    }

    companion object {
        val ID = "com.template.FinanceContract"
    }

    override fun verify(tx : LedgerTransaction) {
        val groups = tx.groupStates(State::withoutOwner)

        val command = tx.commands.requireSingleCommand<FinanceContract.Commands>()


        //verify logic goes here! Whole thing needs comments!
        val timeWindow : TimeWindow? = tx.timeWindow

        for ((inputs,outputs,_) in groups)
        {
            when (command.value)
            {
                is Commands.Move -> {
                    val input = inputs.single()
                    requireThat{
                        "the transaction is signed by the owner of the CP" using (input.owner.owningKey in command.signers)
                        "the state is propagated" using (outputs.size == 1)
                        // Don't need to check anything else, as if outputs.size == 1 then the output is equal to
                        // the input ignoring the owner field due to the grouping.
                    }
                }

                is Commands.Redeem -> {
                    // Redemption of the paper requires movement of on-ledger cash.
                    val input = inputs.single()
                    val received = tx.outputs.map { it.data }.sumCashBy(input.owner)
                    val time = timeWindow?.fromTime ?: throw IllegalArgumentException("Redemptions must be timestamped")
                    requireThat {
                        "the paper must have matured" using (time >= input.maturityDate)
                        "the received amount equals the face value" using (received == input.faceValue)
                        "the paper must be destroyed" using outputs.isEmpty()
                        "the transaction is signed by the owner of the CP" using (input.owner.owningKey in command.signers)
                    }
                }

                is Commands.Issue -> {
                    val output = outputs.single()
                    val time = timeWindow?.untilTime ?: throw IllegalArgumentException("Issuances must be timestamped")
                    requireThat {
                        // Don't allow people to issue commercial paper under other entities identities.
                        "output states are issued by a command signer" using (output.issuance.party.owningKey in command.signers)
                        "output values sum to more than the inputs" using (output.faceValue.quantity > 0)
                        "the maturity date is not in the past" using (time < output.maturityDate)
                        // Don't allow an existing CP state to be replaced by this issuance.
                        "can't reissue an existing state" using inputs.isEmpty()
                    }
                }

                else -> throw IllegalArgumentException("Unrecognised command")
            }
        }
    }
}

// *********
// * State *
// *********


data class TemplateState(val data: String) : ContractState {
    override val participants: List<AbstractParty> = listOf()
}

// *****************
// * Contract State*
// *****************





data class State (
        val issuance: PartyAndReference,
        override val owner: AbstractParty,
        val faceValue: Amount<Issued<Currency>>, //what is this?
        val maturityDate : Instant
) : OwnableState {
    override val participants = listOf(owner)

    fun withoutOwner() = copy(owner = AnonymousParty(NullKeys.NullPublicKey))
    override fun withNewOwner(newOwner : AbstractParty) = CommandAndState(FinanceContract.Commands.Move(), copy(owner = newOwner))
}





// *******
// * IOU *
// *******

data class IOUState(val value: Int,
                    val lender: Party,
                    val borrower: Party) : ContractState {
    override val participants get() = listOf(lender, borrower)
}


