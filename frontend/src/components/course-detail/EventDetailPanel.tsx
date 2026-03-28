import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router'
import { CalendarDays, Check, ChevronDown, Clock, MoreHorizontal, History, Pencil, Trash2, Save, X, ChevronLeft, ChevronRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CourseEventDTO, CourseEventTag } from '@/types/course.types'
import { COURSE_EVENT_TAGS } from '@/types/course.types'
import type { UserSnapshotDTO } from '@/types/user.types'
import { ConfirmActionModal } from '@/components/ui/ConfirmActionModal'

function eventColor(event: CourseEventDTO, fallback?: string): string {
    return event.tagColor ? `#${event.tagColor}` : fallback ?? 'hsl(var(--primary))'
}

function SnapshotAvatar({ user, size = 16 }: { user: UserSnapshotDTO; size?: number }) {
    const [imgError, setImgError] = useState(false)
    const initials = `${user.firstName[0] ?? ''}${user.lastName[0] ?? ''}`.toUpperCase()
    if (user.profilePictureURL && !imgError) {
        return (
            <img
                src={user.profilePictureURL}
                alt=""
                onError={() => setImgError(true)}
                style={{ width: size, height: size }}
                className="rounded-full object-cover shrink-0 ring-1 ring-border"
            />
        )
    }
    return (
        <div
            style={{ width: size, height: size, fontSize: Math.max(7, size * 0.45) }}
            className="rounded-full bg-primary/10 text-primary flex items-center justify-center shrink-0 font-semibold select-none leading-none"
        >
            {initials}
        </div>
    )
}

const MONTH_ABBR = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic']

function formatDateShort(dateStr: string): string {
    const [, m, d] = dateStr.split('-').map(Number)
    return `${d} ${MONTH_ABBR[m - 1]}`
}

function formatTime(timeStr: string | null): string | null {
    if (!timeStr) return null
    const [h, min] = timeStr.split(':')
    return `${h}:${min}`
}

function formatUpdatedAt(isoStr: string): string {
    const d = new Date(isoStr)
    const now = new Date()
    const isToday = d.toDateString() === now.toDateString()
    const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    if (isToday) return time
    return `${String(d.getDate()).padStart(2, '0')} ${MONTH_ABBR[d.getMonth()]} ${time}`
}

// ── Single event card ──────────────────────────────────────────────────────────

interface EventCardProps {
    event: CourseEventDTO
    accent?: string
    onEdit: (id: number, dto: { description: string; tag: CourseEventTag | null }) => Promise<void>
    onDelete: (id: number) => Promise<void>
}

