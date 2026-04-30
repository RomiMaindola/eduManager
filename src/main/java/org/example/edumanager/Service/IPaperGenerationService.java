package org.example.edumanager.Service;

import org.example.edumanager.Dto.PaperGenerationRequest;

public interface IPaperGenerationService {


    String generatePaper(PaperGenerationRequest request);
}