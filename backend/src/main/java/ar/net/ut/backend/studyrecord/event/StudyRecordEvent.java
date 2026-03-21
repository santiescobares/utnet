package ar.net.ut.backend.studyrecord.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.studyrecord.StudyRecord;
import lombok.Getter;

@Getter
public abstract class StudyRecordEvent extends LoggableEvent<StudyRecord> {

    private final StudyRecord studyRecord;

    public StudyRecordEvent(StudyRecord studyRecord, Log.Action action) {
        super(studyRecord.getCreatedBy(), ResourceType.STUDY_RECORD, studyRecord.getId().toString(), action);
        this.studyRecord = studyRecord;
    }

    @Override
    public StudyRecord getEntity() { return studyRecord; }
}
