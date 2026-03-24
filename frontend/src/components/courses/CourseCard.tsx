import { Bookmark, BookOpen, GripVertical } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CourseDTO } from '@/types/course.types'

const YEAR_LABELS: Record<number, string> = {
    1: '1er Año',
    2: '2do Año',
    3: '3er Año',
    4: '4to Año',
    5: '5to Año',
}

interface CourseCardProps {
    course: CourseDTO
    careerName: string
    careerColor?: string // hex 6 chars sin '#', ej: "FF5733"
    className?: string
    onClick?: () => void
    // Bookmark toggle (para cards en "Explorar Cursos")
    isBookmarked?: boolean
    onBookmarkToggle?: (e: React.MouseEvent) => void
    // Drag handle (para cards en "Cursos Guardados")
    isDraggable?: boolean
    dragHandleProps?: React.HTMLAttributes<HTMLSpanElement>
    isDragOver?: boolean
    isDragging?: boolean
}

export function CourseCard({
    course,
    careerName,
    careerColor,
    className,
    onClick,
    isBookmarked,
    onBookmarkToggle,
    isDraggable,
    dragHandleProps,
    isDragOver,
    isDragging,
}: CourseCardProps) {
    const yearLabel = YEAR_LABELS[course.year] ?? `Año ${course.year}`
    const accent = careerColor ? `#${careerColor}` : undefined

    return (
        <div
            onClick={onClick}
            style={accent ? { borderColor: `${accent}40` } : undefined}
            className={cn(
                'group relative flex flex-col gap-4 p-5 rounded-2xl border bg-card',
                'border-border hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5',
                'hover:-translate-y-1 active:scale-[0.98]',
                'transition-all duration-200 cursor-pointer select-none',
                isDragOver && 'ring-2 ring-primary/40 border-primary/40',
                isDragging && 'opacity-50 scale-[0.98]',
                className,
            )}
        >
            {/* Top row: year pill + action icons */}
            <div className="flex items-center justify-between gap-2">
                <span
                    style={accent ? { backgroundColor: `${accent}1a`, color: accent } : undefined}
                    className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-primary/10 text-primary"
                >
                    {yearLabel}
                </span>

                <div className="flex items-center gap-1">
                    {/* Bookmark toggle */}
                    {onBookmarkToggle && (
                        <button
                            onClick={onBookmarkToggle}
                            className={cn(
                                'shrink-0 p-1 rounded-lg transition-all duration-150',
                                isBookmarked
                                    ? 'text-primary hover:bg-primary/10'
                                    // mobile: siempre visible (no hay hover); desktop: aparece al pasar el cursor
                                    : 'text-muted-foreground sm:opacity-0 sm:group-hover:opacity-100 hover:bg-secondary hover:text-foreground',
                            )}
                            title={isBookmarked ? 'Quitar de guardados' : 'Guardar curso'}
                        >
                            <Bookmark
                                size={16}
                                className={cn(isBookmarked && 'fill-current')}
                            />
                        </button>
                    )}

                    {/* Drag handle — solo en "Cursos Guardados" */}
                    {isDraggable && (
                        <span
                            {...dragHandleProps}
                            onClick={(e) => e.stopPropagation()}
                            style={{ touchAction: 'none' }}
                            className="shrink-0 p-1 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary cursor-grab active:cursor-grabbing transition-colors duration-150"
                            title="Arrastrar para reordenar"
                        >
                            <GripVertical size={16} />
                        </span>
                    )}
                </div>
            </div>

            {/* Course icon + name + career */}
            <div className="flex items-center gap-3">
                <div
                    style={accent ? { backgroundColor: `${accent}1a` } : undefined}
                    className="shrink-0 w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center group-hover:bg-primary/15 transition-colors duration-200"
                >
                    <BookOpen size={20} style={accent ? { color: accent } : undefined} className="text-primary" />
                </div>
                <div className="min-w-0">
                    <p className="text-xl font-bold text-foreground leading-tight tracking-wide">
                        {course.name}
                    </p>
                    <p className="text-xs text-muted-foreground mt-0.5 truncate">
                        {careerName}
                    </p>
                </div>
            </div>
        </div>
    )
}

interface CourseCardSkeletonProps {
    className?: string
}

export function CourseCardSkeleton({ className }: CourseCardSkeletonProps) {
    return (
        <div className={cn('flex flex-col gap-4 p-5 rounded-2xl border border-border bg-card', className)}>
            <div className="h-6 w-20 rounded-full bg-muted animate-pulse" />
            <div className="flex items-center gap-3">
                <div className="shrink-0 w-10 h-10 rounded-xl bg-muted animate-pulse" />
                <div className="flex flex-col gap-1.5 flex-1">
                    <div className="h-5 w-16 rounded bg-muted animate-pulse" />
                    <div className="h-3 w-24 rounded bg-muted animate-pulse" />
                </div>
            </div>
        </div>
    )
}
