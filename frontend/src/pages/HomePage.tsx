import { useEffect } from 'react'
import { upcomingEvents, latestNotice, careers } from '@/data/mockData'
import { RecentlyAccessedSection } from '@/components/home/RecentlyAccessedSection'
import { UpcomingEventsSection } from '@/components/home/UpcomingEventsSection'
import { LastNoticeSection } from '@/components/home/LastNoticeSection'
import { CareersCarousel } from '@/components/home/CareersCarousel'
import { useActivityStore } from '@/store/activityStore'
import { userService } from '@/services/user.service'
import { courseService } from '@/services/course.service'
import { studyRecordService } from '@/services/studyRecord.service'
import { careerService } from '@/services/career.service'
import { subjectService } from '@/services/subject.service'
import type { UserActivityDTO, CareerDTO } from '@/types/user.types'
import type { SubjectDTO } from '@/types/subject.types'
import type { RecentItem } from '@/types/content.types'

async function enrichActivity(
    dto: UserActivityDTO,
    allCareers: CareerDTO[],
    allSubjects: SubjectDTO[],
): Promise<RecentItem | null> {
    if (dto.resourceType === 'COURSE') {
        const course = await courseService.getById(Number(dto.resourceId))
        const career = allCareers.find((c) => c.id === course.careerId)
        return {
            id: String(course.id),
            type: 'course',
            title: course.name,
            subtitle: career ? `${career.name} · ${course.year}° año` : `${course.year}° año`,
            href: `/courses/${course.name}`,
            accessedAt: dto.timestamp,
        }
    }
    if (dto.resourceType === 'STUDY_RECORD') {
        const record = await studyRecordService.getBySlug(dto.resourceId)
        const subject = allSubjects.find((s) => s.id === record.subjectId)
        const career = subject?.careers[0]
        return {
            id: record.slug,
            type: 'apunte',
            title: record.title,
            subtitle: career && subject ? `${career.name} · ${subject.name}` : (subject?.name ?? ''),
            href: `/library/${record.slug}`,
            accessedAt: dto.timestamp,
        }
    }
    return null
}

export function HomePage() {
    const { recentItems, initialized, setItems } = useActivityStore()

    useEffect(() => {
        if (initialized) return

        Promise.all([
            userService.getRecentActivity(),
            careerService.getAll(),
            subjectService.getAll(),
        ])
            .then(async ([activities, allCareers, allSubjects]) => {
                const results = await Promise.allSettled(
                    activities.map((dto) => enrichActivity(dto, allCareers, allSubjects))
                )
                const items: RecentItem[] = results
                    .filter(
                        (r): r is PromiseFulfilledResult<RecentItem> =>
                            r.status === 'fulfilled' && r.value !== null
                    )
                    .map((r) => r.value)
                    .sort((a, b) => new Date(b.accessedAt).getTime() - new Date(a.accessedAt).getTime())
                setItems(items)
            })
            .catch(() => {
                setItems([])
            })
    }, [initialized, setItems])

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex flex-col gap-12">
            {recentItems.length > 0 && <RecentlyAccessedSection items={recentItems} />}
            <UpcomingEventsSection events={upcomingEvents} />
            <LastNoticeSection notice={latestNotice} />
            <CareersCarousel careers={careers} />
        </div>
    )
}
