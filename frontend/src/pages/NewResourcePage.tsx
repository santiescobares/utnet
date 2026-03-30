import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'
import { Check, ChevronDown, Loader2, X } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { subjectService } from '@/services/subject.service'
import { studyRecordService } from '@/services/studyRecord.service'
import type { SubjectDTO } from '@/types/subject.types'
import type { StudyRecordType } from '@/types/studyrecord.types'

// ── Constants ─────────────────────────────────────────────────────────────────

const TYPE_OPTIONS: { value: StudyRecordType; label: string; color: string }[] = [
    { value: 'SUMMARY',      label: 'Resúmen',                color: '#0058E6' },
    { value: 'NOTE',         label: 'Apunte',                 color: '#00C4C8' },
    { value: 'BIBLIOGRAPHY', label: 'Material Bibliográfico', color: '#E60086' },
    { value: 'EXAM_MODEL',   label: 'Modelo de Examen',       color: '#E68D00' },
]

const ALLOWED_EXTENSIONS = ['pdf', 'doc', 'docx', 'png', 'jpg', 'jpeg']
const MAX_FILE_SIZE_MB = 30

const INPUT_BASE =
    'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground ' +
    'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50 ' +
    'transition-all duration-150'

// ── TypeDropdown ──────────────────────────────────────────────────────────────

interface TypeDropdownProps {
    value: StudyRecordType | null
    onChange: (v: StudyRecordType) => void
    error?: boolean
}

