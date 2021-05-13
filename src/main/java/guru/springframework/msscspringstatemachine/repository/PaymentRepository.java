package guru.springframework.msscspringstatemachine.repository;

import guru.springframework.msscspringstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
