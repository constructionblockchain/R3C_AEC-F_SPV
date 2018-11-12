package net.corda.server.controllers

import com.template.*
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.vaultQueryBy
import net.corda.server.NodeRPCConnection
import org.bouncycastle.asn1.x500.style.RFC4519Style.member
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.ws.rs.QueryParam

/**
 * Define CorDapp-specific endpoints in a controller such as this.
 */
@RestController
@RequestMapping("/custom") // The paths for GET and POST requests are relative to this base path.
class CustomController(private val rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/createspv", produces = arrayOf("text/plain"))
    private fun createSPV(): String {
        proxy.startFlowDynamic(CreateSPVFlow::class.java).returnValue.get()
        return "Success"
    }

    @GetMapping(value = "/spvs", produces = arrayOf("text/plain"))
    private fun spvs(): String {
        return proxy.vaultQueryBy<SPVState>().states.map {
            it.toString()
        }.joinToString("\r\n")
    }

    @GetMapping(value = "/addmember", produces = arrayOf("text/plain"))
    private fun addMember(@RequestParam("spvid") spvIdString: String, @RequestParam("member") memberString: String): String {
        val spvId = UniqueIdentifier.fromString(spvIdString)
        val member = proxy.partiesFromName(memberString, false).single()
        proxy.startFlowDynamic(AddMemberFlow::class.java, spvId, member).returnValue.get()
        return "Success"
    }

    @GetMapping(value = "/addagreement", produces = arrayOf("text/plain"))
    private fun addAgreement(@QueryParam("spvid") spvIdString: String, @QueryParam("agreement") agreementString: String): String {
        val spvId = UniqueIdentifier.fromString(spvIdString)
        val agreement = agreementString // TODO: change agreement type, not string
        proxy.startFlowDynamic(AddAgreementFlow::class.java, spvId, agreement).returnValue.get()
        return "Success"
    }

    @GetMapping(value = "/dissolvespv", produces = arrayOf("text/plain"))
    private fun dissolveSPV(@QueryParam("spvid") spvIdString: String): String {
        val spvId = UniqueIdentifier.fromString(spvIdString)
        proxy.startFlowDynamic(DissolveSPVFlow::class.java, spvId).returnValue.get()
        return "Success"
    }
}