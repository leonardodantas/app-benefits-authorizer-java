package com.leotech.benefits.authorizer.api.mappers;

import com.leotech.benefits.authorizer.api.requests.CreateTransactionRequest;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {

    Transaction toDomain(CreateTransactionRequest request);
}
