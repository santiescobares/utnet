package ar.net.ut.backend.studyrecord.event;

import ar.net.ut.backend.studyrecord.StudyRecord;

public class StudyRecordUpdateEvent extends StudyRecordEvent {

    public StudyRecordUpdateEvent(StudyRecord studyRecord) {
        super(studyRecord);
    }
}
