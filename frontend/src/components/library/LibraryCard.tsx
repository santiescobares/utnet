import { cn } from '@/lib/utils'
import { UserAvatar } from '@/components/ui/UserAvatar'
import type { StudyRecordType } from '@/types/studyrecord.types'

// Colors mirror the backend StudyRecord.Type enum
export const TYPE_CONFIG: Record<StudyRecordType, { label: string; color: string }> = {
    SUMMARY:     { label: 'Resúmen',                color: '#0058E6' },
    NOTE:        { label: 'Apunte',                 color: '#00C4C8' },
    BIBLIOGRAPHY:{ label: 'Material Bibliográfico', color: '#E60086' },
    EXAM_MODEL:  { label: 'Modelo de Examen',       color: '#E68D00' },
}

export interface LibraryCardData {
    id: number
    title: string
    description: string
    type: StudyRecordType
    typeColor?: string  // hex 6 chars sin '#', proveniente del backend (StudyRecordDTO)
    author?: { firstName: string; lastName: string; pictureURL: string | null }
}

interface LibraryCardProps {
    record: LibraryCardData
    className?: string
    onClick?: () => void
}

export function LibraryCard({ record, className, onClick }: LibraryCardProps) {
    const config = TYPE_CONFIG[record.type]
    const label  = config?.label ?? record.type
    const color  = config?.color ?? (record.typeColor ? `#${record.typeColor}` : '#0066FF')
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
                <span
                    className="self-start inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border"
                    style={{
                        backgroundColor: `${color}22`,
                        color,
                        borderColor: `${color}44`,
                    }}
                >
                    {label}
                </span>

                <p className="text-sm font-semibold text-foreground leading-snug line-clamp-2">
                    {record.title}
                </p>

                <p className="text-xs text-muted-foreground leading-relaxed break-all">
                    {shortDesc}
                </p>
            </div>

            {record.author && (
                <div className="flex items-center gap-2 mt-4 pt-3 border-t border-border">
                    <UserAvatar
                        firstName={record.author.firstName}
                        lastName={record.author.lastName}
                        pictureURL={record.author.pictureURL}
                        size="sm"
                        className="w-6 h-6 text-[10px]"
                    />
                    <span className="text-xs text-muted-foreground truncate">
                        {record.author.firstName} {record.author.lastName}
                    </span>
                </div>
            )}
        </div>
    )
}
