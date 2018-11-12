package com.template

import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.driver
import net.corda.testing.node.User

fun main(args: Array<String>) {
    val user = User("user1", "test", permissions = setOf("ALL"))
    driver(DriverParameters(startNodesInProcess = true, waitForAllNodesToFinish = true)) {
        val (partyA, partyB) = listOf(
                startNode(providedName = CordaX500Name("PartyA", "London", "GB"), rpcUsers = listOf(user)),
                startNode(providedName = CordaX500Name("PartyB", "New York", "US"), rpcUsers = listOf(user)),
                startNode(providedName = CordaX500Name("PartyC", "London", "GB"), rpcUsers = listOf(user)),
                startNode(providedName = CordaX500Name("Lawyer", "New York", "US"), rpcUsers = listOf(user))).map { it.getOrThrow() }
    }
}
