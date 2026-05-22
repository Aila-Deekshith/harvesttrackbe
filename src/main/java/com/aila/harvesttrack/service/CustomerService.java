package com.aila.harvesttrack.service;

import com.aila.harvesttrack.model.Customer;

import java.util.List;

public interface CustomerService {

    // ── Get all customers of an owner
    List<Customer> getAllCustomers(Integer ownerId);

    // ── Get single customer by ID
    Customer getCustomerById(int id, Integer ownerId);

    // ── Add new customer
    Customer addCustomer(Customer customer, Integer ownerId);

    // ── Update existing customer
    Customer updateCustomer(int id, Customer customer, Integer ownerId);

    // ── Delete customer
    void deleteCustomer(int id, Integer ownerId);

    // ── Search customers
    List<Customer> searchCustomers(String keyword, Integer ownerId);
}