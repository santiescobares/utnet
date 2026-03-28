import { useEffect, useMemo, useRef, useState } from 'react'
import { createPortal } from 'react-dom'
import { Link } from 'react-router'
import { Star, Flag, Loader2, AlertCircle, MessageSquarePlus, Tag, Trash2, Plus, MessageSquare, Send, SlidersHorizontal, Check } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { courseReviewService } from '@/services/courseReview.service'
import { reportService } from '@/services/report.service'
import { ConfirmActionModal } from '@/components/ui/ConfirmActionModal'
import type { CourseReviewDTO, CourseReviewCreateDTO, CourseSubjectDTO } from '@/types/course.types'
import type { UserSnapshotDTO } from '@/types/user.types'
import type { SubjectDTO } from '@/types/subject.types'

// ── Star display ──────────────────────────────────────────────────────────────

export function StarDisplay({ rating, size = 12 }: { rating: number; size?: number }) {
    return (
        <div className="flex items-center gap-0.5">
            {Array.from({ length: 5 }, (_, i) => (
                <Star
                    key={i}
                    size={size}
                    className={cn(
                        'transition-colors duration-100',
                        i < Math.round(rating)
                            ? 'text-amber-400 fill-amber-400'
                            : 'text-muted-foreground/30',
                    )}
                />
            ))}
        </div>
    )
}

// ── Star selector ─────────────────────────────────────────────────────────────

function StarSelector({ value, onChange }: { value: number; onChange: (v: number) => void }) {
    const [hovered, setHovered] = useState(0)
    return (
        <div className="flex items-center gap-0.5">
            {Array.from({ length: 5 }, (_, i) => {
                const starValue = i + 1
                const filled = starValue <= (hovered || value)
                return (
                    <button
                        key={i}
                        type="button"
                        onClick={() => onChange(starValue)}
                        onMouseEnter={() => setHovered(starValue)}
                        onMouseLeave={() => setHovered(0)}
                        className="min-w-[25px] min-h-[25px] flex items-center justify-center transition-transform duration-100 hover:scale-110 touch-manipulation"
                        aria-label={`${starValue} estrella${starValue > 1 ? 's' : ''}`}
                    >
                        <Star
                            size={16}
                            className={cn(
                                'transition-colors duration-100',
                                filled ? 'text-amber-400 fill-amber-400' : 'text-muted-foreground/30',
                            )}
                        />
                    </button>
                )
            })}
        </div>
    )
}

// ── User avatar ───────────────────────────────────────────────────────────────

function UserAvatar({ user, size = 32 }: { user: UserSnapshotDTO; size?: number }) {
    const [imgError, setImgError] = useState(false)
    const initials = `${user.firstName[0] ?? ''}${user.lastName[0] ?? ''}`.toUpperCase()
    if (user.profilePictureURL && !imgError) {
        return (
            <img
                src={user.profilePictureURL}
                alt={`${user.firstName} ${user.lastName}`}
                onError={() => setImgError(true)}
                style={{ width: size, height: size }}
                className="rounded-full object-cover shrink-0 ring-1 ring-border"
            />
        )
    }
    return (
        <div
            style={{ width: size, height: size }}
            className="rounded-full bg-primary/10 text-primary flex items-center justify-center shrink-0 text-[11px] font-semibold select-none"
        >
            {initials}
        </div>
    )
}

// ── Subject tag badge ─────────────────────────────────────────────────────────

interface TagBadgeProps {
    tag: SubjectDTO
    color: string
    onRemove?: () => void
    readonly?: boolean
}

