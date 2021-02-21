package com.example.demo.batch.config

import com.example.demo.DemoApplication
import com.example.demo.entity.*
import org.junit.jupiter.api.Test
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBatchTest
@SpringBootTest(classes=[DemoApplication::class])
@ActiveProfiles("sandbox")
internal class RepositoryTestTest {
    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var storeHistoryRepository: StoreHistoryRepository

    @Test
    @Transactional(readOnly = true)
    internal fun find() {
        val stores = storeRepository.findAll()

        val sum = stores.flatMap(Store::products).map(Product::price).sum()
        val names = stores.flatMap(Store::employees).map(Employee::name).toString()

        println(sum)
        println(names)
    }
}