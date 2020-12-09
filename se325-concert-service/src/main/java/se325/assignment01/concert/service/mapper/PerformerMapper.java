package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

public class PerformerMapper {

    public static Performer toDomainModel(PerformerDTO performerDTO){
        return new Performer(
                performerDTO.getId(),
                performerDTO.getName(),
                performerDTO.getImageName(),
                performerDTO.getGenre(),
                performerDTO.getBlurb()
        );
    }

    public static PerformerDTO toDTO(Performer performer){
        return new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb()
        );
    }
}
