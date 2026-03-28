import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useParams, useNavigate, useSearchParams } from 'react-router'
import {
    ArrowLeft, BookOpen, AlertCircle, Loader2, CalendarPlus,
} from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { courseService } from '@/services/course.service'
import { courseSubjectService } from '@/services/courseSubject.service'
import { subjectService } from '@/services/subject.service'
import { careerService } from '@/services/career.service'
import { courseEventService } from '@/services/courseEvent.service'
import { SectionHeader } from '@/components/ui/SectionHeader'
import { CourseSubjectsCarousel } from '@/components/course-detail/CourseSubjectsCarousel'
import { CalendarGrid } from '@/components/course-detail/CalendarGrid'
import { EventDetailPanel } from '@/components/course-detail/EventDetailPanel'
import { EventFormModal } from '@/components/course-detail/EventFormModal'
import { SubjectInfoPanel } from '@/components/course-detail/SubjectInfoPanel'
import { LibraryCarousel } from '@/components/course-detail/LibraryCarousel'
import { ReviewsSection } from '@/components/course-detail/ReviewsSection'
import type {
    CourseDTO,
    CourseSubjectDTO,
    CourseSubjectUpdateDTO,
    CourseEventDTO,
    CourseEventCreateDTO,
    CourseEventUpdateDTO,
    CourseEventTag,
} from '@/types/course.types'
import type { SubjectDTO } from '@/types/subject.types'
import type { CareerDTO } from '@/types/user.types'

// ── Date helpers ───────────────────────────────────────────────────────────────

function getToday() {
    const now = new Date()
    return { year: now.getFullYear(), month: now.getMonth() }
}

function getMaxMonth() {
    const now = new Date()
    const max = new Date(now.getFullYear(), now.getMonth() + 12, 1)
    return { year: max.getFullYear(), month: max.getMonth() }
}

