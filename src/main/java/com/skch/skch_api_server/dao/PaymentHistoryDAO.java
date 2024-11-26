package com.skch.skch_api_server.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.PaymentHistory;

public interface PaymentHistoryDAO extends JpaRepository<PaymentHistory, Long> {

	PaymentHistory findByPaymentId(Long paymentId);

}
