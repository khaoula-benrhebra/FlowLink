package org.supplychain.supplychain.repository.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByName(String name);

    Page<Customer> findAll(Pageable pageable);
}