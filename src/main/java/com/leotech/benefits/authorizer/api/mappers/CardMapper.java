package com.leotech.benefits.authorizer.api.mappers;

import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import com.leotech.benefits.authorizer.domain.card.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {

    @Mapping(target = "balance", ignore = true)
    Card toDomain(CreateCardRequest request);

    CreateCardResponse toResponse(Card card);
}