function toIsoDate(year: number, month: number, day: number): string {
    return `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
}

function firstDayOfMonth(year: number, month: number) {
    return toIsoDate(year, month, 1)
}

function lastDayOfMonth(year: number, month: number) {
    return toIsoDate(year, month, new Date(year, month + 1, 0).getDate())
}

// ── Page component ─────────────────────────────────────────────────────────────

export function CourseDetailPage() {
    const { courseName } = useParams<{ courseName: string }>()
    const navigate = useNavigate()
    const [searchParams, setSearchParams] = useSearchParams()
    const initialTabRef = useRef(searchParams.get('tab'))

    // ── Base data ────────────────────────────────────────────────────────────
    const [course, setCourse] = useState<CourseDTO | null>(null)
    const [career, setCareer] = useState<CareerDTO | null>(null)
    const [courseSubjects, setCourseSubjects] = useState<CourseSubjectDTO[]>([])
    const [subjectDetails, setSubjectDetails] = useState<SubjectDTO[]>([])
    const [pageLoading, setPageLoading] = useState(true)
    const [notFound, setNotFound] = useState(false)
    const [pageError, setPageError] = useState<string | null>(null)

    // ── Selected subject ─────────────────────────────────────────────────────
    const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(null)

    const selectedCourseSubject = useMemo(
        () => courseSubjects.find((cs) => cs.subjectId === selectedSubjectId) ?? null,
        [courseSubjects, selectedSubjectId],
    )

    const selectedSubjectDetail = useMemo(
        () => subjectDetails.find((s) => s.id === selectedSubjectId) ?? null,
        [subjectDetails, selectedSubjectId],
    )

    // ── Calendar state ───────────────────────────────────────────────────────
    const todayMonth = getToday()
    const maxMonth = getMaxMonth()
    const [calYear, setCalYear] = useState(todayMonth.year)
    const [calMonth, setCalMonth] = useState(todayMonth.month)
    const [events, setEvents] = useState<CourseEventDTO[]>([])
    const [eventsLoading, setEventsLoading] = useState(false)
    const [selectedEventId, setSelectedEventId] = useState<number | null>(null)
    const [selectedDate, setSelectedDate] = useState<string | null>(() => new Date().toISOString().split('T')[0])
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [upcomingEvents, setUpcomingEvents] = useState<CourseEventDTO[]>([])
    const [upcomingEventsLoading, setUpcomingEventsLoading] = useState(false)
    const upcomingEventsCache = useRef(new Map<string, CourseEventDTO[]>())

    const minEventDate = useMemo(() => new Date().toISOString().split('T')[0], [])
    const maxEventDate = useMemo(
        () => lastDayOfMonth(maxMonth.year, maxMonth.month),
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    )

    // ── Month navigation ─────────────────────────────────────────────────────
    const canGoPrev =
        calYear > todayMonth.year || (calYear === todayMonth.year && calMonth > todayMonth.month)
    const canGoNext =
        calYear < maxMonth.year || (calYear === maxMonth.year && calMonth < maxMonth.month)

    const goToPrevMonth = () => {
        if (!canGoPrev) return
        setSelectedDate(null)
        setSelectedEventId(null)
        if (calMonth === 0) { setCalYear((y) => y - 1); setCalMonth(11) }
        else setCalMonth((m) => m - 1)
    }

    const goToNextMonth = () => {
        if (!canGoNext) return
        setSelectedDate(null)
        setSelectedEventId(null)
        if (calMonth === 11) { setCalYear((y) => y + 1); setCalMonth(0) }
        else setCalMonth((m) => m + 1)
    }

    // ── Load course + ancillary data ─────────────────────────────────────────
    useEffect(() => {
        if (!courseName) return
        setPageLoading(true)
        setNotFound(false)
        setPageError(null)

        courseService
            .getByName(courseName)
            .then(async (c) => {
                setCourse(c)
                const [courseSubjs, allSubjects, allCareers] = await Promise.all([
                    courseSubjectService.getByCourseId(c.id),
                    subjectService.getAll(),
                    careerService.getAll(),
                ])
                setCourseSubjects(courseSubjs)
                setSubjectDetails(allSubjects)
                setCareer(allCareers.find((ca) => ca.id === c.careerId) ?? null)
                if (courseSubjs.length > 0) {
                    const tab = initialTabRef.current
                    const matched = tab
                        ? allSubjects.find((s) => s.shortName?.toLowerCase() === tab.toLowerCase())
                        : null
                    const matchedCs = matched
                        ? courseSubjs.find((cs) => cs.subjectId === matched.id)
                        : null
                    const initialSubjectId = matchedCs?.subjectId ?? courseSubjs[0].subjectId
                    setSelectedSubjectId(initialSubjectId)
                    const initialSubject = allSubjects.find((s) => s.id === initialSubjectId)
                    const initialTab = initialSubject?.shortName?.toLowerCase()
                    if (initialTab) setSearchParams({ tab: initialTab }, { replace: true })
                }
            })
            .catch((err) => {
                if (err?.response?.status === 404) setNotFound(true)
                else setPageError('No se pudo cargar el curso. Intentá de nuevo.')
            })
            .finally(() => setPageLoading(false))
    }, [courseName])

    // ── Load upcoming events for a given date (cached) ───────────────────────
    const fetchUpcomingForDate = useCallback(async (courseId: number, dateStr: string) => {
        if (upcomingEventsCache.current.has(dateStr)) {
            setUpcomingEvents(upcomingEventsCache.current.get(dateStr)!)
            return
        }
        setUpcomingEventsLoading(true)
        try {
            const from = new Date(dateStr)
            const to = new Date(dateStr)
            to.setDate(to.getDate() + 30)
            const toStr = to.toISOString().split('T')[0]
            const data = await courseEventService.getByCourse(courseId, dateStr, toStr)
            const sorted = [...data].sort((a, b) => {
                if (a.date !== b.date) return a.date < b.date ? -1 : 1
                return (a.startTime ?? '') < (b.startTime ?? '') ? -1 : 1
            })
            upcomingEventsCache.current.set(dateStr, sorted)
            setUpcomingEvents(sorted)
        } catch { /* silencioso */ }
        finally { setUpcomingEventsLoading(false) }
    }, [])

    // ── Load events for current month ────────────────────────────────────────
    const loadEvents = useCallback(async (courseId: number, year: number, month: number) => {
        setEventsLoading(true)
        try {
            // Extend range to cover overflow days from prev and next month shown in the grid
            const firstDay = new Date(year, month, 1).getDay()
            const daysInMonth = new Date(year, month + 1, 0).getDate()
            const pYear = month === 0 ? year - 1 : year
            const pMonth = month === 0 ? 11 : month - 1
            const pDays = new Date(pYear, pMonth + 1, 0).getDate()
            const fromDate = firstDay > 0 ? toIsoDate(pYear, pMonth, pDays - firstDay + 1) : firstDayOfMonth(year, month)
            const overflowDays = (firstDay + daysInMonth) % 7 === 0 ? 0 : 7 - ((firstDay + daysInMonth) % 7)
            const nYear = month === 11 ? year + 1 : year
            const nMonth = month === 11 ? 0 : month + 1
            const toDate = overflowDays > 0 ? toIsoDate(nYear, nMonth, overflowDays) : lastDayOfMonth(year, month)
            const data = await courseEventService.getByCourse(
                courseId,
                fromDate,
                toDate,
            )
            setEvents(data)
        } catch {
            toast.error('No se pudieron cargar los eventos del mes.')
        } finally {
            setEventsLoading(false)
        }
    }, [])

    useEffect(() => {
        if (!course) return
        loadEvents(course.id, calYear, calMonth)
    }, [course, calYear, calMonth, loadEvents])

    // Precarga upcoming events para el día inicial (hoy) al montar
    useEffect(() => {
        if (!course) return
        const today = new Date().toISOString().split('T')[0]
        fetchUpcomingForDate(course.id, today)
    }, [course, fetchUpcomingForDate])

    // ── Event CRUD ────────────────────────────────────────────────────────────
    const handleEventCreate = async (dto: CourseEventCreateDTO) => {
        await courseEventService.create(dto)
        upcomingEventsCache.current.clear()
        await loadEvents(course!.id, calYear, calMonth)
        if (selectedDate) await fetchUpcomingForDate(course!.id, selectedDate)
        toast.success('Evento creado.')
    }

    const handleEventEdit = async (id: number, { description, tag }: { description: string; tag: CourseEventTag | null }) => {
        const dto: CourseEventUpdateDTO = { description, tag: tag ?? undefined }
        const updated = await courseEventService.update(id, dto)
        setEvents((prev) => prev.map((e) => (e.id === id ? updated : e)))
        toast.success('Evento actualizado.')
    }

    const handleEventDelete = async (id: number) => {
        await courseEventService.delete(id)
        setEvents((prev) => prev.filter((e) => e.id !== id))
        if (selectedEventId === id) setSelectedEventId(null)
        upcomingEventsCache.current.clear()
        if (selectedDate) await fetchUpcomingForDate(course!.id, selectedDate)
        toast.success('Evento eliminado.')
    }

    const handleSubjectSelect = (subjectId: number) => {
        setSelectedSubjectId(subjectId)
        const subject = subjectDetails.find((s) => s.id === subjectId)
        const tab = subject?.shortName?.toLowerCase()
        setSearchParams(tab ? { tab } : {}, { replace: true })
    }

    const handleCourseSubjectUpdate = async (id: number, dto: CourseSubjectUpdateDTO) => {
        const updated = await courseSubjectService.update(id, dto)
        setCourseSubjects((prev) => prev.map((cs) => (cs.id === id ? updated : cs)))
        toast.success('Información actualizada.')
    }

    const handleDayClick = (dateStr: string) => {
        setSelectedDate(dateStr)
        const dayEvents = events.filter((e) => e.date === dateStr)
        setSelectedEventId(dayEvents.length > 0 ? dayEvents[0].id : null)
        if (course) fetchUpcomingForDate(course.id, dateStr)
    }

    const handleUpcomingEventSelect = (dateStr: string) => {
        const [y, m] = dateStr.split('-').map(Number)
        const targetYear = y
        const targetMonth = m - 1  // 0-indexed
        if (targetYear !== calYear || targetMonth !== calMonth) {
            setCalYear(targetYear)
            setCalMonth(targetMonth)
        }
        handleDayClick(dateStr)
    }

    const dayEvents = useMemo(
        () =>
            selectedDate
                ? events
                      .filter((e) => e.date === selectedDate)
                      .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
                : [],
        [events, selectedDate],
    )
    const accentColor = career?.color
    const accent = accentColor ? `#${accentColor}` : undefined

    // ── Render states ─────────────────────────────────────────────────────────
    if (pageLoading) {
        return (
            <div className="flex items-center justify-center py-20">
                <Loader2 size={28} className="animate-spin text-muted-foreground" />
            </div>
        )
    }

    if (notFound) {
        return (
            <div className="flex flex-col items-center gap-4 py-20 text-center px-4">
                <BookOpen size={40} className="text-muted-foreground/40" />
                <p className="text-base font-semibold text-foreground">Curso no encontrado</p>
                <p className="text-sm text-muted-foreground">
                    El curso <span className="font-mono font-medium">{courseName}</span> no existe.
                </p>
                <button
                    onClick={() => navigate('/courses')}
                    className="mt-2 px-4 py-2.5 text-sm font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150 touch-manipulation"
                >
                    Volver a Cursos
                </button>
            </div>
        )
    }

    if (pageError || !course) {
        return (
            <div className="flex flex-col items-center gap-4 py-20 text-center px-4">
                <AlertCircle size={36} className="text-destructive/60" />
                <p className="text-sm text-muted-foreground">{pageError ?? 'Error desconocido.'}</p>
                <button
                    onClick={() => navigate('/courses')}
                    className="mt-1 px-4 py-2.5 text-sm font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150 touch-manipulation"
                >
                    Volver
                </button>
            </div>
        )
    }

    // ── Main render ───────────────────────────────────────────────────────────
    return (
        <div className="flex flex-col lg:flex-row lg:h-[calc(100vh-4rem)] lg:overflow-hidden">

            {/* ════════════════════════════════════════════════════
                LEFT PANEL — scrolleable
            ═════════════════════════════════════════════════════ */}
            <div className="flex-1 min-w-0 overflow-y-auto scrollbar-thin animate-in slide-in-from-left-4 fade-in duration-500">
                <div className="px-4 sm:px-6 lg:px-8 py-4 flex flex-col gap-6">

                    {/* ── Back + Course header ── */}
                    <div className="flex flex-col gap-3">
                        <button
                            onClick={() => navigate('/courses')}
                            className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors duration-150 w-fit touch-manipulation"
                        >
                            <ArrowLeft size={15} />
                            Volver a Cursos
                        </button>

                        <div className="flex items-center gap-3 flex-wrap">
                            <div
                                style={accent ? { backgroundColor: `${accent}1a` } : undefined}
                                className="w-12 h-12 rounded-2xl bg-primary/10 flex items-center justify-center shrink-0"
                            >
                                <BookOpen
                                    size={22}
                                    style={accent ? { color: accent } : undefined}
                                    className="text-primary"
                                />
                            </div>
                            <div>
                                <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground leading-tight">
                                    {course.name}
                                </h1>
                                {career && (
                                    <span
                                        style={accent
                                            ? { backgroundColor: `${accent}1a`, color: accent }
                                            : undefined
                                        }
                                        className="inline-flex items-center mt-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary/10 text-primary"
                                    >
                                        {career.name}
                                    </span>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* ── Subjects carousel ── */}
                    {courseSubjects.length > 0 && (
                        <CourseSubjectsCarousel
                            courseSubjects={courseSubjects}
                            subjectDetails={subjectDetails}
                            selectedSubjectId={selectedSubjectId}
                            accentColor={accentColor}
                            onSelect={handleSubjectSelect}
                        />
                    )}

                    {/* ── Subject info (mobile only) ── */}
                    <div className="lg:hidden">
                        <SubjectInfoPanel
                            courseSubject={selectedCourseSubject}
                            subjectDetail={selectedSubjectDetail}
                            accentColor={accentColor}
                            onUpdate={handleCourseSubjectUpdate}
                            className="rounded-2xl border border-border bg-card"
                        />
                    </div>

                    {/* ── Calendario de Eventos ── */}
                    <section className="flex flex-col">
                        <SectionHeader
                            title="Calendario de Eventos"
                            action={
                                <button
                                    onClick={() => setShowCreateModal(true)}
                                    disabled={!!selectedDate && selectedDate < minEventDate}
                                    style={accent ? { color: accent, backgroundColor: `${accent}1a` } : undefined}
                                    className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150 touch-manipulation disabled:opacity-40 disabled:cursor-not-allowed"
                                    aria-label="Crear evento"
                                >
                                    <CalendarPlus size={14} />
                                    Nuevo
                                </button>
                            }
                        />

                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

                            {/* Calendar card */}
                            <div className="rounded-2xl border border-border bg-card p-3 lg:h-[310px] lg:overflow-hidden lg:flex lg:flex-col">
                                {eventsLoading ? (
                                    <div className="flex items-center justify-center py-10">
                                        <Loader2 size={18} className="animate-spin text-muted-foreground" />
                                    </div>
                                ) : (
                                    <CalendarGrid
                                        className="flex-1"
                                        year={calYear}
                                        month={calMonth}
                                        events={events}
                                        selectedEventId={selectedEventId}
                                        selectedDate={selectedDate}
                                        accentColor={accentColor}
                                        canGoPrev={canGoPrev}
                                        canGoNext={canGoNext}
                                        onPrevMonth={goToPrevMonth}
                                        onNextMonth={goToNextMonth}
                                        onDayClick={handleDayClick}
                                        onOverflowDayClick={handleUpcomingEventSelect}
                                        onEventSelect={(id) => {
                                            setSelectedEventId((prev) => (prev === id ? null : id))
                                            setSelectedDate(events.find((e) => e.id === id)?.date ?? null)
                                        }}
                                    />
                                )}
                            </div>

                            {/* Events panel */}
                            <div className="rounded-2xl border border-border bg-card overflow-hidden lg:h-[310px]">
                                <EventDetailPanel
                                    events={dayEvents}
                                    selectedDate={selectedDate}
                                    upcomingEvents={upcomingEvents}
                                    upcomingEventsLoading={upcomingEventsLoading}
                                    onSelectDate={handleUpcomingEventSelect}
                                    accentColor={accentColor}
                                    onEdit={handleEventEdit}
                                    onDelete={handleEventDelete}
                                    className="h-full"
                                />
                            </div>
                        </div>
                    </section>

                    {/* ── Reviews (mobile only) ── */}
                    <section className="lg:hidden flex flex-col">
                        <div className="rounded-2xl border border-border bg-card overflow-hidden">
                            <ReviewsSection
                                courseId={course.id}
                                accentColor={accentColor}
                                courseSubjects={courseSubjects}
                                subjectDetails={subjectDetails}
                            />
                        </div>
                    </section>

                    {/* ── Biblioteca ── */}
                    <section className="flex flex-col">
                        <SectionHeader title="Biblioteca" />
                        <LibraryCarousel
                            subjectId={selectedSubjectId}
                            accentColor={accentColor}
                        />
                    </section>

                    {/* Bottom padding for mobile */}
                    <div className="h-4 lg:hidden" />
                </div>
            </div>

            {/* ════════════════════════════════════════════════════
                RIGHT PANEL — sticky sidebar (desktop only)
                Solo SubjectInfoPanel + Reviews (sin EventDetailPanel)
            ═════════════════════════════════════════════════════ */}
            <aside className={cn(
                'hidden lg:flex lg:flex-col',
                'w-[340px] xl:w-[380px] shrink-0',
                'border-l border-border',
                'sticky top-0 h-[calc(100vh-4rem)] overflow-hidden',
                'animate-in slide-in-from-right-4 fade-in duration-500',
            )}>

                <SubjectInfoPanel
                    courseSubject={selectedCourseSubject}
                    subjectDetail={selectedSubjectDetail}
                    accentColor={accentColor}
                    onUpdate={handleCourseSubjectUpdate}
                    className="shrink-0"
                />

                <div className="h-px bg-border shrink-0" />

                <div className="flex flex-col flex-1 min-h-0 overflow-y-auto scrollbar-thin">
                    <ReviewsSection
                        courseId={course.id}
                        accentColor={accentColor}
                        courseSubjects={courseSubjects}
                        subjectDetails={subjectDetails}
                    />
                </div>
            </aside>

            {/* ── Create event modal ── */}
            <EventFormModal
                open={showCreateModal}
                courseId={course.id}
                minDate={minEventDate}
                maxDate={maxEventDate}
                initialDate={selectedDate ?? undefined}
                onClose={() => setShowCreateModal(false)}
                onSubmit={handleEventCreate}
            />
        </div>
    )
}