function EventCard({ event, accent, onEdit, onDelete }: EventCardProps) {
    const [menuOpen, setMenuOpen] = useState(false)
    const [menuPos, setMenuPos] = useState<{ top: number; right: number } | null>(null)
    const [isEditing, setIsEditing] = useState(false)
    const [editDescription, setEditDescription] = useState('')
    const [editTag, setEditTag] = useState<CourseEventTag | null>(null)
    const [tagDropdownOpen, setTagDropdownOpen] = useState(false)
    const [tagDropPos, setTagDropPos] = useState<{ top: number; left: number; width: number } | null>(null)
    const [showSaveConfirm, setShowSaveConfirm] = useState(false)
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
    const [showHistoryModal, setShowHistoryModal] = useState(false)
    const [isSaving, setIsSaving] = useState(false)
    const [isDeleting, setIsDeleting] = useState(false)
    const buttonRef = useRef<HTMLButtonElement>(null)
    const dropdownRef = useRef<HTMLDivElement>(null)
    const tagBtnRef = useRef<HTMLButtonElement>(null)
    const tagDropdownRef = useRef<HTMLDivElement>(null)
    const textareaRef = useRef<HTMLTextAreaElement>(null)

    useEffect(() => {
        if (!menuOpen) return
        const handler = (e: MouseEvent) => {
            if (
                !buttonRef.current?.contains(e.target as Node) &&
                !dropdownRef.current?.contains(e.target as Node)
            ) setMenuOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [menuOpen])

    useEffect(() => {
        if (!tagDropdownOpen) return
        const handler = (e: MouseEvent) => {
            if (
                !tagBtnRef.current?.contains(e.target as Node) &&
                !tagDropdownRef.current?.contains(e.target as Node)
            ) setTagDropdownOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [tagDropdownOpen])

    const handleMenuToggle = () => {
        if (!menuOpen && buttonRef.current) {
            const rect = buttonRef.current.getBoundingClientRect()
            setMenuPos({ top: rect.bottom + 4, right: window.innerWidth - rect.right })
        }
        setMenuOpen((v) => !v)
    }

    const handleTagToggle = () => {
        if (!tagDropdownOpen && tagBtnRef.current) {
            const rect = tagBtnRef.current.getBoundingClientRect()
            setTagDropPos({ top: rect.bottom + 4, left: rect.left, width: rect.width })
        }
        setTagDropdownOpen((v) => !v)
    }

    useEffect(() => {
        if (isEditing) textareaRef.current?.focus()
    }, [isEditing])

    const handleStartEdit = () => {
        setEditDescription(event.description)
        setEditTag(event.tag ?? null)
        setIsEditing(true)
        setMenuOpen(false)
    }

    const hasChanges = editDescription.trim() !== '' && (
        editDescription !== event.description || editTag !== (event.tag ?? null)
    )

    const handleSaveConfirm = async () => {
        setIsSaving(true)
        try {
            await onEdit(event.id, { description: editDescription, tag: editTag })
            setIsEditing(false)
            setShowSaveConfirm(false)
        } finally {
            setIsSaving(false)
        }
    }

    const handleDeleteConfirm = async () => {
        setIsDeleting(true)
        try {
            await onDelete(event.id)
            setShowDeleteConfirm(false)
        } finally {
            setIsDeleting(false)
        }
    }

    const timeStart = formatTime(event.startTime)
    const timeEnd = formatTime(event.endTime)
    const editor = event.lastEditor ?? event.createdBy
    const selectedTagEntry = editTag ? COURSE_EVENT_TAGS.find((t) => t.value === editTag) : null

    return (
        <>
        <div className="flex rounded-xl border border-border bg-card animate-in fade-in slide-in-from-top-1 duration-200">
            {/* Left accent bar — uses event's own tag color */}
            <div
                className="w-1 shrink-0 rounded-l-xl"
                style={{ backgroundColor: eventColor(event, accent) }}
            />

            <div className="flex-1 flex flex-col gap-1.5 px-3 py-3 min-w-0">
                {/* Optional time */}
                {(timeStart || timeEnd) && (
                    <p className="text-xs text-muted-foreground flex items-center gap-1">
                        <Clock size={10} />
                        {timeStart && timeEnd ? `${timeStart} – ${timeEnd}` : timeStart ?? timeEnd}
                    </p>
                )}

                {/* Description / edit area */}
                {isEditing ? (
                    <div className="flex flex-col gap-2">
                        <textarea
                            ref={textareaRef}
                            value={editDescription}
                            onChange={(e) => setEditDescription(e.target.value)}
                            maxLength={500}
                            rows={3}
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl resize-none',
                                'bg-secondary border border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                            placeholder="Descripción del evento..."
                        />
                        {/* Tag selector */}
                        <button
                            ref={tagBtnRef}
                            type="button"
                            onClick={handleTagToggle}
                            className={cn(
                                'w-full flex items-center gap-2 px-3 py-1.5 text-xs rounded-xl border bg-secondary border-border',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 touch-manipulation',
                            )}
                        >
                            {selectedTagEntry ? (
                                <>
                                    <span className="w-2 h-2 rounded-full shrink-0" style={{ backgroundColor: selectedTagEntry.color }} />
                                    <span className="flex-1 text-left text-foreground">{selectedTagEntry.label}</span>
                                </>
                            ) : (
                                <span className="flex-1 text-left text-muted-foreground">Sin tipo</span>
                            )}
                            <ChevronDown size={12} className={cn('text-muted-foreground shrink-0 transition-transform duration-150', tagDropdownOpen && 'rotate-180')} />
                        </button>
                        <div className="flex items-center justify-between gap-2">
                            <span className="text-[11px] text-muted-foreground">{editDescription.length}/500</span>
                            <div className="flex items-center gap-1.5">
                                <button
                                    onClick={() => setIsEditing(false)}
                                    className="px-3 py-1.5 text-xs font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 touch-manipulation"
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={() => setShowSaveConfirm(true)}
                                    disabled={!hasChanges}
                                    className={cn(
                                        'flex items-center gap-1 px-2.5 py-1.5 text-xs font-medium rounded-xl',
                                        'bg-primary text-primary-foreground hover:bg-primary/90',
                                        'transition-all disabled:opacity-40 disabled:cursor-not-allowed touch-manipulation',
                                    )}
                                >
                                    <Save size={11} /> Guardar
                                </button>
                            </div>
                        </div>
                    </div>
                ) : (
                    <>
                        {event.tag && event.tag !== 'OTHER' && (
                            <span className="text-[10px] font-semibold tracking-widest text-muted-foreground uppercase">
                                {COURSE_EVENT_TAGS.find((t) => t.value === event.tag)?.label}
                            </span>
                        )}
                        <p className="text-sm text-foreground leading-relaxed whitespace-pre-line">
                            {event.description}
                        </p>
                    </>
                )}

                {/* Footer */}
                {!isEditing && (
                    <div className="flex items-center gap-1.5 mt-0.5">
                        <SnapshotAvatar user={editor} size={16} />
                        <p className="text-[11px] text-muted-foreground">
                            Última modificación:{' '}
                            <Link
                                to={`/users/${editor.id}`}
                                className="hover:underline hover:text-foreground transition-colors"
                            >
                                {editor.firstName} {editor.lastName}
                            </Link>
                            {' — '}{formatUpdatedAt(event.updatedAt)}
                        </p>
                    </div>
                )}
            </div>

            {/* 3-dot menu */}
            {!isEditing && (
                <div className="flex items-start pt-2 pr-2">
                    <button
                        ref={buttonRef}
                        onClick={handleMenuToggle}
                        className="min-w-[32px] min-h-[32px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors touch-manipulation"
                        aria-label="Opciones"
                    >
                        <MoreHorizontal size={14} />
                    </button>
                    {menuOpen && menuPos && (
                        <div
                            ref={dropdownRef}
                            style={{ top: menuPos.top, right: menuPos.right }}
                            className={cn(
                                'fixed z-50 w-44',
                                'bg-card border border-border rounded-xl shadow-lg py-1',
                                'animate-in fade-in zoom-in-95 duration-150',
                            )}
                        >
                            <button
                                onClick={() => { setShowHistoryModal(true); setMenuOpen(false) }}
                                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-foreground hover:bg-secondary transition-colors touch-manipulation"
                            >
                                <History size={13} className="text-muted-foreground shrink-0" /> Historial
                            </button>
                            <button
                                onClick={handleStartEdit}
                                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-foreground hover:bg-secondary transition-colors touch-manipulation"
                            >
                                <Pencil size={13} className="text-muted-foreground shrink-0" /> Editar
                            </button>
                            <div className="h-px bg-border mx-2 my-0.5" />
                            <button
                                onClick={() => { setShowDeleteConfirm(true); setMenuOpen(false) }}
                                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-destructive hover:bg-destructive/10 transition-colors touch-manipulation"
                            >
                                <Trash2 size={13} className="shrink-0" /> Eliminar
                            </button>
                        </div>
                    )}
                </div>
            )}

            {/* Modals */}
            <ConfirmActionModal
                open={showSaveConfirm}
                onClose={() => setShowSaveConfirm(false)}
                onConfirm={handleSaveConfirm}
                title="¿Guardar los cambios?"
                description="Estás por modificar la descripción de este evento. Esta acción quedará registrada."
                confirmLabel="Guardar"
                isLoading={isSaving}
            />
            <ConfirmActionModal
                open={showDeleteConfirm}
                onClose={() => setShowDeleteConfirm(false)}
                onConfirm={handleDeleteConfirm}
                title="¿Eliminar este evento?"
                description="Estás por eliminar permanentemente este evento del calendario del curso."
                confirmLabel="Eliminar"
                confirmVariant="destructive"
                isLoading={isDeleting}
            />
            {showHistoryModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div
                        className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                        onClick={() => setShowHistoryModal(false)}
                    />
                    <div className="relative z-10 w-full max-w-sm bg-card border border-border rounded-2xl shadow-2xl p-6 flex flex-col items-center gap-3 animate-in fade-in zoom-in-95 duration-200">
                        <button
                            onClick={() => setShowHistoryModal(false)}
                            className="absolute top-4 right-4 min-w-[36px] min-h-[36px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors"
                        >
                            <X size={16} />
                        </button>
                        <History size={28} className="text-muted-foreground" />
                        <div className="text-center">
                            <p className="font-semibold text-foreground">Historial de cambios</p>
                            <p className="text-sm text-muted-foreground mt-1">Próximamente</p>
                        </div>
                    </div>
                </div>
            )}
        </div>
        {/* Tag dropdown (fixed, escapes card stacking context) */}
        {tagDropdownOpen && tagDropPos && (
            <div
                ref={tagDropdownRef}
                style={{ top: tagDropPos.top, left: tagDropPos.left, width: tagDropPos.width }}
                className={cn(
                    'fixed z-[60] bg-card border border-border rounded-xl shadow-lg py-1',
                    'animate-in fade-in zoom-in-95 duration-150',
                )}
            >
                {COURSE_EVENT_TAGS.map((entry) => (
                    <button
                        key={entry.value}
                        type="button"
                        onClick={() => { setEditTag(entry.value); setTagDropdownOpen(false) }}
                        className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-foreground hover:bg-secondary transition-colors touch-manipulation"
                    >
                        <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: entry.color }} />
                        <span className="flex-1 text-left">{entry.label}</span>
                        {editTag === entry.value && <Check size={13} className="text-primary shrink-0" />}
                    </button>
                ))}
            </div>
        )}
        </>
    )
}

// ── Upcoming event card ────────────────────────────────────────────────────────

function UpcomingEventCard({ event, accent, onSelectDate }: {
    event: CourseEventDTO
    accent?: string
    onSelectDate?: (date: string) => void
}) {
    const timeStart = formatTime(event.startTime)
    return (
        <button
            type="button"
            onClick={() => onSelectDate?.(event.date)}
            className={cn(
                'flex h-[88px] w-full rounded-xl border border-border bg-card shadow-sm overflow-hidden text-left',
                'hover:border-primary/40 hover:bg-secondary/50',
                'transition-colors duration-150 touch-manipulation',
            )}
        >
            <div className="w-1.5 shrink-0" style={{ backgroundColor: eventColor(event, accent) }} />
            <div className="flex flex-col gap-0.5 px-2.5 py-2.5 min-w-0 overflow-hidden flex-1">
                <span className="text-[11px] font-semibold text-muted-foreground leading-tight">
                    {formatDateShort(event.date)}
                </span>
                {timeStart && (
                    <span className="flex items-center gap-0.5 text-[11px] text-muted-foreground">
                        <Clock size={10} />{timeStart}
                    </span>
                )}
                <p className="text-[11px] text-foreground leading-snug line-clamp-2 break-words">
                    {event.description}
                </p>
                {event.tag && event.tag !== 'OTHER' && (
                    <span className="text-[9px] font-semibold tracking-widest text-muted-foreground/70 uppercase leading-tight mt-auto pt-1">
                        {COURSE_EVENT_TAGS.find((t) => t.value === event.tag)?.label}
                    </span>
                )}
            </div>
        </button>
    )
}

// ── Upcoming events carousel ───────────────────────────────────────────────────

function UpcomingEventsCarousel({ events, loading, accent, onSelectDate }: {
    events: CourseEventDTO[]
    loading?: boolean
    accent?: string
    onSelectDate?: (date: string) => void
}) {
    const [page, setPage] = useState(0)
    const [itemsPerPage, setItemsPerPage] = useState(3)

    useEffect(() => {
        const update = () => setItemsPerPage(window.innerWidth >= 1024 ? 5 : 3)
        update()
        window.addEventListener('resize', update)
        return () => window.removeEventListener('resize', update)
    }, [])

    useEffect(() => { setPage(0) }, [events])

    const totalPages = Math.ceil(events.length / itemsPerPage)
    const canPrev = page > 0
    const canNext = page < totalPages - 1
    const visibleEvents = events.slice(page * itemsPerPage, (page + 1) * itemsPerPage)
    // pad with nulls to keep grid stable
    const slots = Array.from({ length: itemsPerPage }, (_, i) => visibleEvents[i] ?? null)

    if (loading) {
        return (
            <div className="grid grid-cols-3 lg:grid-cols-5 gap-2">
                {Array.from({ length: itemsPerPage }).map((_, i) => (
                    <div key={i} className="h-[88px] rounded-xl border border-border bg-card/50 animate-pulse" />
                ))}
            </div>
        )
    }

    if (events.length === 0) return null

    return (
        <div className="flex flex-col gap-2">
            <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-muted-foreground">
                    Próximos eventos{totalPages > 1 ? ` · ${page + 1}/${totalPages}` : ''}
                </span>
                {totalPages > 1 && (
                    <div className="flex items-center gap-1">
                        <button
                            type="button"
                            onClick={() => setPage((p) => p - 1)}
                            disabled={!canPrev}
                            className="min-w-[28px] min-h-[28px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary disabled:opacity-30 transition-colors touch-manipulation"
                        >
                            <ChevronLeft size={14} />
                        </button>
                        <button
                            type="button"
                            onClick={() => setPage((p) => p + 1)}
                            disabled={!canNext}
                            className="min-w-[28px] min-h-[28px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary disabled:opacity-30 transition-colors touch-manipulation"
                        >
                            <ChevronRight size={14} />
                        </button>
                    </div>
                )}
            </div>
            <div className="grid grid-cols-3 lg:grid-cols-5 gap-2">
                {slots.map((event, i) =>
                    event ? (
                        <UpcomingEventCard key={event.id} event={event} accent={accent} onSelectDate={onSelectDate} />
                    ) : (
                        <div key={`empty-${i}`} className="h-[88px]" />
                    )
                )}
            </div>
        </div>
    )
}

// ── Main panel ─────────────────────────────────────────────────────────────────

interface EventDetailPanelProps {
    events: CourseEventDTO[]
    selectedDate?: string | null
    upcomingEvents?: CourseEventDTO[]
    upcomingEventsLoading?: boolean
    onSelectDate?: (date: string) => void
    accentColor?: string
    onEdit: (id: number, dto: { description: string; tag: CourseEventTag | null }) => Promise<void>
    onDelete: (id: number) => Promise<void>
    className?: string
}

export function EventDetailPanel({
    events,
    selectedDate,
    upcomingEvents,
    upcomingEventsLoading,
    onSelectDate,
    accentColor,
    onEdit,
    onDelete,
    className,
}: EventDetailPanelProps) {
    const accent = accentColor ? `#${accentColor}` : undefined

    return (
        <div className={cn('flex flex-col', className)}>
            {/* Header */}
            <div className="flex items-center gap-2 px-4 py-3 border-b border-border shrink-0">
                <span className="text-sm font-semibold text-foreground">Eventos</span>
                {selectedDate && (
                    <span className="text-xs text-muted-foreground">
                        — {formatDateShort(selectedDate)}
                    </span>
                )}
            </div>

            {/* Body */}
            {!selectedDate ? (
                <div className="flex flex-1 flex-col items-center justify-center gap-2 text-center px-4 py-4">
                    <CalendarDays size={20} className="text-muted-foreground/40" />
                    <p className="text-sm text-muted-foreground leading-snug">
                        Seleccioná un día<br />del calendario
                    </p>
                </div>
            ) : events.length === 0 ? (
                <div className="flex flex-col flex-1">
                    <div className="flex-1 flex flex-col items-center justify-center gap-2 text-center px-4 py-6">
                        <CalendarDays size={20} className="text-muted-foreground/40" />
                        <p className="text-sm text-muted-foreground">Sin eventos para este día</p>
                    </div>
                    {(upcomingEventsLoading || (upcomingEvents && upcomingEvents.length > 0)) && (
                        <div className="px-4 pb-4">
                            <UpcomingEventsCarousel
                                events={upcomingEvents ?? []}
                                loading={upcomingEventsLoading}
                                accent={accent}
                                onSelectDate={onSelectDate}
                            />
                        </div>
                    )}
                </div>
            ) : (
                <div className="flex flex-col gap-2 px-4 py-3 flex-1 overflow-y-auto scrollbar-thin">
                    {events.map((event) => (
                        <EventCard
                            key={event.id}
                            event={event}
                            accent={accent}
                            onEdit={onEdit}
                            onDelete={onDelete}
                        />
                    ))}
                </div>
            )}
        </div>
    )
}
