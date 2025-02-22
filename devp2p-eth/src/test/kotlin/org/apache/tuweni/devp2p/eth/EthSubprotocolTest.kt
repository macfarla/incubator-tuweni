// Copyright The Tuweni Authors
// SPDX-License-Identifier: Apache-2.0
package org.apache.tuweni.devp2p.eth

import kotlinx.coroutines.runBlocking
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.devp2p.eth.EthHelloSubprotocol.Companion.ETH63
import org.apache.tuweni.devp2p.eth.EthHelloSubprotocol.Companion.ETH64
import org.apache.tuweni.devp2p.eth.EthSubprotocol.Companion.ETH62
import org.apache.tuweni.devp2p.eth.EthSubprotocol.Companion.ETH65
import org.apache.tuweni.devp2p.eth.EthSubprotocol.Companion.ETH66
import org.apache.tuweni.eth.Hash
import org.apache.tuweni.eth.repository.BlockchainRepository
import org.apache.tuweni.eth.repository.MemoryTransactionPool
import org.apache.tuweni.genesis.Genesis
import org.apache.tuweni.junit.BouncyCastleExtension
import org.apache.tuweni.junit.VertxExtension
import org.apache.tuweni.rlpx.wire.SubProtocolIdentifier
import org.apache.tuweni.units.bigints.UInt256
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BouncyCastleExtension::class, VertxExtension::class)
class EthSubprotocolTest {

  val blockchainInfo = SimpleBlockchainInformation(
    UInt256.ONE,
    UInt256.ONE,
    Hash.fromBytes(Bytes32.random()),
    UInt256.valueOf(42L),
    Hash.fromBytes(Bytes32.random()),
    emptyList(),
  )

  @Test
  fun testVersion() = runBlocking {
    val repository = BlockchainRepository.inMemory(Genesis.dev())
    val eth = EthSubprotocol(
      blockchainInfo = blockchainInfo,
      repository = repository,
      pendingTransactionsPool = MemoryTransactionPool(),
    )
    assertEquals(SubProtocolIdentifier.of("eth", 66), eth.id())
  }

  @Test
  fun testSupports() = runBlocking {
    val repository = BlockchainRepository.inMemory(Genesis.dev())
    val eth = EthSubprotocol(
      blockchainInfo = blockchainInfo,
      repository = repository,
      pendingTransactionsPool = MemoryTransactionPool(),
    )
    assertTrue(eth.supports(SubProtocolIdentifier.of("eth", 66)))
    assertTrue(eth.supports(SubProtocolIdentifier.of("eth", 65)))
    assertTrue(eth.supports(SubProtocolIdentifier.of("eth", 64)))
    assertTrue(eth.supports(SubProtocolIdentifier.of("eth", 63)))
    assertTrue(eth.supports(SubProtocolIdentifier.of("eth", 62)))
    assertFalse(eth.supports(SubProtocolIdentifier.of("eth2", 64)))
    assertFalse(eth.supports(SubProtocolIdentifier.of("eth", 34)))
  }

  @Test
  fun rangeCheck() {
    assertEquals(8, ETH62.versionRange())
    assertEquals(17, ETH63.versionRange())
    assertEquals(17, ETH64.versionRange())
    assertEquals(17, ETH65.versionRange())
    assertEquals(17, ETH66.versionRange())
  }
}
