package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.repositories.IObservationRepository;
import org.eyeseetea.malariacare.domain.entity.Observation;
import org.eyeseetea.malariacare.domain.entity.ObservationStatus;

import java.util.Arrays;
import java.util.List;

public class GetSentObservationsUseCase {
    private IObservationRepository observationRepository;

    public GetSentObservationsUseCase(IObservationRepository observationRepository){
        this.observationRepository = observationRepository;
    }

    public List<Observation> execute() throws Exception {
        return observationRepository.getObservationsByStatus(Arrays.asList(
                ObservationStatus.COMPLETED,
                ObservationStatus.SENT,
                ObservationStatus.SENDING));
    }
}
