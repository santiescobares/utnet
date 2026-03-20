package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.subject.event.SubjectDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyRecordListener {

    private final StudyRecordRepository studyRecordRepository;

    @EventListener
    public void unlinkStudyRecordsOnSubjectDeletion(SubjectDeleteEvent event) {
        studyRecordRepository.unlinkStudyRecordsFromSubject(event.getSubject().getId());
    }
}
