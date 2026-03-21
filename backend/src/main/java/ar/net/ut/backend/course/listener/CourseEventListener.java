package ar.net.ut.backend.course.listener;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceCreateEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceDeleteEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceUpdateEvent;
import ar.net.ut.backend.course.service.CourseEventService;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.report.Report;
import ar.net.ut.backend.report.event.ReportAcceptedEvent;
import ar.net.ut.backend.user.service.UserContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static ar.net.ut.backend.Global.Contributions.*;

@Component
@RequiredArgsConstructor
public class CourseEventListener {

    private final CourseEventService courseEventService;
    private final UserContributionService userContributionService;

    @EventListener
    public void onCourseEventResourceCreate(CourseEventResourceCreateEvent event) {
        if (!courseEventService.isOnContributionCooldown(RequestContextHolder.getCurrentSession().userId())) {
            createEventResourceContribution(event);
        }
    }

    @EventListener
    public void onCourseEventResourceUpdate(CourseEventResourceUpdateEvent event) {
        if (!courseEventService.isOnContributionCooldown(RequestContextHolder.getCurrentSession().userId())) {
            createEventResourceContribution(event);
        }
    }

    @EventListener
    public void onCourseEventResourceDelete(CourseEventResourceDeleteEvent event) {
        // TODO retrieve users that created/edit this course event and subtract received contribution points
    }

    @EventListener
    public void deleteReportedCourseEvent(ReportAcceptedEvent event) {
        Report report = event.getReport();
        if (report.getResourceType() == ResourceType.COURSE_EVENT) {
            courseEventService.deleteCourseEvent(Long.valueOf(report.getResourceId()));
        }
    }

    private void createEventResourceContribution(CourseEventResourceEvent event) {
        CourseEvent courseEvent = event.getCourseEvent();
        userContributionService.createContribution(
                courseEvent.getCreatedBy().getId(),
                ResourceType.COURSE_EVENT,
                courseEvent.getId().toString(),
                EVENT_CREATE_EDIT.getPoints()
        );
    }
}
