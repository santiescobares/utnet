import { useEffect, useRef, useState } from 'react'
import { Trash2, X } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { StudyRecordType } from '@/types/studyrecord.types'
import type { CareerDTO } from '@/types/user.types'
import type { SubjectDTO } from '@/types/subject.types'
import { careerService } from '@/services/career.service'
import { subjectService } from '@/services/subject.service'

export interface LibraryFilters {
    types: StudyRecordType[]
    careers: { id: number; name: string }[]
    subjects: { id: number; name: string }[]
}

interface LibraryFilterModalProps {
    open: boolean
    initial: LibraryFilters
    onClose: () => void
    onApply: (filters: LibraryFilters) => void
}

// Colors mirror the backend StudyRecord.Type enum
const TYPE_OPTIONS: { value: StudyRecordType; label: string; color: string }[] = [
    { value: 'SUMMARY',     label: 'Resúmen',                  color: '#0058E6' },
    { value: 'NOTE',        label: 'Apunte',                   color: '#00C4C8' },
    { value: 'BIBLIOGRAPHY',label: 'Material Bibliográfico',   color: '#E60086' },
    { value: 'EXAM_MODEL',  label: 'Modelo de Examen',         color: '#E68D00' },
]

const EMPTY_FILTERS: LibraryFilters = { types: [], careers: [], subjects: [] }

// ── Generic selectable chip ────────────────────────────────────────────────────

interface FilterChipProps {
    label: string
    selected: boolean
    color?: string
    onToggle: () => void
}

