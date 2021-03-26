package com.example.demo.entity

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.*

@Configuration
class StoreBackupBatchConfiguration{
    final val JOB_NAME = "storeBackupBatch"
    val STEP_NAME = JOB_NAME + "Step"

    @Autowired
    lateinit var entityManagerFactory: EntityManagerFactory

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    var chunkSize : Int = 1000

    @Bean
    @StepScope
    fun reader (@Value("#{jobParameters[address]}") address: String?) : JpaPagingItemReader<Store>{
        val parameters = LinkedHashMap<String, Any>()
        parameters.put("address", address + "%")

        val reader = JpaPagingItemReader<Store>()
        reader.setEntityManagerFactory(entityManagerFactory)
        reader.setQueryString("SELECT s FROM Store s JOIN FETCH s.employees e where s.address like :address")
        reader.setParameterValues(parameters)
        reader.pageSize = chunkSize

        return reader
    }

    fun processor() : ItemProcessor<Store, StoreHistory>{
        return ItemProcessor{ item ->
            StoreHistory(item) }
    }

    fun writer() : JpaItemWriter<StoreHistory>{
        val writer = JpaItemWriter<StoreHistory>()
        writer.setEntityManagerFactory(entityManagerFactory)

        return writer
    }

    @Bean
    @JobScope
    fun step() : Step{
        return stepBuilderFactory.get(STEP_NAME)
                .chunk<Store, StoreHistory>(chunkSize)
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
                .build()
    }

    @Bean
    fun job() : Job{
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build()
    }
}