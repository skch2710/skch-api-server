package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.PaymentHistory;

public interface PaymentHistoryDAO extends JpaRepository<PaymentHistory, Long> {

	PaymentHistory findByPaymentId(Long paymentId);

}
