import { useEffect, useRef, useState } from 'react'
import { Check, ChevronDown, Loader2, Trash2, X } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { studyRecordService } from '@/services/studyRecord.service'
import { ConfirmActionModal } from '@/components/ui/ConfirmActionModal'
import type { StudyRecordDTO, StudyRecordType } from '@/types/studyrecord.types'

// ── Constants ─────────────────────────────────────────────────────────────────

const TYPE_OPTIONS: { value: StudyRecordType; label: string; color: string }[] = [
    { value: 'SUMMARY',      label: 'Resúmen',                color: '#0058E6' },
    { value: 'NOTE',         label: 'Apunte',                 color: '#00C4C8' },
    { value: 'BIBLIOGRAPHY', label: 'Material Bibliográfico', color: '#E60086' },
    { value: 'EXAM_MODEL',   label: 'Modelo de Examen',       color: '#E68D00' },
]

const INPUT_BASE =
    'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground ' +
    'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50 ' +
    'transition-all duration-150'

// ── TypeDropdown ──────────────────────────────────────────────────────────────

interface TypeDropdownProps {
    value: StudyRecordType
    onChange: (v: StudyRecordType) => void
}

function TypeDropdown({ value, onChange }: TypeDropdownProps) {
    const [open, setOpen] = useState(false)
    const containerRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (!containerRef.current?.contains(e.target as Node)) setOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    const selected = TYPE_OPTIONS.find((o) => o.value === value)

    return (
        <div ref={containerRef} className="relative">
            <button
                type="button"
                onClick={() => setOpen((p) => !p)}
                className={cn(INPUT_BASE, 'flex items-center justify-between cursor-pointer pr-2.5')}
            >
                <span className="text-foreground">{selected?.label ?? value}</span>
                <ChevronDown
                    size={15}
                    className={cn(
                        'shrink-0 text-muted-foreground transition-transform duration-150',
                        open && 'rotate-180',
                    )}
                />
            </button>

            {open && (
                <div className="absolute top-full left-0 right-0 mt-1 z-20 bg-card border border-border rounded-xl shadow-lg py-1 overflow-hidden">
                    {TYPE_OPTIONS.map((opt) => (
                        <button
                            key={opt.value}
                            type="button"
                            onClick={() => { onChange(opt.value); setOpen(false) }}
                            className={cn(
                                'w-full flex items-center gap-2.5 px-3 py-2 text-sm hover:bg-secondary transition-colors duration-100',
                                value === opt.value && 'bg-secondary',
                            )}
                        >
                            <span
                                className="w-2 h-2 rounded-full shrink-0"
                                style={{ backgroundColor: opt.color }}
                            />
                            <span className="text-foreground">{opt.label}</span>
                            {value === opt.value && (
                                <Check size={13} className="ml-auto text-primary shrink-0" />
                            )}
                        </button>
                    ))}
                </div>
            )}
        </div>
    )
}

// ── TagsInput ─────────────────────────────────────────────────────────────────

interface TagsInputProps {
    tags: string[]
    onChange: (tags: string[]) => void
}

function TagsInput({ tags, onChange }: TagsInputProps) {
    const [inputValue, setInputValue] = useState('')
    const [shaking, setShaking] = useState(false)
    const inputRef = useRef<HTMLInputElement>(null)

    useEffect(() => {
        if (!shaking) return
        const t = setTimeout(() => setShaking(false), 350)
        return () => clearTimeout(t)
    }, [shaking])

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const filtered = e.target.value.replace(/[^a-z0-9]/gi, '').toLowerCase()
        setInputValue(filtered)
    }

    const tryAddTag = () => {
        const raw = inputValue.trim()
        const valid = raw.length > 0 && !tags.includes(raw) && tags.length < 15
        if (valid) {
            onChange([...tags, raw])
            setInputValue('')
        } else if (raw.length > 0) {
            setShaking(true)
        }
    }

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Backspace' && inputValue === '') {
            onChange(tags.slice(0, -1))
            return
        }
        if (e.key === ' ' || e.key === 'Enter') {
            e.preventDefault()
            tryAddTag()
        }
    }

    return (
        <div
            onClick={() => inputRef.current?.focus()}
            className={cn(
                'flex flex-wrap gap-1.5 px-3 py-2 min-h-[42px] rounded-xl border bg-secondary border-border',
                'focus-within:ring-2 focus-within:ring-primary/30 focus-within:border-primary/50',
                'transition-all duration-150 cursor-text',
                shaking && 'animate-shake',
            )}
        >
            {tags.map((tag) => (
                <span
                    key={tag}
                    className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-medium border border-primary/20"
                >
                    {tag}
                    <button
                        type="button"
                        onClick={(e) => { e.stopPropagation(); onChange(tags.filter((t) => t !== tag)) }}
                        className="text-primary/60 hover:text-primary transition-colors duration-100"
                        aria-label={`Eliminar etiqueta ${tag}`}
                    >
                        <X size={10} />
                    </button>
                </span>
            ))}
            <input
                ref={inputRef}
                value={inputValue}
                onChange={handleChange}
                onKeyDown={handleKeyDown}
                disabled={tags.length >= 15}
                placeholder={tags.length === 0 ? 'Ej: calculo, parcial…' : ''}
                className="flex-1 min-w-[80px] bg-transparent text-sm text-foreground outline-none placeholder:text-muted-foreground"
            />
        </div>
    )
}

