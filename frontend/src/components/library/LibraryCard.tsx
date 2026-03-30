import { Download } from 'lucide-react'
import { cn } from '@/lib/utils'
import { UserAvatar } from '@/components/ui/UserAvatar'
import type { StudyRecordType, SubjectSnapshotDTO, UserSnapshotDTO } from '@/types/studyrecord.types'

// Colors mirror the backend StudyRecord.Type enum
export const TYPE_CONFIG: Record<StudyRecordType, { label: string; color: string }> = {
    SUMMARY:     { label: 'Resúmen',                color: '#0058E6' },
    NOTE:        { label: 'Apunte',                 color: '#00C4C8' },
    BIBLIOGRAPHY:{ label: 'Material Bibliográfico', color: '#E60086' },
    EXAM_MODEL:  { label: 'Modelo de Examen',       color: '#E68D00' },
}

const SUBJECT_FALLBACK_COLOR = '#6B7280'

export interface LibraryCardData {
    id: number
    title: string
    description: string
    type: StudyRecordType
    typeColor?: string
    subjects?: SubjectSnapshotDTO[]
    downloads?: number
    createdBy?: UserSnapshotDTO
}

interface LibraryCardProps {
    record: LibraryCardData
    className?: string
    onClick?: () => void
}

function Badge({ color, label }: { color: string; label: string }) {
    return (
        <span
            className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border whitespace-nowrap"
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

export function LibraryCard({ record, className, onClick }: LibraryCardProps) {
    const config    = TYPE_CONFIG[record.type]
    const label     = config?.label ?? record.type
    const typeColor = config?.color ?? (record.typeColor ? `#${record.typeColor}` : '#0066FF')

    const shortDesc = record.description.length > 80
        ? record.description.slice(0, 80).trimEnd() + '…'
        : record.description

    return (
        <div
            onClick={onClick}
            className={cn(
                'flex flex-col justify-between',
                'rounded-2xl border border-border bg-card',
                'p-4 select-none',
                onClick && 'cursor-pointer',
                'transition-[border-color,box-shadow,transform] duration-200',
                onClick && 'hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5 hover:-translate-y-1',
                'active:scale-[0.98]',
                className,
            )}
        >
            <div className="flex flex-col gap-2">
                {/* Title row + downloads */}
                <div className="flex items-start justify-between gap-2">
                    <p className="text-sm font-semibold text-foreground leading-snug line-clamp-2 flex-1">
                        {record.title}
                    </p>
                    {record.downloads !== undefined && (
                        <div className="flex items-center gap-1 text-muted-foreground shrink-0 pt-0.5">
                            <Download size={11} />
                            <span className="text-[11px] font-medium tabular-nums">{record.downloads}</span>
                        </div>
                    )}
                </div>

                <p className="text-xs text-muted-foreground leading-relaxed break-all">
                    {shortDesc}
                </p>

                {/* Type + subject badges */}
                <div className="flex flex-wrap gap-1">
                    <Badge color={typeColor} label={label} />
                    {record.subjects?.map((s) => (
                        <Badge
                            key={s.id}
                            color={s.color ? `#${s.color}` : SUBJECT_FALLBACK_COLOR}
                            label={s.shortName}
                        />
                    ))}
                </div>
            </div>

            {/* Author footer */}
            {record.createdBy && (
                <div className="flex items-center gap-2 mt-4">
                    <UserAvatar
                        firstName={record.createdBy.firstName}
                        lastName={record.createdBy.lastName}
                        pictureURL={record.createdBy.profilePictureURL}
                        size="sm"
                        className="w-6 h-6 text-[10px]"
                    />
                    <span className="text-xs text-muted-foreground truncate">
                        Publicado por {record.createdBy.firstName} {record.createdBy.lastName}
                    </span>
                </div>
            )}
        </div>
    )
}
