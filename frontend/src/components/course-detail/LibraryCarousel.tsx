import { useCallback, useEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight, Download, BookOpen, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { studyRecordService } from '@/services/studyRecord.service'
import type { StudyRecordDTO } from '@/types/studyrecord.types'

interface LibraryCarouselProps {
    subjectId: number | null
    accentColor?: string // hex sin '#'
}

const SCROLL_AMOUNT = 240

export function LibraryCarousel({ subjectId, accentColor }: LibraryCarouselProps) {
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)
    const [records, setRecords] = useState<StudyRecordDTO[]>([])
    const [loading, setLoading] = useState(false)
    const [downloadingId, setDownloadingId] = useState<number | null>(null)

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
        if (subjectId === null) {
            setRecords([])
            return
        }
        setLoading(true)
        studyRecordService
            .getBySubject(subjectId, 0, 8)
            .then((page) => setRecords(page.content))
            .catch(() => setRecords([]))
            .finally(() => setLoading(false))
    }, [subjectId])

    const scroll = (direction: 'left' | 'right') => {
        scrollRef.current?.scrollBy({
            left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT,
            behavior: 'smooth',
        })
    }

    const handleDownload = async (record: StudyRecordDTO) => {
        setDownloadingId(record.id)
        try {
            const url = await studyRecordService.getDownloadUrl(record.id)
            window.open(url, '_blank', 'noopener,noreferrer')
        } catch {
            toast.error('No se pudo obtener el archivo. Intentá de nuevo.')
        } finally {
            setDownloadingId(null)
        }
    }

    const accent = accentColor ? `#${accentColor}` : undefined

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
                        {subjectId === null
                            ? 'Seleccioná una materia para ver la biblioteca'
                            : 'No hay apuntes disponibles para esta materia'}
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
                                <div
                                    key={record.id}
                                    style={accent ? { borderColor: `${accent}30` } : undefined}
                                    className={cn(
                                        'snap-start shrink-0 w-52 sm:w-60',
                                        'flex flex-col gap-3 rounded-xl border border-border bg-card p-4',
                                        'hover:border-primary/30 hover:shadow-md transition-all duration-200',
                                    )}
                                >
                                    {/* Title */}
                                    <div>
                                        <h4 className="font-semibold text-sm text-foreground line-clamp-2 leading-snug">
                                            {record.title}
                                        </h4>
                                        <p className="text-xs text-muted-foreground mt-1 line-clamp-2 leading-relaxed">
                                            {record.description}
                                        </p>
                                    </div>

                                    {/* Tags */}
                                    {record.tags.length > 0 && (
                                        <div className="flex flex-wrap gap-1">
                                            {record.tags.slice(0, 3).map((tag) => (
                                                <span
                                                    key={tag}
                                                    className="text-[10px] px-2 py-0.5 rounded-full bg-secondary text-muted-foreground font-medium"
                                                >
                                                    {tag}
                                                </span>
                                            ))}
                                            {record.tags.length > 3 && (
                                                <span className="text-[10px] text-muted-foreground">
                                                    +{record.tags.length - 3}
                                                </span>
                                            )}
                                        </div>
                                    )}

                                    {/* Footer: downloads + download button */}
                                    <div className="flex items-center justify-between mt-auto">
                                        <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                            <Download size={11} />
                                            {record.downloads.toLocaleString()}
                                        </span>
                                        <button
                                            onClick={() => handleDownload(record)}
                                            disabled={downloadingId === record.id}
                                            style={accent ? { backgroundColor: `${accent}1a`, color: accent } : undefined}
                                            className={cn(
                                                'flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-xl',
                                                'bg-primary/10 text-primary hover:bg-primary/15',
                                                'transition-colors duration-150 disabled:opacity-60 disabled:cursor-not-allowed',
                                            )}
                                        >
                                            {downloadingId === record.id ? (
                                                <Loader2 size={11} className="animate-spin" />
                                            ) : (
                                                <Download size={11} />
                                            )}
                                            Descargar
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </>
            )}
        </div>
    )
}
