package ar.net.ut.backend.studyrecord.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.studyrecord.StudyRecord;

public class StudyRecordDeleteEvent extends StudyRecordEvent {

    public StudyRecordDeleteEvent(StudyRecord studyRecord) {
        super(studyRecord, Log.Action.DELETE);
    }
}
