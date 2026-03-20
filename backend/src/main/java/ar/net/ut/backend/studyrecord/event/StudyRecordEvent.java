package ar.net.ut.backend.studyrecord.event;

import ar.net.ut.backend.studyrecord.StudyRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class StudyRecordEvent {

    private final StudyRecord studyRecord;
}