function TagBadge({ tag, color, onRemove, readonly = false }: TagBadgeProps) {
    // showDelete: used only on touch (mobile first-tap reveals delete UI)
    const [showDelete, setShowDelete] = useState(false)
    // hovered: used only on mouse (desktop hover reveals delete UI)
    const [hovered, setHovered] = useState(false)
    const btnRef = useRef<HTMLButtonElement>(null)

    // Deselect on outside tap (mobile)
    useEffect(() => {
        if (!showDelete) return
        const handler = (e: MouseEvent) => {
            if (!btnRef.current?.contains(e.target as Node)) setShowDelete(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [showDelete])

    if (readonly) {
        return (
            <span
                style={{ backgroundColor: `${color}22`, color: color, borderColor: `${color}44` }}
                className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border"
            >
                {tag.shortName}
            </span>
        )
    }

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
                    // Desktop: single click removes immediately
                    onRemove?.()
                } else {
                    // Touch: first tap reveals delete UI, second tap removes
                    if (showDelete) onRemove?.()
                    else setShowDelete(true)
                }
            }}
            style={{
                backgroundColor: showOverlay ? 'rgba(255,255,255,0.15)' : `${color}22`,
                color: color,
                borderColor: showOverlay ? `${color}88` : `${color}44`,
            }}
            className="relative inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border transition-all duration-150 touch-manipulation"
            aria-label={`Eliminar etiqueta ${tag.shortName}`}
        >
            <span className={cn('transition-opacity duration-150', showOverlay && 'opacity-0')}>
                {tag.shortName}
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

// ── Tag dropdown ──────────────────────────────────────────────────────────────

interface TagDropdownProps {
    availableSubjects: SubjectDTO[]
    accentColor?: string
    onSelect: (subject: SubjectDTO) => void
    disabled?: boolean
}

function TagDropdown({ availableSubjects, accentColor, onSelect, disabled }: TagDropdownProps) {
    const [open, setOpen] = useState(false)
    const [pos, setPos] = useState<{ top: number; right: number } | null>(null)
    const btnRef = useRef<HTMLButtonElement>(null)
    const dropRef = useRef<HTMLDivElement>(null)
    const accent = accentColor ? `#${accentColor}` : undefined

    const handleOpen = () => {
        if (btnRef.current) {
            const r = btnRef.current.getBoundingClientRect()
            setPos({ top: r.bottom + 4, right: window.innerWidth - r.right })
        }
        setOpen(true)
    }

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (
                !btnRef.current?.contains(e.target as Node) &&
                !dropRef.current?.contains(e.target as Node)
            ) setOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    return (
        <>
            <button
                ref={btnRef}
                type="button"
                onClick={handleOpen}
                disabled={disabled}
                className="min-w-[20px] min-h-[20px] flex items-center justify-center rounded-md text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-30 touch-manipulation"
                title="Agregar etiqueta"
            >
                <Plus size={12} />
            </button>
            {open && pos && (
                <div
                    ref={dropRef}
                    style={{ top: pos.top, right: pos.right }}
                    className="fixed z-50 w-44 bg-card border border-border rounded-xl shadow-lg py-1 animate-in fade-in zoom-in-95 duration-150 max-h-48 overflow-y-auto"
                >
                    {availableSubjects.length === 0 ? (
                        <p className="px-3 py-2 text-xs text-muted-foreground">Sin materias disponibles</p>
                    ) : (
                        availableSubjects.map((s) => {
                            const tagColor = s.color ? `#${s.color}` : (accent ?? '#6366f1')
                            return (
                                <button
                                    key={s.id}
                                    type="button"
                                    onClick={() => { onSelect(s); setOpen(false) }}
                                    className="w-full text-left flex items-center gap-2 px-3 py-2 text-xs text-foreground hover:bg-secondary transition-colors duration-100 touch-manipulation"
                                >
                                    <span
                                        style={{ backgroundColor: `${tagColor}33`, color: tagColor }}
                                        className="inline-flex px-1.5 py-0.5 rounded-full font-semibold shrink-0"
                                    >
                                        {s.shortName}
                                    </span>
                                    <span className="truncate text-muted-foreground">{s.name}</span>
                                </button>
                            )
                        })
                    )}
                </div>
            )}
        </>
    )
}

// ── Filter dropdown ───────────────────────────────────────────────────────────

interface FilterDropdownProps {
    subjects: SubjectDTO[]
    selectedIds: Set<number>
    onToggle: (id: number) => void
    accentColor?: string
}

function FilterDropdown({ subjects, selectedIds, onToggle, accentColor }: FilterDropdownProps) {
    const [open, setOpen] = useState(false)
    const [pos, setPos] = useState<{ top: number; right: number } | null>(null)
    const btnRef = useRef<HTMLButtonElement>(null)
    const dropRef = useRef<HTMLDivElement>(null)
    const accent = accentColor ? `#${accentColor}` : undefined
    const activeCount = selectedIds.size

    const handleOpen = () => {
        if (open) { setOpen(false); return }
        if (btnRef.current) {
            const r = btnRef.current.getBoundingClientRect()
            setPos({ top: r.bottom + 4, right: window.innerWidth - r.right })
        }
        setOpen(true)
    }

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (
                !btnRef.current?.contains(e.target as Node) &&
                !dropRef.current?.contains(e.target as Node)
            ) setOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    if (subjects.length === 0) return null

    return (
        <>
            <button
                ref={btnRef}
                type="button"
                onClick={handleOpen}
                aria-label="Filtrar reseñas"
                style={activeCount > 0 && accent
                    ? { color: accent, backgroundColor: `${accent}1a`, borderColor: `${accent}44` }
                    : undefined}
                className={cn(
                    'relative flex items-center gap-1 px-2.5 py-1 text-xs font-medium rounded-xl',
                    'border transition-colors duration-150 touch-manipulation',
                    activeCount > 0
                        ? 'border-primary/40 bg-primary/10 text-primary'
                        : 'border-border bg-card text-muted-foreground hover:text-foreground hover:border-primary/30',
                )}
            >
                <SlidersHorizontal size={12} />
            </button>
            {open && pos && (
                <div
                    ref={dropRef}
                    style={{ top: pos.top, right: pos.right }}
                    className="fixed z-50 w-52 bg-card border border-border rounded-xl shadow-lg py-1 animate-in fade-in zoom-in-95 duration-150"
                >
                    {subjects.map((s) => {
                        const tagColor = s.color ? `#${s.color}` : (accent ?? '#6366f1')
                        const checked = selectedIds.has(s.id)
                        return (
                            <button
                                key={s.id}
                                type="button"
                                onClick={() => onToggle(s.id)}
                                className="w-full flex items-center gap-2.5 px-3 py-2 text-xs text-foreground hover:bg-secondary transition-colors duration-100 touch-manipulation"
                            >
                                <span
                                    style={checked && accent ? { backgroundColor: accent, borderColor: accent } : undefined}
                                    className={cn(
                                        'w-3.5 h-3.5 rounded flex items-center justify-center border shrink-0 transition-colors duration-100',
                                        checked ? 'bg-primary border-primary' : 'border-border',
                                    )}
                                >
                                    {checked && <Check size={9} className="text-primary-foreground" />}
                                </span>
                                <span
                                    style={{ backgroundColor: `${tagColor}33`, color: tagColor }}
                                    className="inline-flex px-1.5 py-0.5 rounded-full text-[10px] font-semibold shrink-0"
                                >
                                    {s.shortName}
                                </span>
                                <span className="truncate text-muted-foreground">{s.name}</span>
                            </button>
                        )
                    })}
                </div>
            )}
        </>
    )
}

// ── Review card ───────────────────────────────────────────────────────────────

function ReviewCard({
    review,
    accentColor,
    onReport,
}: {
    review: CourseReviewDTO
    accentColor?: string
    onReport: (review: CourseReviewDTO) => void
}) {
    const accent = accentColor ? `#${accentColor}` : undefined
    return (
        <div className="relative flex flex-col gap-2 p-3 rounded-xl border border-border bg-card/50 animate-in fade-in slide-in-from-bottom-2 duration-200">
            {/* Report button */}
            <button
                onClick={() => onReport(review)}
                className="absolute top-2.5 right-2.5 min-w-[32px] min-h-[32px] flex items-center justify-center rounded-lg text-muted-foreground/40 hover:text-muted-foreground hover:bg-secondary transition-colors duration-150 touch-manipulation"
                title="Reportar reseña"
                aria-label="Reportar reseña"
            >
                <Flag size={11} />
            </button>

            {/* User info */}
            <Link
                to={`/users/${review.postedBy.id}`}
                className="flex items-center gap-2 pr-6 group w-fit"
            >
                <UserAvatar user={review.postedBy} size={28} />
                <div className="min-w-0">
                    <p className="text-xs font-semibold text-foreground truncate group-hover:text-foreground/70">
                        {review.postedBy.firstName} {review.postedBy.lastName}
                    </p>
                    <StarDisplay rating={review.rating} size={10} />
                </div>
            </Link>

            {/* Content */}
            <p className="text-xs text-muted-foreground leading-relaxed">
                {review.content}
            </p>

            {/* Subject tags */}
            {review.subjectTags.length > 0 && (
                <div className="flex flex-wrap gap-1 mt-0.5">
                    {review.subjectTags.map((tag) => {
                        const tagColor = tag.color ? `#${tag.color}` : (accent ?? '#6366f1')
                        return (
                            <TagBadge key={tag.id} tag={tag} color={tagColor} readonly />
                        )
                    })}
                </div>
            )}
        </div>
    )
}

// ── Main component ────────────────────────────────────────────────────────────

interface ReviewsSectionProps {
    courseId: number
    accentColor?: string
    courseSubjects?: CourseSubjectDTO[]
    subjectDetails?: SubjectDTO[]
}

export function ReviewsSection({ courseId, accentColor, courseSubjects, subjectDetails }: ReviewsSectionProps) {
    const [reviews, setReviews] = useState<CourseReviewDTO[]>([])
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [loading, setLoading] = useState(false)
    const [loadingMore, setLoadingMore] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const averageRating = useMemo(() => {
        if (!reviews.length) return null
        return reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
    }, [reviews])

    const [showForm, setShowForm] = useState(false)
    const [formRating, setFormRating] = useState(0)
    const [formContent, setFormContent] = useState('')
    const [formTags, setFormTags] = useState<SubjectDTO[]>([])
    const [showPublishConfirm, setShowPublishConfirm] = useState(false)
    const [isPublishing, setIsPublishing] = useState(false)

    const [filterTags, setFilterTags] = useState<Set<number>>(new Set())

    const toggleFilterTag = (id: number) => {
        setFilterTags((prev) => {
            const next = new Set(prev)
            if (next.has(id)) next.delete(id)
            else next.add(id)
            return next
        })
    }

    const filteredReviews = useMemo(() => {
        if (filterTags.size === 0) return reviews
        return reviews.filter((r) => r.subjectTags.some((t) => filterTags.has(t.id)))
    }, [reviews, filterTags])

    const [reportingReview, setReportingReview] = useState<CourseReviewDTO | null>(null)
    const [reportReason, setReportReason] = useState('')
    const [showReportConfirm, setShowReportConfirm] = useState(false)
    const [isReporting, setIsReporting] = useState(false)

    const accent = accentColor ? `#${accentColor}` : undefined

    // Subjects available for tagging (those in this course, not yet selected)
    const tagSubjectPool = useMemo(() => {
        if (!subjectDetails || !courseSubjects) return []
        const courseSubjectIds = new Set(courseSubjects.map((cs) => cs.subjectId))
        return subjectDetails.filter((s) => courseSubjectIds.has(s.id))
    }, [subjectDetails, courseSubjects])

    const availableTagSubjects = useMemo(
        () => tagSubjectPool.filter((s) => !formTags.some((t) => t.id === s.id)),
        [tagSubjectPool, formTags],
    )

    // Initial load
    useEffect(() => {
        setLoading(true)
        setReviews([])
        setPage(0)
        courseReviewService
            .getByCourse(courseId, 0, 8)
            .then((resp) => {
                setReviews(resp.content)
                setTotalPages(resp.totalPages)
                setPage(0)
            })
            .catch(() => setError('No se pudieron cargar las reseñas.'))
            .finally(() => setLoading(false))
    }, [courseId])

    const handleLoadMore = async () => {
        const nextPage = page + 1
        setLoadingMore(true)
        try {
            const resp = await courseReviewService.getByCourse(courseId, nextPage, 8)
            setReviews((prev) => [...prev, ...resp.content])
            setPage(nextPage)
            setTotalPages(resp.totalPages)
        } catch {
            toast.error('No se pudieron cargar más reseñas.')
        } finally {
            setLoadingMore(false)
        }
    }

    const handlePublishConfirm = async () => {
        if (!formRating || !formContent.trim()) return
        setIsPublishing(true)
        const dto: CourseReviewCreateDTO = {
            courseId,
            content: formContent.trim(),
            rating: formRating,
            ...(formTags.length > 0 && { subjectTagIds: formTags.map((t) => t.id) }),
        }
        try {
            const newReview = await courseReviewService.create(dto)
            setReviews((prev) => [newReview, ...prev])
            setShowForm(false)
            setFormRating(0)
            setFormContent('')
            setFormTags([])
            setShowPublishConfirm(false)
            toast.success('Reseña publicada.')
        } catch {
            toast.error('No se pudo publicar la reseña. Es posible que ya hayas escrito una.')
            setShowPublishConfirm(false)
        } finally {
            setIsPublishing(false)
        }
    }

    const handleReportConfirm = async () => {
        if (!reportingReview || reportReason.trim().length < 10) return
        setIsReporting(true)
        try {
            await reportService.create({
                resourceType: 'COURSE_REVIEW',
                resourceId: String(reportingReview.id),
                reason: reportReason.trim(),
            })
            setReportingReview(null)
            setShowReportConfirm(false)
            toast.success('Reporte enviado.')
        } catch {
            toast.error('No se pudo enviar el reporte.')
        } finally {
            setIsReporting(false)
        }
    }

    // ── Review form ───────────────────────────────────────────────────────────
    if (showForm) {
        return (
            <div className="flex flex-col gap-4 px-4 py-5 animate-in fade-in slide-in-from-bottom-2 duration-200">
                {/* Title + subtitle placeholder (matches SubjectInfoPanel rhythm) */}
                <div className="min-w-0">
                    <h2 className="text-lg font-bold text-foreground leading-tight">Nueva reseña</h2>
                </div>

                {/* Calificación + Etiquetas en la misma fila, simétricos */}
                <div className="flex items-start gap-4">
                    {/* Calificación */}
                    <div className="flex-1 flex flex-col gap-1.5">
                        <div className="flex items-center gap-1.5">
                            <Star size={13} className="text-muted-foreground shrink-0 -mt-0.5" />
                            <label className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                Calificación
                            </label>
                        </div>
                        <StarSelector value={formRating} onChange={setFormRating} />
                    </div>

                    {/* Etiquetas */}
                    {tagSubjectPool.length > 0 && (
                        <div className="flex-1 flex flex-col gap-1.5 -mt-0.5">
                            <div className="flex items-center gap-1.5">
                                <Tag size={13} className="text-muted-foreground shrink-0" />
                                <label className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                    Etiquetas
                                </label>
                                <TagDropdown
                                    availableSubjects={availableTagSubjects}
                                    accentColor={accentColor}
                                    onSelect={(s) => setFormTags((prev) => [...prev, s])}
                                    disabled={formTags.length >= 3}
                                />
                            </div>
                            {formTags.length > 0 ? (
                                <div className="flex flex-wrap gap-1">
                                    {formTags.map((tag) => {
                                        const tagColor = tag.color ? `#${tag.color}` : (accent ?? '#6366f1')
                                        return (
                                            <TagBadge
                                                key={tag.id}
                                                tag={tag}
                                                color={tagColor}
                                                onRemove={() => setFormTags((prev) => prev.filter((t) => t.id !== tag.id))}
                                            />
                                        )
                                    })}
                                </div>
                            ) : (
                                <p className="text-xs text-muted-foreground italic mt-1">Sin etiquetas</p>
                            )}
                        </div>
                    )}
                </div>

                {/* Comentario */}
                <div className="flex flex-col gap-1.5">
                    <div className="flex items-center gap-1.5">
                        <MessageSquare size={13} className="text-muted-foreground shrink-0" />
                        <label className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                            Comentario
                        </label>
                        <span className="text-[11px] text-muted-foreground ml-auto shrink-0">
                            {formContent.length}/500
                        </span>
                    </div>
                    <textarea
                        value={formContent}
                        onChange={(e) => setFormContent(e.target.value)}
                        maxLength={500}
                        rows={4}
                        placeholder="Contá tu experiencia con este curso..."
                        className={cn(
                            'w-full px-2.5 py-2 text-xs rounded-lg resize-none',
                            'bg-secondary border border-border text-foreground',
                            'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                            'transition-all duration-150 placeholder:text-muted-foreground',
                        )}
                    />
                </div>

                {/* Save / Cancel */}
                <div className="flex items-center justify-end gap-2 pt-1">
                    <button
                        onClick={() => { setShowForm(false); setFormTags([]) }}
                        className="px-3 py-1.5 text-xs font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 touch-manipulation"
                    >
                        Cancelar
                    </button>
                    <button
                        onClick={() => setShowPublishConfirm(true)}
                        disabled={!formRating || !formContent.trim()}
                        style={accent ? { backgroundColor: accent } : undefined}
                        className={cn(
                            'flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-xl',
                            'bg-primary text-primary-foreground hover:bg-primary/90',
                            'transition-all duration-150 disabled:opacity-50 touch-manipulation',
                        )}
                    >
                        <Send size={11} />
                        Publicar
                    </button>
                </div>

                <ConfirmActionModal
                    open={showPublishConfirm}
                    onClose={() => setShowPublishConfirm(false)}
                    onConfirm={handlePublishConfirm}
                    title="¿Publicar esta reseña?"
                    description="Tu reseña será visible para todos los estudiantes. Solo podés publicar una reseña por curso."
                    confirmLabel="Publicar"
                    isLoading={isPublishing}
                />
            </div>
        )
    }

    // ── Reviews list ──────────────────────────────────────────────────────────
    return (
        <div className="flex flex-col">
        {/* Section header with rating + add button */}
        <div className="flex items-center justify-between gap-2 px-4 py-3 border-b border-border">
            <div className="flex items-center gap-2 flex-wrap">
                <span className="text-lg font-bold text-foreground leading-tight">Reseñas</span>
                {averageRating !== null && (
                    <div className="flex items-center gap-1">
                        <span className="text-xs text-muted-foreground">—</span>
                        <span className="text-xs font-semibold text-amber-500">
                            {averageRating.toFixed(1)}
                        </span>
                        <Star size={11} className="text-amber-400 fill-amber-400" />
                    </div>
                )}
            </div>
            <div className="flex items-center gap-1.5">
                <FilterDropdown
                    subjects={tagSubjectPool}
                    selectedIds={filterTags}
                    onToggle={toggleFilterTag}
                    accentColor={accentColor}
                />
                <button
                    onClick={() => setShowForm(true)}
                    style={accent ? { color: accent, backgroundColor: `${accent}1a` } : undefined}
                    className="flex items-center gap-1 px-2.5 py-1 text-xs font-medium rounded-xl bg-primary/10 text-primary hover:bg-primary/15 transition-colors duration-150 touch-manipulation"
                    aria-label="Agregar reseña"
                >
                    <MessageSquarePlus size={12} />
                    Agregar
                </button>
            </div>
        </div>

        <div className="flex flex-col gap-2 px-4 py-3">
            {error ? (
                <div className="flex flex-col items-center gap-2 py-6 text-center">
                    <AlertCircle size={20} className="text-destructive/60" />
                    <p className="text-xs text-muted-foreground">{error}</p>
                </div>
            ) : loading ? (
                <div className="flex flex-col gap-2">
                    {Array.from({ length: 3 }).map((_, i) => (
                        <div key={i} className="h-20 rounded-xl border border-border bg-card/50 animate-pulse" />
                    ))}
                </div>
            ) : reviews.length === 0 ? (
                <div className="flex flex-col items-center gap-2 py-6 text-center">
                    <Star size={20} className="text-muted-foreground/40" />
                    <p className="text-xs text-muted-foreground">
                        Aún no hay reseñas. ¡Sé el primero!
                    </p>
                </div>
            ) : filteredReviews.length === 0 ? (
                <div className="flex flex-col items-center gap-2 py-6 text-center">
                    <SlidersHorizontal size={20} className="text-muted-foreground/40" />
                    <p className="text-xs text-muted-foreground">
                        No hay reseñas con las etiquetas seleccionadas.
                    </p>
                </div>
            ) : (
                <>
                    <div className="flex flex-col gap-2">
                        {filteredReviews.map((review) => (
                            <ReviewCard
                                key={review.id}
                                review={review}
                                accentColor={accentColor}
                                onReport={setReportingReview}
                            />
                        ))}
                    </div>

                    {page + 1 < totalPages && (
                        <button
                            onClick={handleLoadMore}
                            disabled={loadingMore}
                            className={cn(
                                'flex items-center justify-center gap-1.5 w-full py-2 text-xs font-medium rounded-xl border border-border',
                                'bg-card text-foreground hover:border-primary/40 hover:bg-secondary',
                                'transition-all duration-150 disabled:opacity-60 touch-manipulation',
                            )}
                        >
                            {loadingMore && <Loader2 size={11} className="animate-spin" />}
                            Cargar más
                        </button>
                    )}
                </>
            )}

            {/* Report modal — renderizado via portal para escapar el stacking context del sidebar */}
            {reportingReview && createPortal(
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div
                        className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                        onClick={() => setReportingReview(null)}
                    />
                    <div className={cn(
                        'relative z-10 w-full max-w-md',
                        'bg-card border border-border rounded-2xl shadow-2xl',
                        'p-6 flex flex-col gap-4',
                        'animate-in fade-in zoom-in-95 duration-200',
                    )}>
                        <div>
                            <h3 className="font-semibold text-base text-foreground">Reportar reseña</h3>
                            <p className="text-xs text-muted-foreground mt-1">
                                Describí por qué esta reseña viola las normas de la comunidad.
                            </p>
                        </div>
                        <textarea
                            value={reportReason}
                            onChange={(e) => setReportReason(e.target.value)}
                            maxLength={500}
                            rows={4}
                            placeholder="Motivo del reporte (mínimo 10 caracteres)..."
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl resize-none',
                                'bg-secondary border border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                        />
                        <span className="text-[11px] text-muted-foreground text-right -mt-2">
                            {reportReason.length}/500
                        </span>
                        <div className="flex items-center justify-end gap-2">
                            <button
                                onClick={() => setReportingReview(null)}
                                className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 touch-manipulation"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={() => setShowReportConfirm(true)}
                                disabled={reportReason.trim().length < 10}
                                className={cn(
                                    'px-4 py-2.5 text-sm font-medium rounded-xl',
                                    'bg-destructive text-destructive-foreground hover:bg-destructive/90',
                                    'transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed touch-manipulation',
                                )}
                            >
                                Reportar
                            </button>
                        </div>
                    </div>
                </div>,
                document.body,
            )}

            <ConfirmActionModal
                open={showReportConfirm}
                onClose={() => setShowReportConfirm(false)}
                onConfirm={handleReportConfirm}
                title="¿Confirmar reporte?"
                description="Tu reporte será revisado por el equipo de moderación."
                confirmLabel="Confirmar"
                confirmVariant="destructive"
                isLoading={isReporting}
            />
        </div>
        </div>
    )
}
