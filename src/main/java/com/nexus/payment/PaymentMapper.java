package com.nexus.payment;

import com.nexus.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    private final UserMapper userMapper;

    public PaymentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BasicPaymentResponse toBasicPaymentResponse(Payment payment) {
        return new BasicPaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                userMapper.toUserInfo(payment.getPayer())
        );
    }
}