function FilterChip({ label, selected, color, onToggle }: FilterChipProps) {
    const [showDelete, setShowDelete] = useState(false)
    const [hovered, setHovered] = useState(false)
    const [needsReenter, setNeedsReenter] = useState(false)
    const btnRef = useRef<HTMLButtonElement>(null)

    useEffect(() => {
        if (!selected) {
            setHovered(false)
            setNeedsReenter(false)
            setShowDelete(false)
        }
    }, [selected])

    useEffect(() => {
        if (!showDelete) return
        const handler = (e: MouseEvent) => {
            if (!btnRef.current?.contains(e.target as Node)) setShowDelete(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [showDelete])

    const showOverlay = selected && ((hovered && !needsReenter) || showDelete)
    const baseColor = color ?? '#0066FF'

    return (
        <button
            ref={btnRef}
            type="button"
            onPointerEnter={(e) => { if (e.pointerType === 'mouse') setHovered(true) }}
            onPointerLeave={(e) => {
                if (e.pointerType === 'mouse') {
                    setHovered(false)
                    setNeedsReenter(false)
                }
            }}
            onClick={(e) => {
                const pointerType = (e.nativeEvent as PointerEvent).pointerType
                if (!selected) {
                    setNeedsReenter(true)
                    onToggle()
                } else if (pointerType === 'mouse') {
                    onToggle()
                } else {
                    if (showDelete) onToggle()
                    else setShowDelete(true)
                }
            }}
            style={selected ? {
                backgroundColor: `${baseColor}22`,
                color: baseColor,
                borderColor: showOverlay ? `${baseColor}66` : `${baseColor}44`,
            } : undefined}
            className={cn(
                'relative inline-flex items-center px-3 py-1.5 rounded-full text-xs font-medium border transition-all duration-150 touch-manipulation',
                selected
                    ? ''
                    : 'bg-secondary border-border text-foreground hover:border-primary/40 hover:bg-primary/5',
            )}
        >
            <span className={cn('transition-opacity duration-150', showOverlay && 'opacity-0')}>
                {label}
            </span>
            {selected && (
                <Trash2
                    size={10}
                    className={cn(
                        'absolute inset-0 m-auto transition-opacity duration-150',
                        showOverlay ? 'opacity-100' : 'opacity-0',
                    )}
                />
            )}
        </button>
    )
}

// ── Main modal ─────────────────────────────────────────────────────────────────

export function LibraryFilterModal({ open, initial, onClose, onApply }: LibraryFilterModalProps) {
    const [local, setLocal] = useState<LibraryFilters>(initial)
    const [careers, setCareers] = useState<CareerDTO[]>([])
    const [subjects, setSubjects] = useState<SubjectDTO[]>([])

    useEffect(() => {
        if (open) setLocal(initial)
    }, [open]) // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        if (!open || careers.length > 0) return
        careerService.getAll().then(setCareers).catch(() => {})
        subjectService.getAll().then(setSubjects).catch(() => {})
    }, [open]) // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        if (!open) return
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handler)
        return () => document.removeEventListener('keydown', handler)
    }, [open, onClose])

    useEffect(() => {
        if (open) document.body.style.overflow = 'hidden'
        else document.body.style.overflow = ''
        return () => { document.body.style.overflow = '' }
    }, [open])

    if (!open) return null

    const toggleType = (t: StudyRecordType) =>
        setLocal((p) => ({
            ...p,
            types: p.types.includes(t) ? p.types.filter((x) => x !== t) : [...p.types, t],
        }))

    const toggleCareer = (career: CareerDTO) =>
        setLocal((p) => ({
            ...p,
            careers: p.careers.some((c) => c.id === career.id)
                ? p.careers.filter((c) => c.id !== career.id)
                : [...p.careers, { id: career.id, name: career.name }],
        }))

    const toggleSubject = (subject: SubjectDTO) =>
        setLocal((p) => ({
            ...p,
            subjects: p.subjects.some((s) => s.id === subject.id)
                ? p.subjects.filter((s) => s.id !== subject.id)
                : [...p.subjects, { id: subject.id, name: subject.name }],
        }))

    const visibleSubjects = local.careers.length > 0
        ? subjects.filter((s) => s.careers.some((c) => local.careers.some((lc) => lc.id === c.id)))
        : subjects

    const hasActiveFilters = local.types.length > 0 || local.careers.length > 0 || local.subjects.length > 0

    return (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
            <div
                className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                onClick={onClose}
            />

            <div className={cn(
                'relative z-10 w-full sm:max-w-lg',
                'bg-card border border-border',
                'rounded-t-2xl sm:rounded-2xl shadow-2xl',
                'flex flex-col max-h-[85dvh]',
                'animate-in fade-in slide-in-from-bottom-4 sm:zoom-in-95 duration-200',
            )}>
                {/* Header */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-border shrink-0">
                    <h2 className="font-semibold text-base text-foreground">Seleccionar filtros</h2>
                    <button
                        onClick={onClose}
                        className="p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150"
                    >
                        <X size={16} />
                    </button>
                </div>

                {/* Body */}
                <div className="flex-1 overflow-y-auto px-5 py-4 flex flex-col gap-6">
                    <section className="flex flex-col gap-3">
                        <h3 className="text-sm font-semibold text-foreground">Tipo</h3>
                        <div className="flex flex-wrap gap-2">
                            {TYPE_OPTIONS.map((opt) => (
                                <FilterChip
                                    key={opt.value}
                                    label={opt.label}
                                    selected={local.types.includes(opt.value)}
                                    color={opt.color}
                                    onToggle={() => toggleType(opt.value)}
                                />
                            ))}
                        </div>
                    </section>

                    <section className="flex flex-col gap-3">
                        <h3 className="text-sm font-semibold text-foreground">Carrera</h3>
                        {careers.length === 0 ? (
                            <p className="text-xs text-muted-foreground">Cargando carreras…</p>
                        ) : (
                            <div className="flex flex-wrap gap-2">
                                {careers.map((c) => (
                                    <FilterChip
                                        key={c.id}
                                        label={c.name}
                                        selected={local.careers.some((lc) => lc.id === c.id)}
                                        color={`#${c.color}`}
                                        onToggle={() => toggleCareer(c)}
                                    />
                                ))}
                            </div>
                        )}
                    </section>

                    <section className="flex flex-col gap-3">
                        <h3 className="text-sm font-semibold text-foreground">Materia</h3>
                        {subjects.length === 0 ? (
                            <p className="text-xs text-muted-foreground">Cargando materias…</p>
                        ) : visibleSubjects.length === 0 ? (
                            <p className="text-xs text-muted-foreground">No hay materias para las carreras seleccionadas.</p>
                        ) : (
                            <div className="flex flex-wrap gap-2">
                                {visibleSubjects.map((s) => (
                                    <FilterChip
                                        key={s.id}
                                        label={s.name}
                                        selected={local.subjects.some((ls) => ls.id === s.id)}
                                        color={s.color ? `#${s.color}` : undefined}
                                        onToggle={() => toggleSubject(s)}
                                    />
                                ))}
                            </div>
                        )}
                    </section>
                </div>

                {/* Footer */}
                <div className="flex items-center justify-between px-5 py-4 border-t border-border shrink-0">
                    {/* Limpiar filtros — red border, light content, disabled when empty */}
                    <button
                        onClick={() => setLocal(EMPTY_FILTERS)}
                        disabled={!hasActiveFilters}
                        className={cn(
                            'px-4 py-2.5 text-sm font-medium rounded-xl border transition-colors duration-150',
                            hasActiveFilters
                                ? 'border-destructive/50 text-destructive/70 hover:bg-destructive/10 hover:border-destructive hover:text-destructive'
                                : 'border-border/50 text-muted-foreground/40 cursor-not-allowed',
                        )}
                    >
                        Limpiar filtros
                    </button>
                    <div className="flex items-center gap-2">
                        <button
                            onClick={onClose}
                            className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150"
                        >
                            Cancelar
                        </button>
                        <button
                            onClick={() => { onApply(local); onClose() }}
                            className="px-4 py-2.5 text-sm font-medium rounded-xl bg-primary text-primary-foreground hover:bg-primary/90 transition-all duration-150"
                        >
                            Aplicar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}
