import { TYPE_CONFIG } from '@/components/library/LibraryCard'
import type { StudyRecordDTO, SubjectSoftDTO } from '@/types/studyrecord.types'

interface StudyRecordBadgesProps {
    record: StudyRecordDTO
    subject: SubjectSoftDTO | null
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

export function StudyRecordBadges({ record, subject }: StudyRecordBadgesProps) {
    const typeConfig = TYPE_CONFIG[record.type]
    const typeColor = typeConfig?.color ?? `#${record.typeColor}`

    return (
        <div className="flex flex-wrap gap-1.5">
            {/* Career badges */}
            {subject?.careers.map((career) => (
                <Badge key={career.id} color={`#${career.color}`} label={career.name} />
            ))}

            {/* Subject badge */}
            {subject && (
                <Badge
                    color={subject.color ? `#${subject.color}` : FALLBACK_COLOR}
                    label={subject.name}
                />
            )}

            {/* Type badge */}
            <Badge color={typeColor} label={typeConfig?.label ?? record.type} />
        </div>
    )
}
