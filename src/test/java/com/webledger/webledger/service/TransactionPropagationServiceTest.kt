package com.webledger.webledger.service;

import io.mockk.impl.annotations.InjectMockKs;
import io.mockk.junit5.MockKExtension;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockKExtension::class)
internal class TransactionPropagationServiceTest {
    @InjectMockKs
    lateinit var transactionPropagationService: TransactionPropagationService

    @Test
    fun `updateAllocationCenters - `() {

    }
}