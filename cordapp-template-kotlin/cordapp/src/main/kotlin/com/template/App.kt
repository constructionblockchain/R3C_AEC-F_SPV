package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class CreateSPVFlow : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val txBuilder = TransactionBuilder(serviceHub.networkMapCache.notaryIdentities[0])
                .addOutputState(SPVState(ourIdentity), SPVContract.ID)
                .addCommand(SPVContract.Commands.Create(), ourIdentity.owningKey)
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        subFlow(FinalityFlow(signedTx))
    }
}

@InitiatingFlow
@StartableByRPC
class AddAgreementFlow(val linearId: UniqueIdentifier, val agreement: String) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val input = serviceHub.vaultService.queryBy<SPVState>(queryCriteria).states[0]
        val inputContractState = input.state.data

        val txBuilder = TransactionBuilder(input.state.notary)
                .addInputState(input)
                .addOutputState(inputContractState.copy(agreements = inputContractState.agreements + agreement), input.state.contract)
                .addCommand(SPVContract.Commands.AddAgreement(), ourIdentity.owningKey)
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        subFlow(FinalityFlow(signedTx))
    }
}

@InitiatingFlow
@StartableByRPC
class AddMemberFlow(val linearId: UniqueIdentifier, val member: Party) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val input = serviceHub.vaultService.queryBy<SPVState>(queryCriteria).states[0]
        val inputContractState = input.state.data

        val txBuilder = TransactionBuilder(input.state.notary)
                .addInputState(input)
                .addOutputState(inputContractState.copy(members = inputContractState.members + member), input.state.contract)
                .addCommand(SPVContract.Commands.AddMember(), ourIdentity.owningKey)
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        subFlow(FinalityFlow(signedTx))
    }
}

@InitiatingFlow
@StartableByRPC
class DissolveSPVFlow(val linearId: UniqueIdentifier) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val input = serviceHub.vaultService.queryBy<SPVState>(queryCriteria).states[0]

        val txBuilder = TransactionBuilder(input.state.notary)
                .addInputState(input)
                .addCommand(SPVContract.Commands.AddMember(), ourIdentity.owningKey)
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        subFlow(FinalityFlow(signedTx))
    }
}
