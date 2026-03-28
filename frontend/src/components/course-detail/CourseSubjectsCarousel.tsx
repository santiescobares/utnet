import { useCallback, useEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CourseSubjectDTO } from '@/types/course.types'
import type { SubjectDTO } from '@/types/subject.types'

interface CourseSubjectsCarouselProps {
    courseSubjects: CourseSubjectDTO[]
    subjectDetails: SubjectDTO[]
    selectedSubjectId: number | null
    accentColor?: string // hex sin '#'
    onSelect: (subjectId: number) => void
}

const SCROLL_AMOUNT = 200

export function CourseSubjectsCarousel({
    courseSubjects,
    subjectDetails,
    selectedSubjectId,
    accentColor,
    onSelect,
}: CourseSubjectsCarouselProps) {
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)

    const subjectMap = new Map(subjectDetails.map((s) => [s.id, s]))

    const updateArrows = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        setCanScrollLeft(el.scrollLeft > 4)
        setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 4)
    }, [])

    useEffect(() => {
        updateArrows()
    }, [courseSubjects, updateArrows])

    const scroll = (direction: 'left' | 'right') => {
        scrollRef.current?.scrollBy({
            left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT,
            behavior: 'smooth',
        })
    }

    const accent = accentColor ? `#${accentColor}` : undefined

    const arrowBase = cn(
        'hidden lg:flex',
        'absolute top-1/2 -translate-y-1/2 z-10',
        'w-8 h-8 items-center justify-center rounded-full',
        'bg-background border border-border shadow-sm',
        'text-foreground/60 hover:text-foreground hover:shadow-md hover:scale-105',
        'transition-all duration-150',
    )

    if (courseSubjects.length === 0) return null

    return (
        <div className="relative">
            <button
                onClick={() => scroll('left')}
                aria-label="Anterior"
                className={cn(arrowBase, '-left-4', !canScrollLeft && 'opacity-0 pointer-events-none')}
            >
                <ChevronLeft size={16} />
            </button>

            <button
                onClick={() => scroll('right')}
                aria-label="Siguiente"
                className={cn(arrowBase, '-right-4', !canScrollRight && 'opacity-0 pointer-events-none')}
            >
                <ChevronRight size={16} />
            </button>

            <div className="-mx-4 px-4 sm:-mx-6 sm:px-6 lg:mx-0 lg:px-0">
                <div
                    ref={scrollRef}
                    onScroll={updateArrows}
                    className="flex gap-2 overflow-x-auto pb-0.5 snap-x snap-mandatory scrollbar-hide"
                >
                    {courseSubjects.map((cs) => {
                        const subject = subjectMap.get(cs.subjectId)
                        const isSelected = selectedSubjectId === cs.subjectId
                        return (
                            <button
                                key={cs.id}
                                onClick={() => onSelect(cs.subjectId)}
                                style={isSelected && accent
                                    ? { color: accent, backgroundColor: `${accent}18`, borderColor: `${accent}50` }
                                    : undefined
                                }
                                className={cn(
                                    // Ancho fijo igual para todos los badges
                                    'snap-start shrink-0 w-[110px] sm:w-[120px]',
                                    'flex flex-col gap-0.5 px-3 py-2 rounded-2xl border text-left',
                                    'transition-all duration-200 touch-manipulation',
                                    isSelected
                                        ? 'border-primary/40 bg-primary/10 text-primary'
                                        : 'border-border bg-card text-foreground hover:border-primary/30 hover:bg-secondary',
                                )}
                            >
                                <span className={cn(
                                    'text-sm font-bold truncate leading-tight',
                                    isSelected ? '' : 'text-foreground',
                                )}>
                                    {subject?.shortName ?? `Mat. ${cs.subjectId}`}
                                </span>
                                <span className={cn(
                                    'text-[11px] truncate leading-tight',
                                    isSelected ? 'opacity-75' : 'text-muted-foreground',
                                )}>
                                    {subject?.name ?? ''}
                                </span>
                            </button>
                        )
                    })}
                </div>
            </div>
        </div>
    )
}
