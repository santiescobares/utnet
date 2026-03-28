import { useEffect, useRef, useState } from 'react'
import { CalendarPlus, Check, ChevronDown, X } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CourseEventCreateDTO, CourseEventTag } from '@/types/course.types'
import { COURSE_EVENT_TAGS } from '@/types/course.types'
import { ConfirmActionModal } from '@/components/ui/ConfirmActionModal'

interface EventFormModalProps {
    open: boolean
    courseId: number
    minDate: string  // ISO "YYYY-MM-DD"
    maxDate: string  // ISO "YYYY-MM-DD"
    initialDate?: string  // ISO "YYYY-MM-DD" — pre-fills the date field
    isLoading?: boolean
    onClose: () => void
    onSubmit: (dto: CourseEventCreateDTO) => Promise<void>
}

export function EventFormModal({
    open,
    courseId,
    minDate,
    maxDate,
    initialDate,
    isLoading = false,
    onClose,
    onSubmit,
}: EventFormModalProps) {
    const today = new Date().toISOString().split('T')[0]

    const [date, setDate] = useState(initialDate ?? today)
    const [description, setDescription] = useState('')
    const [tag, setTag] = useState<CourseEventTag | null>(null)
    const [tagDropdownOpen, setTagDropdownOpen] = useState(false)
    const [showConfirm, setShowConfirm] = useState(false)
    const [isSubmitting, setIsSubmitting] = useState(false)

    const dateRef = useRef<HTMLInputElement>(null)
    const tagBtnRef = useRef<HTMLButtonElement>(null)
    const tagDropdownRef = useRef<HTMLDivElement>(null)
    const [tagDropPos, setTagDropPos] = useState<{ top: number; left: number; width: number } | null>(null)

    // Reset form on open
    useEffect(() => {
        if (open) {
            setDate(initialDate ?? today)
            setDescription('')
            setTag(null)
            setTagDropdownOpen(false)
            setShowConfirm(false)
            setTimeout(() => dateRef.current?.focus(), 50)
        }
    }, [open, initialDate]) // eslint-disable-line react-hooks/exhaustive-deps

    // Close on Escape
    useEffect(() => {
        if (!open) return
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') { if (tagDropdownOpen) setTagDropdownOpen(false); else onClose() } }
        document.addEventListener('keydown', handler)
        return () => document.removeEventListener('keydown', handler)
    }, [open, onClose, tagDropdownOpen])

    // Lock body scroll
    useEffect(() => {
        if (open) document.body.style.overflow = 'hidden'
        else document.body.style.overflow = ''
        return () => { document.body.style.overflow = '' }
    }, [open])

    // Close tag dropdown on outside click
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

    const handleTagToggle = () => {
        if (!tagDropdownOpen && tagBtnRef.current) {
            const rect = tagBtnRef.current.getBoundingClientRect()
            setTagDropPos({ top: rect.bottom + 4, left: rect.left, width: rect.width })
        }
        setTagDropdownOpen((v) => !v)
    }

    const selectedTagEntry = tag ? COURSE_EVENT_TAGS.find((t) => t.value === tag) : null

    const isValid = date >= minDate && date <= maxDate && description.trim().length >= 1 && tag !== null

    const handlePublishConfirm = async () => {
        if (!isValid) return
        setIsSubmitting(true)
        try {
            await onSubmit({ courseId, date, description: description.trim(), tag: tag! })
            setShowConfirm(false)
            onClose()
        } finally {
            setIsSubmitting(false)
        }
    }

    if (!open) return null

    return (
        <>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                {/* Backdrop */}
                <div
                    className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                    onClick={onClose}
                />

                {/* Panel */}
                <div className={cn(
                    'relative z-10 w-full max-w-md',
                    'bg-card border border-border rounded-2xl shadow-2xl',
                    'p-6 flex flex-col gap-5',
                    'animate-in fade-in zoom-in-95 duration-200',
                )}>
                    {/* Close */}
                    <button
                        onClick={onClose}
                        disabled={isLoading}
                        className="absolute top-4 right-4 p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                    >
                        <X size={16} />
                    </button>

                    {/* Header */}
                    <div className="flex items-center gap-3 pr-6">
                        <div className="w-9 h-9 rounded-xl bg-primary/10 flex items-center justify-center shrink-0">
                            <CalendarPlus size={18} className="text-primary" />
                        </div>
                        <div>
                            <h3 className="font-semibold text-base text-foreground">Crear evento</h3>
                            <p className="text-xs text-muted-foreground mt-0.5">Se publicará en el calendario del curso</p>
                        </div>
                    </div>

                    {/* Date + Tag row */}
                    <div className="flex gap-3">
                        <div className="flex flex-col gap-1.5 w-[9.5rem] shrink-0">
                            <label className="text-sm font-medium text-foreground">Fecha</label>
                            <input
                                ref={dateRef}
                                type="date"
                                value={date}
                                min={minDate}
                                max={maxDate}
                                onChange={(e) => setDate(e.target.value)}
                                className={cn(
                                    'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground',
                                    'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                    'transition-all duration-150',
                                    '[color-scheme:light] dark:[color-scheme:dark]',
                                )}
                            />
                        </div>
                        <div className="flex flex-col gap-1.5 flex-1 min-w-0">
                            <label className="text-sm font-medium text-foreground">Etiqueta</label>
                            <button
                                ref={tagBtnRef}
                                type="button"
                                onClick={handleTagToggle}
                                className={cn(
                                    'w-full flex items-center gap-2 px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border',
                                    'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                    'transition-all duration-150',
                                )}
                            >
                                {selectedTagEntry ? (
                                    <>
                                        <span
                                            className="w-2.5 h-2.5 rounded-full shrink-0"
                                            style={{ backgroundColor: selectedTagEntry.color }}
                                        />
                                        <span className="flex-1 text-left text-foreground truncate">{selectedTagEntry.label}</span>
                                    </>
                                ) : (
                                    <span className="flex-1 text-left text-muted-foreground truncate">Seleccionar...</span>
                                )}
                                <ChevronDown size={14} className={cn('text-muted-foreground shrink-0 transition-transform duration-150', tagDropdownOpen && 'rotate-180')} />
                            </button>
                        </div>
                    </div>

                    {/* Description field */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">Descripción</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            maxLength={500}
                            rows={4}
                            placeholder="Describí el evento..."
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl resize-none',
                                'bg-secondary border border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                        />
                        <span className="text-[11px] text-muted-foreground text-right">
                            {description.length}/500
                        </span>
                    </div>

                    {/* Actions */}
                    <div className="flex items-center justify-end gap-2">
                        <button
                            onClick={onClose}
                            disabled={isSubmitting}
                            className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                        >
                            Cancelar
                        </button>
                        <button
                            onClick={() => setShowConfirm(true)}
                            disabled={!isValid || isSubmitting}
                            className={cn(
                                'px-4 py-2.5 text-sm font-medium rounded-xl',
                                'bg-primary text-primary-foreground hover:bg-primary/90',
                                'transition-all duration-150 disabled:opacity-40 disabled:cursor-not-allowed',
                            )}
                        >
                            Publicar
                        </button>
                    </div>
                </div>
            </div>

            {/* Tag dropdown (fixed, outside panel) */}
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
                            onClick={() => { setTag(entry.value); setTagDropdownOpen(false) }}
                            className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-foreground hover:bg-secondary transition-colors touch-manipulation"
                        >
                            <span
                                className="w-2.5 h-2.5 rounded-full shrink-0"
                                style={{ backgroundColor: entry.color }}
                            />
                            <span className="flex-1 text-left">{entry.label}</span>
                            {tag === entry.value && <Check size={13} className="text-primary shrink-0" />}
                        </button>
                    ))}
                </div>
            )}

            <ConfirmActionModal
                open={showConfirm}
                onClose={() => setShowConfirm(false)}
                onConfirm={handlePublishConfirm}
                title="¿Publicar este evento?"
                description="El evento será visible para todos los estudiantes del curso."
                confirmLabel="Publicar"
                isLoading={isSubmitting}
            />
        </>
    )
}
