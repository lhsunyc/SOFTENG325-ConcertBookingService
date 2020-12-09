package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;

import java.util.List;
import java.util.stream.Collectors;

public class ConcertMapper {

//    public static Concert toDomainModel(ConcertDTO concertDTO) {
//        return new Concert(
//                concertDTO.getId(),
//                concertDTO.getTitle(),
//                concertDTO.getImageName(),
//                concertDTO.getBlurb()
//        );
//    }

    public static ConcertDTO toDTO(Concert concert) {
        ConcertDTO concertDTO = new ConcertDTO(concert.getId(), concert.getTitle(), concert.getImageName(), concert.getBlurb());
        concertDTO.setDates(List.copyOf(concert.getDates()));
        concertDTO.setPerformers(concert.getPerformers().stream().map(PerformerMapper::toDTO).collect(Collectors.toList()));
        return concertDTO;
    }

    public static ConcertSummaryDTO toSummaryDTO(Concert concert){
        return new ConcertSummaryDTO(concert.getId(), concert.getTitle(), concert.getImageName());
    }

}
