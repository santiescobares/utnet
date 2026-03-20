package ar.net.ut.backend.studyrecord.event;

import ar.net.ut.backend.studyrecord.StudyRecord;

public class StudyRecordCreateEvent extends StudyRecordEvent {

    public StudyRecordCreateEvent(StudyRecord studyRecord) {
        super(studyRecord);
    }
}