// ── Modal ─────────────────────────────────────────────────────────────────────

interface StudyRecordEditModalProps {
    record: StudyRecordDTO
    onClose: () => void
    onSaved: (updated: StudyRecordDTO) => void
    onDeleted: () => void
}

export function StudyRecordEditModal({ record, onClose, onSaved, onDeleted }: StudyRecordEditModalProps) {
    // Form fields — pre-populated from record
    const [title, setTitle]             = useState(record.title)
    const [type, setType]               = useState<StudyRecordType>(record.type)
    const [description, setDescription] = useState(record.description)
    const [tags, setTags]               = useState<string[]>([...record.tags])
    const [makeHidden, setMakeHidden]   = useState(record.hidden)

    // UI state
    const [isSubmitting, setIsSubmitting]       = useState(false)
    const [submitted, setSubmitted]             = useState(false)
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
    const [isDeleting, setIsDeleting]           = useState(false)

    // Blur tracking
    const touchedRef = useRef<Set<string>>(new Set())
    const [, forceUpdate] = useState(0)
    const touchField = (field: string) => {
        touchedRef.current.add(field)
        forceUpdate((n) => n + 1)
    }

    // Close on Escape
    useEffect(() => {
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handler)
        return () => document.removeEventListener('keydown', handler)
    }, [onClose])

    // Lock body scroll while modal is open
    useEffect(() => {
        document.body.style.overflow = 'hidden'
        return () => { document.body.style.overflow = '' }
    }, [])

    const getErrors = () => ({
        title:
            title.trim().length < 5   ? 'El título debe tener al menos 5 caracteres.' :
            title.trim().length > 120 ? 'El título no puede superar los 120 caracteres.' : '',
        description:
            description.trim().length < 10   ? 'La descripción debe tener al menos 10 caracteres.' :
            description.trim().length > 2000 ? 'La descripción no puede superar los 2000 caracteres.' : '',
    })

    const errors      = getErrors()
    const isFormValid = Object.values(errors).every((e) => e === '')

    const showErr = (field: keyof ReturnType<typeof getErrors>) =>
        (submitted || touchedRef.current.has(field)) && errors[field] !== ''

    // Detect actual changes vs original record
    const hasChanged =
        title.trim()         !== record.title       ||
        type                 !== record.type        ||
        description.trim()   !== record.description ||
        JSON.stringify(tags) !== JSON.stringify(record.tags) ||
        makeHidden           !== record.hidden

    const handleDelete = async () => {
        setIsDeleting(true)
        try {
            await studyRecordService.deleteRecord(record.id)
            toast.success('Recurso eliminado.')
            onDeleted()
        } catch {
            toast.error('No se pudo eliminar el recurso. Intentá de nuevo.')
        } finally {
            setIsDeleting(false)
        }
    }

    const handleSave = async () => {
        setSubmitted(true)
        if (!isFormValid || !hasChanged || isSubmitting) return
        setIsSubmitting(true)
        try {
            const updated = await studyRecordService.update(record.id, {
                title:       title.trim(),
                type,
                description: description.trim(),
                tags,
                hidden:      makeHidden,
            })
            toast.success('Recurso actualizado correctamente.')
            onSaved(updated)
        } catch {
            toast.error('No se pudo guardar los cambios. Intentá de nuevo.')
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div
            className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4"
            onClick={(e) => { if (e.target === e.currentTarget) onClose() }}
        >
            {/* Backdrop */}
            <div className="absolute inset-0 bg-background/70 backdrop-blur-sm" />

            {/* Panel */}
            <div className="relative z-10 w-full sm:max-w-2xl max-h-[92dvh] sm:max-h-[90dvh] flex flex-col bg-card border border-border rounded-t-2xl sm:rounded-2xl shadow-xl overflow-hidden">

                {/* Header */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-border shrink-0">
                    <div>
                        <h2 className="text-base font-bold text-foreground">Editar Recurso</h2>
                        <p className="text-xs text-muted-foreground mt-0.5">
                            Modificá la información de la publicación
                        </p>
                    </div>
                    <button
                        onClick={onClose}
                        className="p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors"
                        aria-label="Cerrar"
                    >
                        <X size={18} />
                    </button>
                </div>

                {/* Scrollable body */}
                <div className="flex-1 overflow-y-auto px-5 py-4 flex flex-col gap-4">

                    {/* ROW 1: Título + Tipo */}
                    <div className="flex flex-col gap-5 sm:flex-row sm:gap-4">
                        {/* Título */}
                        <div className="flex flex-col gap-1.5 sm:flex-[2]">
                            <label className="text-sm font-medium text-foreground">
                                Título <span className="text-destructive">*</span>
                            </label>
                            <input
                                type="text"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                onBlur={() => touchField('title')}
                                maxLength={120}
                                placeholder="Ej: Resumen Análisis Matemático I"
                                className={cn(INPUT_BASE, showErr('title') && 'border-destructive focus:ring-destructive/30')}
                            />
                            <div className="flex items-start justify-between gap-2">
                                {showErr('title')
                                    ? <p className="text-xs text-destructive">{errors.title}</p>
                                    : <span />
                                }
                                <span className="text-[11px] text-muted-foreground shrink-0">{title.length} / 120</span>
                            </div>
                        </div>

                        {/* Tipo */}
                        <div className="flex flex-col gap-1.5 sm:flex-[1]">
                            <label className="text-sm font-medium text-foreground">
                                Tipo <span className="text-destructive">*</span>
                            </label>
                            <TypeDropdown value={type} onChange={setType} />
                        </div>
                    </div>

                    {/* ROW 2: Descripción */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Descripción <span className="text-destructive">*</span>
                        </label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            onBlur={() => touchField('description')}
                            maxLength={2000}
                            rows={5}
                            placeholder="Describí brevemente el contenido del material…"
                            className={cn(
                                INPUT_BASE, 'resize-none',
                                showErr('description') && 'border-destructive focus:ring-destructive/30',
                            )}
                        />
                        <div className="flex items-start justify-between gap-2">
                            {showErr('description')
                                ? <p className="text-xs text-destructive">{errors.description}</p>
                                : <span />
                            }
                            <span className="text-[11px] text-muted-foreground shrink-0">{description.length} / 2000</span>
                        </div>
                    </div>

                    {/* ROW 3: Etiquetas */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Etiquetas{' '}
                            <span className="text-muted-foreground font-normal">(opcional)</span>
                        </label>
                        <TagsInput tags={tags} onChange={setTags} />
                        <p className="text-[11px] text-muted-foreground">
                            Presioná Espacio o Enter para agregar. Solo letras y números, máximo 15 etiquetas.
                        </p>
                    </div>
                </div>

                {/* Footer */}
                <div className="shrink-0 flex items-center gap-3 px-5 py-4 border-t border-border">
                    {/* Delete button */}
                    <button
                        type="button"
                        onClick={() => setShowDeleteConfirm(true)}
                        disabled={isSubmitting || isDeleting}
                        className="flex items-center gap-1.5 px-3 py-2.5 text-sm font-medium rounded-xl text-destructive border border-destructive/30 hover:bg-destructive/10 transition-colors duration-150 disabled:opacity-40"
                    >
                        <Trash2 size={14} />
                        Eliminar
                    </button>
                    
                    {/* Hacer privado / público toggle */}
                    <label className="flex items-center gap-2 cursor-pointer select-none">
                        <button
                            type="button"
                            role="switch"
                            aria-checked={makeHidden}
                            onClick={() => setMakeHidden((v) => !v)}
                            className={cn(
                                'relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent',
                                'transition-colors duration-200',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:ring-offset-2 focus:ring-offset-card',
                                makeHidden ? 'bg-primary' : 'bg-muted-foreground/30',
                            )}
                        >
                            <span
                                className={cn(
                                    'pointer-events-none block h-4 w-4 rounded-full bg-white shadow-sm',
                                    'transform transition-transform duration-200',
                                    makeHidden ? 'translate-x-4' : 'translate-x-0',
                                )}
                            />
                        </button>
                        <span className="text-xs text-muted-foreground">
                            {makeHidden ? 'Hacer público' : 'Hacer privado'}
                        </span>
                    </label>

                    {/* Spacer */}
                    <div className="flex-1" />

                    {/* Action buttons */}
                    <div className="flex items-center gap-2">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={isSubmitting || isDeleting}
                            className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                        >
                            Cancelar
                        </button>
                        <button
                            type="button"
                            onClick={handleSave}
                            disabled={!hasChanged || !isFormValid || isSubmitting || isDeleting}
                            className={cn(
                                'px-4 py-2.5 text-sm font-medium rounded-xl flex items-center gap-2',
                                'bg-primary text-primary-foreground hover:bg-primary/90',
                                'transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed',
                            )}
                        >
                            {isSubmitting && <Loader2 size={14} className="animate-spin" />}
                            {isSubmitting ? 'Guardando…' : 'Guardar'}
                        </button>
                    </div>
                </div>
            </div>

            <ConfirmActionModal
                open={showDeleteConfirm}
                onClose={() => setShowDeleteConfirm(false)}
                onConfirm={handleDelete}
                title="¿Eliminar recurso?"
                description="Esta acción eliminará permanentemente la publicación y su archivo. No se puede deshacer."
                confirmLabel="Eliminar"
                confirmVariant="destructive"
                isLoading={isDeleting}
            />
        </div>
    )
}
