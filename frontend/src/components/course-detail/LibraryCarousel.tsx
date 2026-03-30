import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'
import { ChevronLeft, ChevronRight, BookOpen, Loader2 } from 'lucide-react'
import { cn } from '@/lib/utils'
import { studyRecordService } from '@/services/studyRecord.service'
import { LibraryCard } from '@/components/library/LibraryCard'
import type { StudyRecordDTO } from '@/types/studyrecord.types'

interface LibraryCarouselProps {
    subjectIds: number[]
    accentColor?: string // hex sin '#'
}

const SCROLL_AMOUNT = 240

export function LibraryCarousel({ subjectIds }: LibraryCarouselProps) {
    const navigate = useNavigate()
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)
    const [records, setRecords] = useState<StudyRecordDTO[]>([])
    const [loading, setLoading] = useState(false)

    const updateArrows = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        setCanScrollLeft(el.scrollLeft > 4)
        setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 4)
    }, [])

    useEffect(() => {
        updateArrows()
    }, [records, updateArrows])

    useEffect(() => {
        if (subjectIds.length === 0) {
            setRecords([])
            return
        }
        setLoading(true)
        studyRecordService
            .search(undefined, subjectIds, undefined, 0, 10)
            .then((page) => setRecords(page.content))
            .catch(() => setRecords([]))
            .finally(() => setLoading(false))
    }, [subjectIds])

    const scroll = (direction: 'left' | 'right') => {
        scrollRef.current?.scrollBy({
            left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT,
            behavior: 'smooth',
        })
    }

    const arrowBase = cn(
        'hidden lg:flex',
        'absolute top-1/2 -translate-y-1/2 z-10',
        'w-8 h-8 items-center justify-center rounded-full',
        'bg-background border border-border shadow-md',
        'text-foreground/70 hover:text-foreground hover:shadow-lg hover:scale-105',
        'transition-all duration-150',
    )

    return (
        <div className="relative animate-in fade-in duration-300">
            {loading ? (
                <div className="flex items-center justify-center py-10">
                    <Loader2 size={20} className="animate-spin text-muted-foreground" />
                </div>
            ) : records.length === 0 ? (
                <div className="flex flex-col items-center justify-center gap-2 py-10 text-center">
                    <BookOpen size={28} className="text-muted-foreground/40" />
                    <p className="text-sm text-muted-foreground">
                        No hay apuntes disponibles para este curso
                    </p>
                </div>
            ) : (
                <>
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
                            className="flex gap-3 overflow-x-auto pb-3 snap-x snap-mandatory scrollbar-hide"
                        >
                            {records.map((record) => (
                                <LibraryCard
                                    key={record.id}
                                    record={record}
                                    className="snap-start shrink-0 w-60 sm:w-[calc((100%-2.25rem)/4)]"
                                    onClick={() => navigate(`/library/${record.slug}`)}
                                />
                            ))}
                        </div>
                    </div>
                </>
            )}
        </div>
    )
}
