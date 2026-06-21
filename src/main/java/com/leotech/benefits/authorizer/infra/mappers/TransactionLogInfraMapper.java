package com.leotech.benefits.authorizer.infra.mappers;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionLogInfraMapper {

    TransactionEvent toDomain(TransactionLogEntity entity);
}
