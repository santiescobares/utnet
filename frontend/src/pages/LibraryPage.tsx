import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'
import { ChevronLeft, ChevronRight, Filter, Loader2, Search, Trash2, Upload, X } from 'lucide-react'
import { cn } from '@/lib/utils'
import { LibraryCard } from '@/components/library/LibraryCard'
import { LibraryFilterModal, type LibraryFilters } from '@/components/library/LibraryFilterModal'
import { studyRecordService } from '@/services/studyRecord.service'
import { mockLibraryRecords } from '@/data/mockData'
import type { StudyRecordDTO, StudyRecordType } from '@/types/studyrecord.types'

const TYPE_LABELS: Record<StudyRecordType, string> = {
    SUMMARY:      'Resúmen',
    NOTE:         'Apunte',
    BIBLIOGRAPHY: 'Material Bibliográfico',
    EXAM_MODEL:   'Modelo de Examen',
}

const EMPTY_FILTERS: LibraryFilters = { types: [], careers: [], subjects: [] }

// ── Active filter badge — identical to TagBadge in ReviewsSection ─────────────

interface ActiveFilterBadgeProps {
    label: string
    onRemove: () => void
}

function ActiveFilterBadge({ label, onRemove }: ActiveFilterBadgeProps) {
    const [showDelete, setShowDelete] = useState(false)
    const [hovered, setHovered] = useState(false)
    const btnRef = useRef<HTMLButtonElement>(null)
    const color = '#0066FF'

    useEffect(() => {
        if (!showDelete) return
        const handler = (e: MouseEvent) => {
            if (!btnRef.current?.contains(e.target as Node)) setShowDelete(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [showDelete])

    const showOverlay = hovered || showDelete

    return (
        <button
            ref={btnRef}
            type="button"
            onPointerEnter={(e) => { if (e.pointerType === 'mouse') setHovered(true) }}
            onPointerLeave={(e) => { if (e.pointerType === 'mouse') setHovered(false) }}
            onClick={(e) => {
                const pointerType = (e.nativeEvent as PointerEvent).pointerType
                if (pointerType === 'mouse') {
                    onRemove()
                } else {
                    if (showDelete) onRemove()
                    else setShowDelete(true)
                }
            }}
            style={{
                backgroundColor: showOverlay ? 'rgba(255,255,255,0.15)' : `${color}22`,
                color,
                borderColor: showOverlay ? `${color}88` : `${color}44`,
            }}
            className="relative inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold border transition-all duration-150 touch-manipulation"
        >
            <span className={cn('transition-opacity duration-150', showOverlay && 'opacity-0')}>
                {label}
            </span>
            <Trash2
                size={10}
                className={cn(
                    'absolute inset-0 m-auto transition-opacity duration-150',
                    showOverlay ? 'opacity-100' : 'opacity-0',
                )}
            />
        </button>
    )
}

// ── Library section — carousel matching CareersCarousel style ─────────────────

const MAX_RECORDS = 10

const arrowBase = cn(
    'hidden sm:flex',
    'absolute top-1/2 -translate-y-1/2 z-10',
    'w-9 h-9 items-center justify-center rounded-full',
    'bg-background border border-border shadow-md',
    'text-foreground/70 hover:text-foreground hover:shadow-lg hover:scale-105',
    'transition-all duration-150',
)

interface LibrarySectionProps {
    title: string
    records: typeof mockLibraryRecords
}

function LibrarySection({ title, records }: LibrarySectionProps) {
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)

    const limited = records.slice(0, MAX_RECORDS)

    const updateArrows = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        setCanScrollLeft(el.scrollLeft > 1)
        setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 1)
    }, [])

    useEffect(() => {
        const el = scrollRef.current
        if (!el) return
        updateArrows()
        const ro = new ResizeObserver(updateArrows)
        ro.observe(el)
        return () => ro.disconnect()
    }, [limited.length, updateArrows])

    if (limited.length === 0) return null

    const scrollByCard = (dir: 'left' | 'right') => {
        const el = scrollRef.current
        if (!el) return
        const gap = 12
        const cardWidth = (el.clientWidth - 4 * gap) / 5
        const amount = cardWidth + gap
        el.scrollBy({ left: dir === 'right' ? amount : -amount, behavior: 'smooth' })
    }

    return (
        <section className="flex flex-col gap-3">
            <h2 className="text-base font-semibold text-foreground">{title}</h2>

            <div className="relative">
                <button
                    onClick={() => scrollByCard('left')}
                    aria-label="Anterior"
                    className={cn(arrowBase, '-left-4', !canScrollLeft && 'opacity-0 pointer-events-none')}
                >
                    <ChevronLeft size={18} />
                </button>

                <button
                    onClick={() => scrollByCard('right')}
                    aria-label="Siguiente"
                    className={cn(arrowBase, '-right-4', !canScrollRight && 'opacity-0 pointer-events-none')}
                >
                    <ChevronRight size={18} />
                </button>

                <div
                    ref={scrollRef}
                    onScroll={updateArrows}
                    className="flex gap-3 overflow-x-auto scrollbar-hide snap-x snap-mandatory py-2 -my-2"
                >
                    {limited.map((record) => (
                        <LibraryCard
                            key={record.id}
                            record={record}
                            className={cn(
                                'snap-start shrink-0',
                                'w-[175px] sm:w-[calc((100%-3rem)/5)]',
                            )}
                        />
                    ))}
                </div>
            </div>
        </section>
    )
}

