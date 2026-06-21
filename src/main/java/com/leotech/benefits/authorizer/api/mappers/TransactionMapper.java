package com.leotech.benefits.authorizer.api.mappers;

import com.leotech.benefits.authorizer.api.requests.CreateTransactionRequest;
import com.leotech.benefits.authorizer.api.responses.TransactionLogResponse;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {

    Transaction toDomain(CreateTransactionRequest request);

    TransactionLogResponse toResponse(TransactionEvent event);
}
