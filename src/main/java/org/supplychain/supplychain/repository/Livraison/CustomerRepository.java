package org.supplychain.supplychain.repository.Livraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.Customer;
import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Recherche multi-champs (name, address, city)
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer> searchByKeyword(@Param("search") String search, Pageable pageable);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByName(String name);
    Page<Customer> findAll(Pageable pageable);
}