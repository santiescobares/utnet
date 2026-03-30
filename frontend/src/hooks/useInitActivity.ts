import { useEffect } from 'react'
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

/**
 * Dispara el GET de actividad reciente una sola vez por sesión (mientras
 * initialized sea false). Llamar desde cualquier página que necesite los
 * datos antes de que el usuario haya pasado por la home.
 */
export function useInitActivity(): void {
    const { initialized, setItems } = useActivityStore()

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
}