// ── Page ──────────────────────────────────────────────────────────────────────

export function LibraryPage() {
    const [searchQuery, setSearchQuery]     = useState('')
    const [activeFilters, setActiveFilters] = useState<LibraryFilters>(EMPTY_FILTERS)
    const [showFilterModal, setShowFilterModal] = useState(false)

    // Search state — null means "not searched yet", show sections instead
    const [searchResults, setSearchResults]   = useState<StudyRecordDTO[] | null>(null)
    const [lastSearchQuery, setLastSearchQuery] = useState('')
    const [isSearching, setIsSearching]       = useState(false)

    const navigate = useNavigate()

    const handleSearch = async (q: string) => {
        const trimmed = q.trim()
        if (!trimmed) {
            setSearchResults(null)
            setLastSearchQuery('')
            return
        }
        setIsSearching(true)
        setLastSearchQuery(trimmed)
        try {
            const page = await studyRecordService.search(trimmed, undefined, undefined, 0, 30)
            setSearchResults(page.content)
        } catch {
            setSearchResults([])
        } finally {
            setIsSearching(false)
        }
    }

    const clearSearch = () => {
        setSearchQuery('')
        setSearchResults(null)
        setLastSearchQuery('')
    }

    // Mock sections — only used when not in search mode; filtered by activeFilters only
    const filteredRecords = mockLibraryRecords.filter((r) => {
        if (activeFilters.types.length > 0 && !activeFilters.types.includes(r.type)) return false
        return true
    })

    const recent  = filteredRecords.filter((r) => r.section === 'recent')
    const popular = filteredRecords.filter((r) => r.section === 'popular')
    const latest  = filteredRecords.filter((r) => r.section === 'latest')

    const activeFilterLabels: { key: string; label: string }[] = [
        ...activeFilters.types.map((t)   => ({ key: `type:${t}`,      label: TYPE_LABELS[t] })),
        ...activeFilters.careers.map((c) => ({ key: `career:${c.id}`, label: c.name })),
        ...activeFilters.subjects.map((s)=> ({ key: `subject:${s.id}`,label: s.name })),
    ]

    const removeTypeFilter    = (type: StudyRecordType) =>
        setActiveFilters((p) => ({ ...p, types:    p.types.filter((t)    => t !== type) }))
    const removeCareerFilter  = (id: number) =>
        setActiveFilters((p) => ({ ...p, careers:  p.careers.filter((c)  => c.id !== id) }))
    const removeSubjectFilter = (id: number) =>
        setActiveFilters((p) => ({ ...p, subjects: p.subjects.filter((s) => s.id !== id) }))

    const totalActive = activeFilters.types.length + activeFilters.careers.length + activeFilters.subjects.length

    return (
        <div className="flex flex-col gap-6 px-4 sm:px-6 pt-6 pb-10">

            {/* Header */}
            <div>
                <h1 className="text-2xl font-bold text-foreground">Biblioteca</h1>
                <p className="text-sm text-muted-foreground mt-1">
                    Apuntes, modelos de parcial, y todo lo que necesitás para aprobar
                </p>
            </div>

            {/* Search + buttons */}
            <div className="flex flex-col gap-2">
                <div className="flex gap-2 sm:grid sm:grid-cols-5 sm:gap-3">
                    <div className="relative flex-1 min-w-0 sm:col-span-4">
                        {isSearching ? (
                            <Loader2
                                size={16}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground animate-spin"
                            />
                        ) : (
                            <Search
                                size={16}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
                            />
                        )}
                        <input
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            onKeyDown={(e) => { if (e.key === 'Enter') handleSearch(searchQuery) }}
                            placeholder="Buscar en la biblioteca..."
                            className={cn(
                                'w-full pl-9 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                                searchResults !== null ? 'pr-8' : 'pr-3',
                            )}
                        />
                        {searchResults !== null && (
                            <button
                                onClick={clearSearch}
                                aria-label="Limpiar búsqueda"
                                className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors duration-150"
                            >
                                <X size={14} />
                            </button>
                        )}
                    </div>

                    <div className="flex gap-1.5 sm:col-span-1 sm:gap-2">
                        <button
                            onClick={() => setShowFilterModal(true)}
                            className={cn(
                                'flex items-center justify-center gap-1.5 px-3 py-2.5 text-sm font-medium rounded-xl border shrink-0',
                                'sm:flex-[2] transition-all duration-150',
                                totalActive > 0
                                    ? 'bg-primary/10 border-primary/40 text-primary'
                                    : 'bg-secondary border-border text-foreground hover:border-primary/40 hover:bg-primary/5',
                            )}
                        >
                            <Filter size={15} className="shrink-0" />
                            <span>Filtrar</span>
                            {totalActive > 0 && (
                                <span className="w-5 h-5 rounded-full bg-primary text-primary-foreground text-[11px] font-bold flex items-center justify-center shrink-0">
                                    {totalActive}
                                </span>
                            )}
                        </button>

                        <button
                            onClick={() => navigate('/library/new-resource')}
                            className={cn(
                                'flex items-center justify-center gap-2 px-3 py-2.5 text-sm font-medium rounded-xl shrink-0',
                                'sm:flex-[3] bg-primary text-primary-foreground hover:bg-primary/90 transition-all duration-150',
                            )}
                        >
                            <Upload size={15} className="shrink-0" />
                            <span className="hidden sm:inline">Crear Recurso</span>
                        </button>
                    </div>
                </div>

                {/* Active filter badges */}
                {activeFilterLabels.length > 0 && (
                    <div className="flex items-start gap-2">
                        <button
                            onClick={() => setActiveFilters(EMPTY_FILTERS)}
                            title="Limpiar todos los filtros"
                            className="shrink-0 p-1.5 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors duration-150 mt-0.5"
                        >
                            <Trash2 size={14} />
                        </button>
                        <div className="flex flex-wrap gap-1.5">
                            {activeFilterLabels.map(({ key, label }) => (
                                <ActiveFilterBadge
                                    key={key}
                                    label={label}
                                    onRemove={() => {
                                        if (key.startsWith('type:'))    removeTypeFilter(key.slice(5) as StudyRecordType)
                                        else if (key.startsWith('career:'))  removeCareerFilter(Number(key.slice(7)))
                                        else if (key.startsWith('subject:')) removeSubjectFilter(Number(key.slice(8)))
                                    }}
                                />
                            ))}
                        </div>
                    </div>
                )}
            </div>

            {/* Search results */}
            {searchResults !== null ? (
                searchResults.length === 0 ? (
                    <div className="flex flex-col items-center gap-2 py-16 text-center">
                        <Search size={32} className="text-muted-foreground/40" />
                        <p className="text-sm text-muted-foreground">
                            No se encontraron resultados para "<span className="font-medium">{lastSearchQuery}</span>".
                        </p>
                    </div>
                ) : (
                    <div className="flex flex-col gap-3">
                        <p className="text-sm text-muted-foreground">
                            {searchResults.length} resultado{searchResults.length !== 1 ? 's' : ''} para "<span className="font-medium text-foreground">{lastSearchQuery}</span>"
                        </p>
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3 py-2 -my-2">
                            {searchResults.map((record) => (
                                <LibraryCard
                                    key={record.id}
                                    record={record}
                                    className="min-w-0"
                                    onClick={() => navigate(`/library/${record.slug}`)}
                                />
                            ))}
                        </div>
                    </div>
                )
            ) : (
                /* Mock sections */
                recent.length === 0 && popular.length === 0 && latest.length === 0 ? (
                    <div className="flex flex-col items-center gap-2 py-16 text-center">
                        <Search size={32} className="text-muted-foreground/40" />
                        <p className="text-sm text-muted-foreground">
                            No se encontraron resultados para los filtros aplicados.
                        </p>
                    </div>
                ) : (
                    <div className="flex flex-col gap-8">
                        <LibrarySection title="Vistos recientemente" records={recent} />
                        <LibrarySection title="Más Descargados"      records={popular} />
                        <LibrarySection title="Últimas Publicaciones" records={latest} />
                    </div>
                )
            )}

            <LibraryFilterModal
                open={showFilterModal}
                initial={activeFilters}
                onClose={() => setShowFilterModal(false)}
                onApply={setActiveFilters}
            />
        </div>
    )
}
