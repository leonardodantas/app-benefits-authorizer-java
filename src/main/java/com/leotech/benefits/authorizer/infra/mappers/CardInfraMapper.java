package com.leotech.benefits.authorizer.infra.mappers;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardInfraMapper {

    CardEntity toEntity(Card card);

    Card toDomain(CardEntity entity);
}
