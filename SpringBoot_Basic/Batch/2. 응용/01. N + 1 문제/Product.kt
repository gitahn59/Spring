package com.example.demo.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Product(
        val name : String,
        val price : Long,
        @ManyToOne(cascade = [CascadeType.PERSIST])
        var store : Store? = null,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long = 0,
)

@Entity
class Store(
        val name : String,
        val address : String,
        @OneToMany(mappedBy = "store", cascade = [CascadeType.PERSIST])
        val products : MutableList<Product> = arrayListOf(),
        @OneToMany(mappedBy = "store", cascade = [CascadeType.PERSIST])
        val employees : MutableList<Employee> = arrayListOf(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long = 0,
){
    fun addProduct(product : Product){
        product.store = this
        products.add(product)
    }

    fun addEmployee(employee : Employee){
        employee.store = this
        employees.add(employee)
    }
}

@Entity
class Employee(
        val name : String,
        val hireDate : LocalDateTime,
        @ManyToOne()
        var store: Store? = null,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long = 0,
)

@Entity
class StoreHistory(
        val storeName : String,
        val productNames : String,
        val employeeNames : String,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long = 0,
){
    constructor(store:Store) :
            this(store.name,
                    store.products.map(Product::name).joinToString(),
                    store.employees.map(Employee::name).joinToString())
}

@Repository
interface ProductRepository : JpaRepository<Product, Long>

@Repository
interface StoreRepository : JpaRepository<Store, Long>

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long>

@Repository
interface StoreHistoryRepository : JpaRepository<StoreHistory, Long>
