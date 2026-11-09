package org.example.repository;

import org.example.domain.model.Customer;
import org.example.domain.vo.Cpf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByCpf(Cpf cpf);
}
