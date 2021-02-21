package com.example.demo.batch.config

import com.example.demo.DemoApplication
import com.example.demo.entity.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
class TestBatchConfig

@SpringBatchTest
@SpringBootTest(classes=[DemoApplication::class, TestBatchConfig::class])
@ActiveProfiles("sandbox")
internal class NplusOneBatchTest{
    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var storeHistoryRepository: StoreHistoryRepository

    //@BeforeEach
    internal fun setUp() {
        println("start : given")
        //given
        val store1 = Store("s1", "Seoul a1")
        store1.addProduct(Product("p1", 1000L))
        store1.addProduct(Product("p2", 2000L))
        store1.addEmployee(Employee("e1", LocalDateTime.now()))
        storeRepository.save(store1)
        productRepository.saveAll(store1.products)
        employeeRepository.saveAll(store1.employees)

        val store2 = Store("s2", "Newyork a2")
        store2.addProduct(Product("p3", 3000L))
        store2.addProduct(Product("p4", 4000L))
        store2.addEmployee(Employee("e2", LocalDateTime.now()))
        storeRepository.save(store2)
        productRepository.saveAll(store2.products)
        employeeRepository.saveAll(store2.employees)

        val store3 = Store("s3", "Seoul a3")
        store3.addProduct(Product("p5", 1000L))
        store3.addProduct(Product("p6", 2000L))
        store3.addEmployee(Employee("e3", LocalDateTime.now()))
        storeRepository.save(store3)
        productRepository.saveAll(store3.products)
        employeeRepository.saveAll(store3.employees)

        println("end : given")
    }

    @Test
    fun store(){
        val jobParameters = JobParametersBuilder()
                .addString("requestDate", LocalDateTime.now().toString())
                .addString("address", "Seoul")
                .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        val result = storeHistoryRepository.findAll()
        println(result.size)

        assertEquals(BatchStatus.COMPLETED, jobExecution.status)
    }
}