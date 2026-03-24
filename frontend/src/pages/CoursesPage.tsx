import { useState, useEffect, useMemo, useCallback } from 'react'
import { Search, ChevronDown, BookOpen, AlertCircle } from 'lucide-react'
import { toast } from 'sonner'
import { courseService } from '@/services/course.service'
import { careerService } from '@/services/career.service'
import { userService } from '@/services/user.service'
import { CourseCard, CourseCardSkeleton } from '@/components/courses/CourseCard'
import { SavedCoursesSection } from '@/components/courses/SavedCoursesSection'
import { SectionHeader } from '@/components/ui/SectionHeader'
import { useAuthStore } from '@/store/authStore'
import { cn } from '@/lib/utils'
import type { CareerDTO } from '@/types/user.types'
import type { CourseDTO } from '@/types/course.types'

const YEAR_OPTIONS = [
    { value: 1, label: '1er Año' },
    { value: 2, label: '2do Año' },
    { value: 3, label: '3er Año' },
    { value: 4, label: '4to Año' },
    { value: 5, label: '5to Año' },
]

const COURSES_PER_PAGE = 12
const MAX_BOOKMARKS = 6

export function CoursesPage() {
    const { user, setUser } = useAuthStore()

    // ── Saved courses (bookmarks) ───────────────────────────────────────────
    const [savedCourses, setSavedCourses] = useState<CourseDTO[]>(
        () => (user?.bookmarkedCourses ?? []).slice(0, MAX_BOOKMARKS),
    )
    const [isSaving, setIsSaving] = useState(false)

    // ── Explore courses ─────────────────────────────────────────────────────
    const [careers, setCareers] = useState<CareerDTO[]>([])
    const [courses, setCourses] = useState<CourseDTO[]>([])
    const [selectedCareerId, setSelectedCareerId] = useState<number | null>(null)
    const [selectedYear, setSelectedYear] = useState<number | null>(null)
    const [search, setSearch] = useState('')
    const [displayCount, setDisplayCount] = useState(COURSES_PER_PAGE)
    const [careersLoading, setCareersLoading] = useState(true)
    const [coursesLoading, setCoursesLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    // ── Cargar carreras ─────────────────────────────────────────────────────
    useEffect(() => {
        setCareersLoading(true)
        careerService
            .getAll()
            .then((data) => {
                const sorted = [...data].sort((a, b) => a.sortPosition - b.sortPosition)
                setCareers(sorted)
            })
            .catch(() => {/* continuar sin filtro de carrera */})
            .finally(() => setCareersLoading(false))
    }, []) // eslint-disable-line react-hooks/exhaustive-deps

    // ── Cargar cursos cuando cambia la carrera seleccionada ─────────────────
    useEffect(() => {
        setCoursesLoading(true)
        setError(null)
        const fetcher = courseService.getCourses(selectedCareerId ?? undefined)
        fetcher
            .then(setCourses)
            .catch(() => setError('No se pudieron cargar los cursos. Intentá de nuevo más tarde.'))
            .finally(() => setCoursesLoading(false))
    }, [selectedCareerId])

    // ── Mapas de carrera ────────────────────────────────────────────────────
    const careerMap = useMemo(
        () => new Map(careers.map((c) => [c.id, c.name])),
        [careers],
    )

    const careerColorMap = useMemo(
        () => new Map(careers.map((c) => [c.id, c.color])),
        [careers],
    )

    const careerSortMap = useMemo(
        () => new Map(careers.map((c) => [c.id, c.sortPosition])),
        [careers],
    )

    // ── Filtrado client-side (año + búsqueda) + ordenamiento ───────────────
    const filteredCourses = useMemo(() => {
        return courses
            .filter((c) => !selectedYear || c.year === selectedYear)
            .filter(
                (c) =>
                    !search ||
                    c.name.toLowerCase().includes(search.toLowerCase()),
            )
            .sort((a, b) => {
                const sa = careerSortMap.get(a.careerId) ?? 0
                const sb = careerSortMap.get(b.careerId) ?? 0
                if (sa !== sb) return sa - sb
                if (a.year !== b.year) return a.year - b.year
                return a.division - b.division
            })
    }, [courses, selectedYear, search, careerSortMap])

    const visibleCourses = filteredCourses.slice(0, displayCount)
    const hasMore = filteredCourses.length > displayCount

    // ── Set de IDs guardados (para el ícono bookmark) ──────────────────────
    const bookmarkedIds = useMemo(
        () => new Set(savedCourses.map((c) => c.id)),
        [savedCourses],
    )

    // ── Persistir bookmarks en el backend ──────────────────────────────────
    const persistBookmarks = useCallback(
        async (newList: CourseDTO[], rollback: CourseDTO[]) => {
            setIsSaving(true)
            try {
                const updated = await userService.updateUser({
                    firstName: null,
                    lastName: null,
                    birthday: null,
                    profile: null,
                    bookmarkedCourseIds: newList.map((c) => c.id),
                })
                setUser(updated)
                setSavedCourses(updated.bookmarkedCourses.slice(0, MAX_BOOKMARKS))
            } catch {
                setSavedCourses(rollback)
                toast.error('No se pudo actualizar los cursos guardados.')
            } finally {
                setIsSaving(false)
            }
        },
        [setUser],
    )

    // ── Toggle bookmark desde "Explorar Cursos" ────────────────────────────
    const handleBookmarkToggle = useCallback(
        (e: React.MouseEvent, course: CourseDTO) => {
            e.stopPropagation()
            if (isSaving) return

            const prev = savedCourses
            if (bookmarkedIds.has(course.id)) {
                const next = prev.filter((c) => c.id !== course.id)
                setSavedCourses(next)
                persistBookmarks(next, prev)
            } else {
                if (prev.length >= MAX_BOOKMARKS) {
                    toast.error(`Límite de ${MAX_BOOKMARKS} cursos guardados alcanzado.`)
                    return
                }
                const next = [...prev, course]
                setSavedCourses(next)
                persistBookmarks(next, prev)
            }
        },
        [isSaving, savedCourses, bookmarkedIds, persistBookmarks],
    )

    // ── Quitar bookmark desde "Cursos Guardados" ───────────────────────────
    const handleRemoveBookmark = useCallback(
        (courseId: number) => {
            if (isSaving) return
            const prev = savedCourses
            const next = prev.filter((c) => c.id !== courseId)
            setSavedCourses(next)
            persistBookmarks(next, prev)
        },
        [isSaving, savedCourses, persistBookmarks],
    )

    // ── Reordenar (compartido entre desktop DnD y mobile touch) ───────────────
    const handleReorder = useCallback(
        (fromIndex: number, toIndex: number) => {
            if (fromIndex === toIndex) return
            const prev = savedCourses
            const next = [...prev]
            const [moved] = next.splice(fromIndex, 1)
            next.splice(toIndex, 0, moved)
            setSavedCourses(next)
            persistBookmarks(next, prev)
        },
        [savedCourses, persistBookmarks],
    )

    // ── Handlers de filtros ─────────────────────────────────────────────────
    const handleCareerChange = (careerId: number | null) => {
        setSelectedCareerId(careerId)
        setDisplayCount(COURSES_PER_PAGE)
        setSearch('')
        setSelectedYear(null)
    }

    const handleYearChange = (year: number | null) => {
        setSelectedYear(year)
        setDisplayCount(COURSES_PER_PAGE)
    }

    const handleSearch = (value: string) => {
        setSearch(value)
        setDisplayCount(COURSES_PER_PAGE)
    }

    const selectedCareerName = selectedCareerId
        ? (careerMap.get(selectedCareerId) ?? '')
        : 'todas las carreras'

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex flex-col gap-12">

            {/* ────────────────── Cursos Guardados ────────────────── */}
            {savedCourses.length > 0 && (
                <section>
                    <SectionHeader title="Cursos Guardados" />

                    <SavedCoursesSection
                        courses={savedCourses}
                        careerMap={careerMap}
                        careerColorMap={careerColorMap}
                        onReorder={handleReorder}
                        onBookmarkToggle={handleRemoveBookmark}
                    />
                </section>
            )}

            {/* ────────────────── Explorar Cursos ────────────────── */}
            <section>
                {/* Header con búsqueda y filtros */}
                <div className="flex flex-col gap-3 mb-5">
                    <div className="flex flex-col sm:flex-row sm:items-center gap-3">
                        <h2 className="text-xl font-bold tracking-tight text-foreground shrink-0">
                            Explorar Cursos
                        </h2>

                        {/* Búsqueda */}
                        <div className="relative flex-1 max-w-xs">
                            <Search
                                size={15}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
                            />
                            <input
                                type="text"
                                placeholder="Buscar por nombre..."
                                value={search}
                                onChange={(e) => handleSearch(e.target.value)}
                                className="w-full pl-9 pr-4 py-2 text-sm rounded-xl bg-secondary border border-border focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50 transition-all duration-150 text-foreground placeholder:text-muted-foreground"
                            />
                        </div>

                        {/* Filtros */}
                        <div className="flex items-center gap-2 sm:ml-auto">
                            <span className="text-sm font-medium text-muted-foreground shrink-0">
                                Filtrar
                            </span>

                            {/* Dropdown Carrera */}
                            <div className="relative">
                                <select
                                    value={selectedCareerId ?? ''}
                                    onChange={(e) =>
                                        handleCareerChange(e.target.value ? Number(e.target.value) : null)
                                    }
                                    disabled={careersLoading}
                                    className={cn(
                                        'appearance-none pl-3 pr-8 py-2 text-sm rounded-xl border bg-secondary border-border',
                                        'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                        'transition-all duration-150 text-foreground cursor-pointer',
                                        'disabled:opacity-60 disabled:cursor-not-allowed',
                                        'max-w-[160px] truncate',
                                    )}
                                >
                                    {careersLoading ? (
                                        <option>Cargando...</option>
                                    ) : (
                                        <>
                                            <option value="">Todas las carreras</option>
                                            {careers.map((c) => (
                                                <option key={c.id} value={c.id}>
                                                    {c.name}
                                                </option>
                                            ))}
                                        </>
                                    )}
                                </select>
                                <ChevronDown
                                    size={14}
                                    className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
                                />
                            </div>

                            {/* Dropdown Año */}
                            <div className="relative">
                                <select
                                    value={selectedYear ?? ''}
                                    onChange={(e) =>
                                        handleYearChange(e.target.value ? Number(e.target.value) : null)
                                    }
                                    className={cn(
                                        'appearance-none pl-3 pr-8 py-2 text-sm rounded-xl border bg-secondary border-border',
                                        'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                        'transition-all duration-150 text-foreground cursor-pointer',
                                    )}
                                >
                                    <option value="">Año</option>
                                    {YEAR_OPTIONS.map((y) => (
                                        <option key={y.value} value={y.value}>
                                            {y.label}
                                        </option>
                                    ))}
                                </select>
                                <ChevronDown
                                    size={14}
                                    className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                {/* Grid de cursos */}
                {error ? (
                    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
                        <AlertCircle size={36} className="text-destructive/60" />
                        <p className="text-sm text-muted-foreground max-w-xs">{error}</p>
                        <button
                            onClick={() => handleCareerChange(selectedCareerId)}
                            className="mt-1 px-4 py-2 text-sm font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150"
                        >
                            Reintentar
                        </button>
                    </div>
                ) : coursesLoading ? (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        {Array.from({ length: 6 }).map((_, i) => (
                            <CourseCardSkeleton key={i} />
                        ))}
                    </div>
                ) : filteredCourses.length === 0 ? (
                    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
                        <BookOpen size={36} className="text-muted-foreground/40" />
                        <p className="text-sm font-medium text-muted-foreground">
                            {search || selectedYear
                                ? 'Sin resultados para los filtros aplicados'
                                : `No hay cursos disponibles para ${selectedCareerName}`}
                        </p>
                        {(search || selectedYear) && (
                            <button
                                onClick={() => {
                                    setSearch('')
                                    setSelectedYear(null)
                                }}
                                className="mt-1 px-4 py-2 text-sm font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150"
                            >
                                Limpiar filtros
                            </button>
                        )}
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                            {visibleCourses.map((course) => (
                                <CourseCard
                                    key={course.id}
                                    course={course}
                                    careerName={careerMap.get(course.careerId) ?? selectedCareerName}
                                    careerColor={careerColorMap.get(course.careerId)}
                                    isBookmarked={bookmarkedIds.has(course.id)}
                                    onBookmarkToggle={(e) => handleBookmarkToggle(e, course)}
                                />
                            ))}
                        </div>

                        {/* Cargar más */}
                        {hasMore && (
                            <div className="mt-6 flex justify-center">
                                <button
                                    onClick={() =>
                                        setDisplayCount((prev) => prev + COURSES_PER_PAGE)
                                    }
                                    className={cn(
                                        'px-6 py-2.5 text-sm font-medium rounded-xl border border-border',
                                        'bg-card text-foreground hover:border-primary/40 hover:bg-secondary',
                                        'transition-all duration-150 active:scale-[0.97]',
                                    )}
                                >
                                    Cargar más
                                </button>
                            </div>
                        )}
                    </>
                )}
            </section>
        </div>
    )
}
