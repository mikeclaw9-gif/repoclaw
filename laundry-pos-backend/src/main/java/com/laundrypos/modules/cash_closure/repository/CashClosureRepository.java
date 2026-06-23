package com.laundrypos.modules.cash_closure.repository;

import com.laundrypos.modules.cash_closure.model.CashClosure;
import com.laundrypos.modules.cash_closure.model.CashClosure.CashClosureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CashClosureRepository extends JpaRepository<CashClosure, Long> {
    List<CashClosure> findByStatus(CashClosureStatus status);
    List<CashClosure> findByOpenedBy(String openedBy);
}