function TypeDropdown({ value, onChange, error }: TypeDropdownProps) {
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
                className={cn(
                    INPUT_BASE,
                    'flex items-center justify-between cursor-pointer pr-2.5',
                    error && 'border-destructive focus:ring-destructive/30',
                )}
            >
                {selected ? (
                    <span className="text-foreground">{selected.label}</span>
                ) : (
                    <span className="text-muted-foreground">Seleccioná un tipo…</span>
                )}
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
        // Strip any non-alphanumeric character silently as the user types
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

// ── SubjectsInput ─────────────────────────────────────────────────────────────

const MAX_SUBJECTS = 5

interface SubjectsInputProps {
    allSubjects: SubjectDTO[]
    selected: SubjectDTO[]
    onChange: (subjects: SubjectDTO[]) => void
    error?: boolean
    onBlur?: () => void
}

function SubjectsInput({ allSubjects, selected, onChange, error, onBlur }: SubjectsInputProps) {
    const [query, setQuery]   = useState('')
    const [open, setOpen]     = useState(false)
    const containerRef        = useRef<HTMLDivElement>(null)
    const inputRef            = useRef<HTMLInputElement>(null)

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (!containerRef.current?.contains(e.target as Node)) {
                setOpen(false)
                setQuery('')
            }
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    const suggestions = query.trim().length === 0
        ? []
        : allSubjects
            .filter((s) => !selected.some((sel) => sel.id === s.id))
            .filter((s) =>
                s.name.toLowerCase().includes(query.toLowerCase()) ||
                s.shortName.toLowerCase().includes(query.toLowerCase()),
            )
            .slice(0, 8)

    const addSubject = (s: SubjectDTO) => {
        if (selected.length >= MAX_SUBJECTS) return
        onChange([...selected, s])
        setQuery('')
        setOpen(false)
        inputRef.current?.focus()
    }

    const removeSubject = (id: number) => {
        onChange(selected.filter((s) => s.id !== id))
    }

    const isFull = selected.length >= MAX_SUBJECTS

    return (
        <div ref={containerRef} className="relative">
            <div
                onClick={() => !isFull && inputRef.current?.focus()}
                className={cn(
                    'flex flex-wrap gap-1.5 px-3 py-2 min-h-[42px] rounded-xl border bg-secondary border-border',
                    'focus-within:ring-2 focus-within:ring-primary/30 focus-within:border-primary/50',
                    'transition-all duration-150',
                    !isFull && 'cursor-text',
                    error && 'border-destructive focus-within:ring-destructive/30',
                )}
            >
                {selected.map((s) => (
                    <span
                        key={s.id}
                        className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-medium border border-primary/20"
                    >
                        {s.name}
                        <button
                            type="button"
                            onClick={(e) => { e.stopPropagation(); removeSubject(s.id) }}
                            className="text-primary/60 hover:text-primary transition-colors duration-100"
                            aria-label={`Eliminar ${s.name}`}
                        >
                            <X size={10} />
                        </button>
                    </span>
                ))}
                {!isFull && (
                    <input
                        ref={inputRef}
                        value={query}
                        onChange={(e) => { setQuery(e.target.value); setOpen(true) }}
                        onFocus={() => setOpen(true)}
                        onBlur={() => { onBlur?.() }}
                        onKeyDown={(e) => {
                            if (e.key === 'Escape') { setOpen(false); setQuery('') }
                            if (e.key === 'Backspace' && query === '' && selected.length > 0) {
                                removeSubject(selected[selected.length - 1].id)
                            }
                            if (e.key === 'Enter' && suggestions.length > 0) {
                                e.preventDefault()
                                addSubject(suggestions[0])
                            }
                        }}
                        placeholder={selected.length === 0 ? 'Buscá una materia…' : ''}
                        className="flex-1 min-w-[120px] bg-transparent text-sm text-foreground outline-none placeholder:text-muted-foreground"
                    />
                )}
            </div>

            {open && suggestions.length > 0 && (
                <div className="absolute top-full left-0 right-0 mt-1 z-20 bg-card border border-border rounded-xl shadow-lg py-1 overflow-hidden max-h-52 overflow-y-auto">
                    {suggestions.map((s) => (
                        <button
                            key={s.id}
                            type="button"
                            onMouseDown={(e) => { e.preventDefault(); addSubject(s) }}
                            className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-left hover:bg-secondary transition-colors duration-100"
                        >
                            {s.color && (
                                <span
                                    className="w-2 h-2 rounded-full shrink-0"
                                    style={{ backgroundColor: `#${s.color}` }}
                                />
                            )}
                            <span className="text-foreground">{s.name}</span>
                            <span className="text-muted-foreground text-xs ml-auto shrink-0">{s.shortName}</span>
                        </button>
                    ))}
                </div>
            )}
        </div>
    )
}

// ── Page ──────────────────────────────────────────────────────────────────────

export function NewResourcePage() {
    // Form fields
    const [title, setTitle]                       = useState('')
    const [type, setType]                         = useState<StudyRecordType | null>(null)
    const [description, setDescription]           = useState('')
    const [selectedSubjects, setSelectedSubjects] = useState<SubjectDTO[]>([])
    const [tags, setTags]                         = useState<string[]>([])
    const [file, setFile]                         = useState<File | null>(null)
    const [agreed, setAgreed]                     = useState(false)

    // Async data
    const [allSubjects, setAllSubjects] = useState<SubjectDTO[]>([])

    // UI state
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [submitted, setSubmitted]       = useState(false)
    const [fileError, setFileError]       = useState('')

    // Blur tracking
    const touchedRef = useRef<Set<string>>(new Set())
    const [, forceUpdate] = useState(0)
    const touchField = (field: string) => {
        touchedRef.current.add(field)
        forceUpdate((n) => n + 1)
    }

    const navigate     = useNavigate()
    const fileInputRef = useRef<HTMLInputElement>(null)

    useEffect(() => {
        subjectService.getAll()
            .then((data) => setAllSubjects([...data].sort((a, b) => a.sortPosition - b.sortPosition)))
            .catch(() => toast.error('No se pudo cargar la información necesaria.'))
    }, []) // eslint-disable-line react-hooks/exhaustive-deps

    const getErrors = () => ({
        title:
            title.trim().length < 5   ? 'El título debe tener al menos 5 caracteres.' :
            title.trim().length > 120 ? 'El título no puede superar los 120 caracteres.' : '',
        type:        !type                      ? 'Seleccioná un tipo de recurso.' : '',
        description:
            description.trim().length < 10   ? 'La descripción debe tener al menos 10 caracteres.' :
            description.trim().length > 2000 ? 'La descripción no puede superar los 2000 caracteres.' : '',
        subjects: selectedSubjects.length === 0 ? 'Seleccioná al menos una materia.' : '',
        file:     !file                         ? 'Seleccioná un archivo.'           : fileError,
        agreed:   !agreed                       ? 'required'                         : '',
    })

    const errors      = getErrors()
    const isFormValid = Object.values(errors).every((e) => e === '')

    const showErr = (field: keyof ReturnType<typeof getErrors>) =>
        field !== 'agreed' &&
        (submitted || touchedRef.current.has(field)) &&
        errors[field] !== ''

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selected = e.target.files?.[0] ?? null
        setFileError('')
        if (!selected) { setFile(null); return }

        const ext = selected.name.split('.').pop()?.toLowerCase() ?? ''
        if (!ALLOWED_EXTENSIONS.includes(ext)) {
            setFileError(`Formato no permitido. Aceptados: ${ALLOWED_EXTENSIONS.join(', ')}.`)
            setFile(null); e.target.value = ''; return
        }
        if (selected.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
            setFileError(`El archivo supera el límite de ${MAX_FILE_SIZE_MB} MB.`)
            setFile(null); e.target.value = ''; return
        }
        setFile(selected)
    }

    const handleSubmit = async () => {
        setSubmitted(true)
        if (!isFormValid || isSubmitting) return
        setIsSubmitting(true)
        try {
            const result = await studyRecordService.create(
                {
                    subjectIds:  selectedSubjects.map((s) => s.id),
                    title:       title.trim(),
                    description: description.trim(),
                    type:        type!,
                    tags:        tags.length > 0 ? tags : undefined,
                },
                file!,
            )
            toast.success('Recurso publicado correctamente')
            navigate(`/library/${result.slug}`)
        } catch {
            toast.error('No se pudo publicar el recurso. Intentá de nuevo.')
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="px-4 sm:px-6 pt-6 pb-10">
            <div className="max-w-2xl mx-auto flex flex-col gap-6">

                {/* Header */}
                <div>
                    <h1 className="text-2xl font-bold text-foreground">Nuevo Recurso</h1>
                    <p className="text-sm text-muted-foreground mt-1">
                        Compartí material de estudio con la comunidad
                    </p>
                </div>

                {/* Form card */}
                <div className="bg-card border border-border rounded-2xl p-6 flex flex-col gap-4">

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
                            <TypeDropdown
                                value={type}
                                onChange={(v) => { setType(v); touchField('type') }}
                                error={showErr('type')}
                            />
                            {showErr('type') && (
                                <p className="text-xs text-destructive">{errors.type}</p>
                            )}
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

                    {/* ROW 3: Materias */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Materias <span className="text-destructive">*</span>
                        </label>
                        <SubjectsInput
                            allSubjects={allSubjects}
                            selected={selectedSubjects}
                            onChange={setSelectedSubjects}
                            error={showErr('subjects')}
                            onBlur={() => touchField('subjects')}
                        />
                        {showErr('subjects') ? (
                            <p className="text-xs text-destructive">{errors.subjects}</p>
                        ) : (
                            <p className="text-[11px] text-muted-foreground">
                                Escribí para buscar. Podés agregar hasta {MAX_SUBJECTS} materias, mínimo 1 requerida.
                            </p>
                        )}
                    </div>

                    {/* ROW 4: Etiquetas */}
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

                    {/* ROW 5: Archivo */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Recurso <span className="text-destructive">*</span>
                        </label>
                        <div className="flex items-center gap-3">
                            <span className={cn('text-sm flex-1 truncate', file ? 'text-foreground' : 'text-muted-foreground')}>
                                {file ? `Recurso: ${file.name}` : 'Ningún archivo seleccionado'}
                            </span>
                            <button
                                type="button"
                                onClick={() => fileInputRef.current?.click()}
                                className="shrink-0 px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150"
                            >
                                Seleccionar
                            </button>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept={ALLOWED_EXTENSIONS.map((e) => `.${e}`).join(',')}
                                onChange={handleFileChange}
                                className="hidden"
                            />
                        </div>
                        {(showErr('file') || fileError) && (
                            <p className="text-xs text-destructive">{errors.file || fileError}</p>
                        )}
                        <p className="text-[11px] text-muted-foreground">
                            {ALLOWED_EXTENSIONS.join(', ')} · Máx {MAX_FILE_SIZE_MB} MB
                        </p>
                    </div>

                    {/* ROW 6: Toggle declaración */}
                    <div className="flex flex-col gap-1.5">
                        <label className="flex items-start gap-3 cursor-pointer">
                            <button
                                type="button"
                                role="switch"
                                aria-checked={agreed}
                                onClick={() => { setAgreed((p) => !p); touchField('agreed') }}
                                className={cn(
                                    'relative mt-0.5 inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent',
                                    'transition-colors duration-200',
                                    'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:ring-offset-2 focus:ring-offset-card',
                                    agreed ? 'bg-primary' : 'bg-muted-foreground/30',
                                )}
                            >
                                <span
                                    className={cn(
                                        'pointer-events-none block h-4 w-4 rounded-full bg-white shadow-sm',
                                        'transform transition-transform duration-200',
                                        agreed ? 'translate-x-4' : 'translate-x-0',
                                    )}
                                />
                            </button>
                            <span className="text-xs text-muted-foreground leading-relaxed">
                                Declaro poseer los derechos de propiedad intelectual sobre este documento
                                y acepto los Términos y Condiciones de publicación de UTNet.
                            </span>
                        </label>
                    </div>

                    {/* ROW 7: Botones */}
                    <div className="flex items-center justify-end gap-2 pt-2">
                        <button
                            type="button"
                            onClick={() => navigate(-1)}
                            disabled={isSubmitting}
                            className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                        >
                            Cancelar
                        </button>
                        <button
                            type="button"
                            onClick={handleSubmit}
                            disabled={!isFormValid || isSubmitting}
                            className={cn(
                                'px-4 py-2.5 text-sm font-medium rounded-xl flex items-center gap-2',
                                'bg-primary text-primary-foreground hover:bg-primary/90',
                                'transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed',
                            )}
                        >
                            {isSubmitting && <Loader2 size={14} className="animate-spin" />}
                            {isSubmitting ? 'Publicando…' : 'Publicar'}
                        </button>
                    </div>

                </div>
            </div>
        </div>
    )
}
