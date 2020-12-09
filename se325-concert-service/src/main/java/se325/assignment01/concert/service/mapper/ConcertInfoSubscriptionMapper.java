package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.service.domain.ConcertInfoSubscription;

public class ConcertInfoSubscriptionMapper {

    public static ConcertInfoSubscription toDomainModel(ConcertInfoSubscriptionDTO concertInfoSubscriptionDTO) {
        return new ConcertInfoSubscription(concertInfoSubscriptionDTO.getConcertId(),
                concertInfoSubscriptionDTO.getDate(),
                concertInfoSubscriptionDTO.getPercentageBooked()
        );
    }

    public static ConcertInfoSubscriptionDTO toDTO(ConcertInfoSubscription concertInfoSubscription) {
        return new ConcertInfoSubscriptionDTO(concertInfoSubscription.getConcertId(),
                concertInfoSubscription.getDate(),
                concertInfoSubscription.getPercentageBooked()
        );
    }

}
