import { TYPE_CONFIG } from '@/components/library/LibraryCard'
import type { StudyRecordDTO } from '@/types/studyrecord.types'

interface StudyRecordBadgesProps {
    record: StudyRecordDTO
}

const FALLBACK_COLOR = '#6B7280'

function Badge({ color, label }: { color: string; label: string }) {
    return (
        <span
            className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border"
            style={{
                backgroundColor: `${color}22`,
                color,
                borderColor: `${color}44`,
            }}
        >
            {label}
        </span>
    )
}

export function StudyRecordBadges({ record }: StudyRecordBadgesProps) {
    const typeConfig = TYPE_CONFIG[record.type]
    const typeColor  = typeConfig?.color ?? `#${record.typeColor}`

    // Deduplicate careers across all subjects
    const seenCareerIds = new Set<number>()
    const careers = record.subjects.flatMap((s) => s.careers).filter((c) => {
        if (seenCareerIds.has(c.id)) return false
        seenCareerIds.add(c.id)
        return true
    })

    return (
        <div className="flex flex-wrap gap-1.5">
            {/* Career badges */}
            {careers.map((career) => (
                <Badge
                    key={career.id}
                    color={career.color ? `#${career.color}` : FALLBACK_COLOR}
                    label={career.name}
                />
            ))}

            {/* Subject badges (full name) */}
            {record.subjects.map((s) => (
                <Badge
                    key={s.id}
                    color={s.color ? `#${s.color}` : FALLBACK_COLOR}
                    label={s.name}
                />
            ))}

            {/* Type badge */}
            <Badge color={typeColor} label={typeConfig?.label ?? record.type} />
        </div>
    )
}
